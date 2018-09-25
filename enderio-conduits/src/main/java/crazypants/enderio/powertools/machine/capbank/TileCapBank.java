package crazypants.enderio.powertools.machine.capbank;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.capacitor.ICapacitorData;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.TileEntityEio;
import crazypants.enderio.base.capacitor.DefaultCapacitorData;
import crazypants.enderio.base.conduit.ConduitUtil;
import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.base.config.config.DiagnosticsConfig;
import crazypants.enderio.base.machine.gui.IPowerBarData;
import crazypants.enderio.base.machine.interfaces.IIoConfigurable;
import crazypants.enderio.base.machine.modes.IoMode;
import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.power.ILegacyPowerReceiver;
import crazypants.enderio.base.power.IPowerInterface;
import crazypants.enderio.base.power.IPowerStorage;
import crazypants.enderio.base.power.PowerHandlerUtil;
import crazypants.enderio.powertools.init.PowerToolObject;
import crazypants.enderio.powertools.machine.capbank.network.CapBankClientNetwork;
import crazypants.enderio.powertools.machine.capbank.network.ClientNetworkManager;
import crazypants.enderio.powertools.machine.capbank.network.EnergyReceptor;
import crazypants.enderio.powertools.machine.capbank.network.ICapBankNetwork;
import crazypants.enderio.powertools.machine.capbank.network.NetworkUtil;
import crazypants.enderio.powertools.machine.capbank.packet.PacketNetworkIdRequest;
import crazypants.enderio.util.NbtValue;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.util.NBTAction;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Storable
public class TileCapBank extends TileEntityEio implements ILegacyPowerReceiver, IIoConfigurable, IPowerStorage, IPaintable.IPaintableTileEntity, IPowerBarData {

  @Store
  private EnumMap<EnumFacing, IoMode> faceModes;
  @Store
  private EnumMap<EnumFacing, InfoDisplayType> faceDisplayTypes;

  @Store({ NBTAction.SAVE, NBTAction.CLIENT })
  private int energyStored;
  @Store
  private int maxInput = -1;
  @Store
  private int maxOutput = -1;

  @Store
  private @Nonnull RedstoneControlMode inputControlMode = RedstoneControlMode.IGNORE;
  @Store
  private @Nonnull RedstoneControlMode outputControlMode = RedstoneControlMode.IGNORE;

  private boolean redstoneStateDirty = true;

  private final @Nonnull List<EnergyReceptor> receptors = new ArrayList<EnergyReceptor>();
  private boolean receptorsDirty = true;

  private ICapBankNetwork network;

  // Client side reference to look up network state
  private int networkId = -1;
  private int idRequestTimer = 0;

  private boolean displayTypesDirty;
  private boolean revalidateDisplayTypes;
  private int lastComparatorState;

  public @Nonnull CapBankType getType() {
    if (!hasWorld()) {
      // needed when loading from invalid NBT
      return CapBankType.VIBRANT;
    }
    return CapBankType.getTypeFromMeta(getBlockMetadata());
  }

  public void onNeighborBlockChange(Block blockId) {
    redstoneStateDirty = true;
    revalidateDisplayTypes = true;
    // directly call updateReceptors() to work around issue #1433
    updateReceptors();
  }

  // ---------- Multiblock

  @SideOnly(Side.CLIENT)
  public void setNetworkId(int networkId) {
    this.networkId = networkId;
    if (networkId != -1) {
      ClientNetworkManager.getInstance().addToNetwork(networkId, this);
    }
  }

  @SideOnly(Side.CLIENT)
  public int getNetworkId() {
    return networkId;
  }

  public ICapBankNetwork getNetwork() {
    return network;
  }

  public boolean setNetwork(ICapBankNetwork network) {
    this.network = network;
    return true;
  }

  public boolean canConnectTo(@Nonnull TileCapBank cap) {
    CapBankType myType = getType();
    return myType.isMultiblock() && myType == cap.getType();
  }

  @Override
  public void onChunkUnload() {
    if (network != null) {
      network.destroyNetwork();
    }
  }

  @Override
  public void invalidate() {
    super.invalidate();
    if (network != null) {
      network.destroyNetwork();
    }
  }

  @Override
  public void doUpdate() {
    if (world.isRemote) {
      if (networkId == -1) {
        if (idRequestTimer <= 0) {
          PacketHandler.INSTANCE.sendToServer(new PacketNetworkIdRequest(this));
          idRequestTimer = 5;
        } else {
          --idRequestTimer;
        }
      }
      return;
    }

    if (network == null) {
      NetworkUtil.ensureValidNetwork(this);
      if (network == null) {
        return;
      }
    }

    if (redstoneStateDirty) {
      int sig = ConduitUtil.isBlockIndirectlyGettingPoweredIfLoaded(world, getPos());
      boolean recievingSignal = sig > 0;
      network.updateRedstoneSignal(this, recievingSignal);
      redstoneStateDirty = false;
    }

    if (receptorsDirty) {
      updateReceptors();
    }
    if (revalidateDisplayTypes) {
      validateDisplayTypes();
      revalidateDisplayTypes = false;
    }
    if (displayTypesDirty) {
      displayTypesDirty = false;
      IBlockState bs = world.getBlockState(pos);
      world.notifyBlockUpdate(pos, bs, bs, 3);
    }

    // update any comparators, since they don't check themselves
    int comparatorState = getComparatorOutput();
    if (lastComparatorState != comparatorState) {
      world.updateComparatorOutputLevel(getPos(), getBlockType());
      lastComparatorState = comparatorState;
    }
  }

  // ---------- IO

  @Override
  public @Nonnull IoMode toggleIoModeForFace(@Nullable EnumFacing faceHit) {
    if (faceHit == null) {
      return IoMode.NONE;
    }
    IPowerInterface rec = getReceptorForFace(faceHit);
    IoMode curMode = getIoMode(faceHit);
    if (curMode == IoMode.PULL) {
      setIoMode(faceHit, IoMode.PUSH, true);
      return IoMode.PUSH;
    }
    if (curMode == IoMode.PUSH) {
      setIoMode(faceHit, IoMode.DISABLED, true);
      return IoMode.DISABLED;
    }
    if (curMode == IoMode.DISABLED) {
      if (rec == null || rec.getProvider() instanceof IConduitBundle) {
        setIoMode(faceHit, IoMode.NONE, true);
        return IoMode.NONE;
      }
    }
    setIoMode(faceHit, IoMode.PULL, true);
    return IoMode.PULL;
  }

  @Override
  public boolean supportsMode(@Nullable EnumFacing faceHit, @Nullable IoMode mode) {
    if (faceHit == null || mode == null) {
      return false;
    }
    IPowerInterface rec = getReceptorForFace(faceHit);
    if (mode == IoMode.NONE) {
      return rec == null || rec.getProvider() instanceof IConduitBundle;
    }
    return true;
  }

  @Override
  public void setIoMode(@Nullable EnumFacing faceHit, @Nullable IoMode mode) {
    if (faceHit != null && mode != null) {
      setIoMode(faceHit, mode, true);
    }
  }

  public void setIoMode(@Nonnull EnumFacing faceHit, @Nonnull IoMode mode, boolean updateReceptors) {
    if (mode == IoMode.NONE) {
      if (faceModes == null) {
        return;
      }
      faceModes.remove(faceHit);
      if (faceModes.isEmpty()) {
        faceModes = null;
      }
    } else {
      if (faceModes == null) {
        faceModes = new EnumMap<EnumFacing, IoMode>(EnumFacing.class);
      }
      faceModes.put(faceHit, mode);
    }
    markDirty();
    if (updateReceptors) {
      validateModeForReceptor(faceHit);
      receptorsDirty = true;
    }
    if (hasWorld()) {
      IBlockState bs = world.getBlockState(pos);
      world.notifyBlockUpdate(pos, bs, bs, 3);
      // TODO what is this? world.notifyBlockOfStateChange(getPos(), getBlockType());
      world.notifyNeighborsOfStateChange(pos, getBlockType(), true);
    }
  }

  public void setDefaultIoMode(@Nonnull EnumFacing faceHit) {
    EnergyReceptor er = getEnergyReceptorForFace(faceHit);
    if (er == null || er.getConduit() != null) {
      setIoMode(faceHit, IoMode.NONE);
    } else if (er.getReceptor().canReceive()) {
      setIoMode(faceHit, IoMode.PUSH);
    } else {
      setIoMode(faceHit, IoMode.PULL);
    }
  }

  @Override
  public void clearAllIoModes() {
    if (network != null) {
      for (TileCapBank cb : network.getMembers()) {
        cb.doClearAllIoModes();
      }
    } else {
      doClearAllIoModes();
    }
  }

  private void doClearAllIoModes() {
    for (EnumFacing dir : EnumFacing.values()) {
      setDefaultIoMode(NullHelper.notnullJ(dir, "Enum.values()"));
    }
  }

  @Override
  public @Nonnull IoMode getIoMode(@Nullable EnumFacing face) {
    if (faceModes == null) {
      return IoMode.NONE;
    }
    IoMode res = faceModes.get(face);
    if (res == null) {
      return IoMode.NONE;
    }
    return res;
  }

  // ----- Info Display

  public boolean hasDisplayTypes() {
    return faceDisplayTypes != null && !faceDisplayTypes.isEmpty();
  }

  public @Nonnull InfoDisplayType getDisplayType(EnumFacing face) {
    if (faceDisplayTypes == null) {
      return InfoDisplayType.NONE;
    }
    InfoDisplayType res = faceDisplayTypes.get(face);
    return res == null ? InfoDisplayType.NONE : res;
  }

  public void setDisplayType(EnumFacing face, InfoDisplayType type) {
    setDisplayType(face, type, true);
  }

  public void setDisplayType(EnumFacing face, InfoDisplayType type, boolean markDirty) {
    if (type == null) {
      type = InfoDisplayType.NONE;
    }
    if (faceDisplayTypes == null && type == InfoDisplayType.NONE) {
      return;
    }
    InfoDisplayType cur = getDisplayType(face);
    if (cur == type) {
      return;
    }

    if (faceDisplayTypes == null) {
      faceDisplayTypes = new EnumMap<EnumFacing, InfoDisplayType>(EnumFacing.class);
    }

    if (type == InfoDisplayType.NONE) {
      faceDisplayTypes.remove(face);
    } else {
      faceDisplayTypes.put(face, type);
    }

    if (faceDisplayTypes.isEmpty()) {
      faceDisplayTypes = null;
    }
    if (markDirty) {
      displayTypesDirty = true;
      markDirty();
    }
    invalidateDisplayInfoCache();
  }

  public void validateDisplayTypes() {
    if (faceDisplayTypes == null) {
      return;
    }
    List<EnumFacing> reset = new ArrayList<EnumFacing>();
    for (Entry<EnumFacing, InfoDisplayType> entry : faceDisplayTypes.entrySet()) {
      IBlockState bs = world.getBlockState(getPos().offset(NullHelper.notnullJ(entry.getKey(), "EnumMap.getKey()")));
      if (bs.isOpaqueCube() || bs.getBlock() == PowerToolObject.block_cap_bank.getBlock()) {
        reset.add(entry.getKey());
      }
    }
    for (EnumFacing dir : reset) {
      setDisplayType(dir, InfoDisplayType.NONE);
      setDefaultIoMode(NullHelper.notnullJ(dir, "Enum.values()"));
    }
  }

  private void invalidateDisplayInfoCache() {
    if (network != null) {
      network.invalidateDisplayInfoCache();
    }
  }

  // ----------- rendering

  @Override
  public boolean shouldRenderInPass(int pass) {
    if (faceDisplayTypes == null) {
      return false;
    }
    return pass == 0;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull AxisAlignedBB getRenderBoundingBox() {
    if (!getType().isMultiblock() || !(network instanceof CapBankClientNetwork)) {
      return super.getRenderBoundingBox();
    }

    int xCoord = getPos().getX();
    int yCoord = getPos().getY();
    int zCoord = getPos().getZ();

    int minX = xCoord;
    int minY = yCoord;
    int minZ = zCoord;
    int maxX = minX + 1;
    int maxY = minY + 1;
    int maxZ = minZ + 1;

    if (faceDisplayTypes != null) {
      CapBankClientNetwork cn = (CapBankClientNetwork) network;

      if (faceDisplayTypes.get(EnumFacing.NORTH) == InfoDisplayType.IO) {
        CapBankClientNetwork.IOInfo info = cn.getIODisplayInfo(xCoord, yCoord, zCoord, EnumFacing.NORTH);
        maxX = Math.max(maxX, xCoord + info.width);
        minY = Math.min(minY, yCoord + 1 - info.height);
      }
      if (faceDisplayTypes.get(EnumFacing.SOUTH) == InfoDisplayType.IO) {
        CapBankClientNetwork.IOInfo info = cn.getIODisplayInfo(xCoord, yCoord, zCoord, EnumFacing.SOUTH);
        minX = Math.min(minX, xCoord + 1 - info.width);
        minY = Math.min(minY, yCoord + 1 - info.height);
      }
      if (faceDisplayTypes.get(EnumFacing.EAST) == InfoDisplayType.IO) {
        CapBankClientNetwork.IOInfo info = cn.getIODisplayInfo(xCoord, yCoord, zCoord, EnumFacing.EAST);
        maxZ = Math.max(maxZ, zCoord + info.width);
        minY = Math.min(minY, yCoord + 1 - info.height);
      }
      if (faceDisplayTypes.get(EnumFacing.WEST) == InfoDisplayType.IO) {
        CapBankClientNetwork.IOInfo info = cn.getIODisplayInfo(xCoord, yCoord, zCoord, EnumFacing.WEST);
        minZ = Math.min(minZ, zCoord + 1 - info.width);
        minY = Math.min(minY, yCoord + 1 - info.height);
      }
    }

    return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
  }

  // ----------- Redstone

  public @Nonnull RedstoneControlMode getInputControlMode() {
    return inputControlMode;
  }

  public void setInputControlMode(@Nonnull RedstoneControlMode inputControlMode) {
    this.inputControlMode = inputControlMode;
    markDirty();
  }

  public @Nonnull RedstoneControlMode getOutputControlMode() {
    return outputControlMode;
  }

  public void setOutputControlMode(@Nonnull RedstoneControlMode outputControlMode) {
    this.outputControlMode = outputControlMode;
    markDirty();
  }

  // ----------- Power

  @Override
  public IPowerStorage getController() {
    return network;
  }

  @Override
  public long getEnergyStoredL() {
    if (network == null) {
      return getEnergyStored();
    }
    return network.getEnergyStoredL();
  }

  @Override
  public long getMaxEnergyStoredL() {
    if (network == null) {
      return getMaxEnergyStored();
    }
    return network.getMaxEnergyStoredL();
  }

  @Override
  public int getAverageIOPerTick() {
    return network == null ? 0 : network.getAverageIOPerTick();
  }

  @Override
  public boolean isOutputEnabled(@Nonnull EnumFacing direction) {
    IoMode mode = getIoMode(direction);
    return (mode == IoMode.PUSH || mode == IoMode.NONE) && isOutputEnabled();
  }

  private boolean isOutputEnabled() {
    if (network == null) {
      return true;
    }
    return network.isOutputEnabled();
  }

  @Override
  public boolean isInputEnabled(@Nonnull EnumFacing direction) {
    IoMode mode = getIoMode(direction);
    return (mode == IoMode.PULL || mode == IoMode.NONE) && isInputEnabled();
  }

  private boolean isInputEnabled() {
    if (network == null) {
      return true;
    }
    return network.isInputEnabled();
  }

  @Override
  public boolean isNetworkControlledIo(@Nonnull EnumFacing direction) {
    IoMode mode = getIoMode(direction);
    return mode == IoMode.NONE || mode == IoMode.PULL;
  }

  @Override
  public boolean isCreative() {
    return getType().isCreative();
  }

  public @Nonnull List<EnergyReceptor> getReceptors() {
    if (receptorsDirty) {
      updateReceptors();
    }
    return receptors;
  }

  private void updateReceptors() {

    if (network == null) {
      return;
    }
    network.removeReceptors(receptors);

    receptors.clear();
    for (EnumFacing dir : EnumFacing.values()) {
      IPowerInterface pi = getReceptorForFace(NullHelper.notnullJ(dir, "Enum.values()"));
      if (pi != null) {
        IoMode ioMode = getIoMode(NullHelper.notnullJ(dir, "Enum.values()"));
        if (ioMode != IoMode.DISABLED && ioMode != IoMode.PULL) {
          EnergyReceptor er = new EnergyReceptor(this, pi, NullHelper.notnullJ(dir, "Enum.values()"));
          validateModeForReceptor(er);
          receptors.add(er);
        }
      }
    }
    network.addReceptors(receptors);

    receptorsDirty = false;
  }

  private IPowerInterface getReceptorForFace(@Nonnull EnumFacing faceHit) {
    TileEntity te = world.getTileEntity(getPos().offset(faceHit));
    if (!(te instanceof TileCapBank)) {
      return PowerHandlerUtil.getPowerInterface(te, faceHit.getOpposite());
    } else {
      TileCapBank other = (TileCapBank) te;
      if (other.getType() != getType()) {
        return PowerHandlerUtil.getPowerInterface(te, faceHit.getOpposite());
      }
    }
    return null;
  }

  private EnergyReceptor getEnergyReceptorForFace(@Nonnull EnumFacing dir) {
    IPowerInterface pi = getReceptorForFace(dir);
    if (pi == null || pi.getProvider() instanceof TileCapBank) {
      return null;
    }
    return new EnergyReceptor(this, pi, dir);
  }

  private void validateModeForReceptor(@Nonnull EnumFacing dir) {
    validateModeForReceptor(getEnergyReceptorForFace(dir));
  }

  private void validateModeForReceptor(EnergyReceptor er) {
    if (er == null)
      return;
    // IoMode ioMode = getIoMode(er.getDir());
    // if ((ioMode == IoMode.PUSH_PULL || ioMode == IoMode.NONE) && er.getConduit() == null) {
    // if (er.getReceptor().isOutputOnly()) {
    // setIoMode(er.getDir(), IoMode.PULL, false);
    // } else if (er.getReceptor().isInputOnly()) {
    // setIoMode(er.getDir(), IoMode.PUSH, false);
    // }
    // }
    // if (ioMode == IoMode.PULL && er.getReceptor().isInputOnly()) {
    // setIoMode(er.getDir(), IoMode.PUSH, false);
    // } else if (ioMode == IoMode.PUSH && er.getReceptor().isOutputOnly()) {
    // setIoMode(er.getDir(), IoMode.DISABLED, false);
    // }
  }

  // ------------------- Power -----------------

  @Override
  public void addEnergy(int energy) {
    if (network == null) {
      setEnergyStored(getEnergyStored() + energy);
    } else {
      network.addEnergy(energy);
    }
  }

  @Override
  public void setEnergyStored(int stored) {
    energyStored = MathHelper.clamp(stored, 0, getMaxEnergyStored());
    markDirty();
  }

  @Override
  public int getEnergyStored() {
    return energyStored;
  }

  @Override
  public int getMaxEnergyStored() {
    return getType().getMaxEnergyStored();
  }

  @Override
  public int getMaxEnergyRecieved(EnumFacing dir) {
    return getMaxInput();
  }

  @Override
  public int getMaxInput() {
    if (network == null) {
      return getType().getMaxIO();
    }
    return network.getMaxInput();
  }

  public void setMaxInput(int maxInput) {
    if (this.maxInput == maxInput) {
      return;
    }

    if (DiagnosticsConfig.debugTraceCapLimitsExtremelyDetailed.get()) {
      StringBuilder sb = new StringBuilder("CapBank ").append(this).append(" input changed from ").append(this.maxInput).append(" to ").append(maxInput);
      for (StackTraceElement elem : new Exception("Stackstrace").getStackTrace()) {
        sb.append(" at ").append(elem);
      }
      Log.warn(sb);
    }
    this.maxInput = maxInput;
    markDirty();
  }

  public int getMaxInputOverride() {
    return maxInput;
  }

  @Override
  public int getMaxOutput() {
    if (network == null) {
      return getType().getMaxIO();
    }
    return network.getMaxOutput();
  }

  public void setMaxOutput(int maxOutput) {
    if (this.maxOutput == maxOutput) {
      return;
    }

    if (DiagnosticsConfig.debugTraceCapLimitsExtremelyDetailed.get()) {
      StringBuilder sb = new StringBuilder("CapBank ").append(this).append(" output changed from ").append(this.maxOutput).append(" to ").append(maxOutput);
      for (StackTraceElement elem : new Exception("Stackstrace").getStackTrace()) {
        sb.append(" at ").append(elem);
      }
      Log.warn(sb);
    }
    this.maxOutput = maxOutput;
    markDirty();
  }

  public int getMaxOutputOverride() {
    return maxOutput;
  }

  @Override
  public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
    if (network == null || from == null) {
      return 0;
    }
    IoMode mode = getIoMode(from);
    if (mode == IoMode.DISABLED || mode == IoMode.PUSH) {
      return 0;
    }
    return network.receiveEnergy(maxReceive, simulate);
  }

  @Override
  public boolean canConnectEnergy(@Nonnull EnumFacing from) {
    return getIoMode(from) != IoMode.DISABLED;
  }

  public int getComparatorOutput() {
    double stored = getEnergyStored();
    return stored == 0 ? 0 : (int) (1 + stored / getMaxEnergyStored() * 14);
  }

  @Override
  public boolean displayPower() {
    return true;
  }

  // ---------------- NBT

  @Override
  public void readCustomNBT(@Nonnull ItemStack stack) {
    super.readCustomNBT(stack);
    energyStored = NbtValue.ENERGY.getInt(stack);
  }

  @Override
  public void writeCustomNBT(@Nonnull ItemStack stack) {
    super.writeCustomNBT(stack);
    NbtValue.ENERGY.setInt(stack, energyStored);
  }

  @Override
  public @Nonnull BlockPos getLocation() {
    return getPos();
  }

  @Override
  @Nonnull
  public ICapacitorData getCapacitorData() {
    return DefaultCapacitorData.BASIC_CAPACITOR;
  }

  @Override
  public int getMaxUsage() {
    return 0;
  }

}
