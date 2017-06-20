package crazypants.enderio.machine.capbank;

import com.enderio.core.common.NBTAction;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.EntityUtil;
import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.util.Util;
import com.enderio.core.common.vecmath.Vector3d;
import crazypants.enderio.Log;
import crazypants.enderio.TileEntityEio;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.capbank.network.*;
import crazypants.enderio.machine.capbank.packet.PacketNetworkIdRequest;
import crazypants.enderio.machine.interfaces.IIoConfigurable;
import crazypants.enderio.machine.modes.IoMode;
import crazypants.enderio.machine.modes.RedstoneControlMode;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.power.ILegacyPowerReceiver;
import crazypants.enderio.power.IPowerInterface;
import crazypants.enderio.power.IPowerStorage;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.util.NbtValue;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.handlers.enderio.HandleIOMode;
import li.cil.oc.api.prefab.ItemStackTabIconRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static crazypants.enderio.machine.MachineObject.blockCapBank;

@Storable
public class TileCapBank extends TileEntityEio implements ILegacyPowerReceiver, IInventory, IIoConfigurable, IPowerStorage {

  @Store(handler = HandleIOMode.class)
  private Map<EnumFacing, IoMode> faceModes;
  @Store(handler = HandleDisplayMode.class)
  private Map<EnumFacing, InfoDisplayType> faceDisplayTypes;

  @Store
  private CapBankType type;

  @Store({ NBTAction.SAVE, NBTAction.SYNC, NBTAction.UPDATE })
  private int energyStored;
  @Store
  private int maxInput = -1;
  @Store
  private int maxOutput = -1;

  @Store
  private RedstoneControlMode inputControlMode = RedstoneControlMode.IGNORE;
  @Store
  private RedstoneControlMode outputControlMode = RedstoneControlMode.IGNORE;

  private boolean redstoneStateDirty = true;

  private final List<EnergyReceptor> receptors = new ArrayList<EnergyReceptor>();
  private boolean receptorsDirty = true;

  private ICapBankNetwork network;

  @Store
  private final ItemStack[] inventory = new ItemStack[4];

  // Client side reference to look up network state
  private int networkId = -1;
  private int idRequestTimer = 0;

  private boolean dropItems;
  private boolean displayTypesDirty;
  private boolean revalidateDisplayTypes;
  private int lastComparatorState;

  public CapBankType getType() {
    if (type == null) {
      if (!hasWorld()) {
        // needed when loading from invalid NBT
        return CapBankType.VIBRANT;
      }
      type = CapBankType.getTypeFromMeta(getBlockMetadata());
    }
    return type;
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

  public boolean canConnectTo(TileCapBank cap) {
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

  public void moveInventoryToNetwork() {
    if (network == null) {
      return;
    }
    if (network.getInventory().getCapBank() == this && !InventoryImpl.isInventoryEmtpy(inventory)) {
      for (TileCapBank cb : network.getMembers()) {
        if (cb != this) {
          for (int i = 0; i < inventory.length; i++) {
            cb.inventory[i] = inventory[i];
            inventory[i] = null;
          }
          network.getInventory().setCapBank(cb);
          markDirty();
          cb.markDirty();
          break;
        }
      }
    }
  }

  public void onBreakBlock() {
    // If we are holding the networks inventory when we are broken, transfer it to another member of the network
    moveInventoryToNetwork();
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

    doDropItems();
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
      if (bs.isOpaqueCube() || bs.getBlock() == blockCapBank.getBlock()) {
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
  public AxisAlignedBB getRenderBoundingBox() {
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

  public RedstoneControlMode getInputControlMode() {
    return inputControlMode;
  }

  public void setInputControlMode(RedstoneControlMode inputControlMode) {
    this.inputControlMode = inputControlMode;
    markDirty();
  }

  public RedstoneControlMode getOutputControlMode() {
    return outputControlMode;
  }

  public void setOutputControlMode(RedstoneControlMode outputControlMode) {
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

  public List<EnergyReceptor> getReceptors() {
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
//    IoMode ioMode = getIoMode(er.getDir());
//    if ((ioMode == IoMode.PUSH_PULL || ioMode == IoMode.NONE) && er.getConduit() == null) {
//      if (er.getReceptor().isOutputOnly()) {
//        setIoMode(er.getDir(), IoMode.PULL, false);
//      } else if (er.getReceptor().isInputOnly()) {
//        setIoMode(er.getDir(), IoMode.PUSH, false);
//      }
//    }
//    if (ioMode == IoMode.PULL && er.getReceptor().isInputOnly()) {
//      setIoMode(er.getDir(), IoMode.PUSH, false);
//    } else if (ioMode == IoMode.PUSH && er.getReceptor().isOutputOnly()) {
//      setIoMode(er.getDir(), IoMode.DISABLED, false);
//    }
  }

  
  //------------------- Power -----------------
  
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
    if(this.maxInput == maxInput) {
      return;
    }

    if (Config.debugTraceCapLimitsExtremelyDetailed) {
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
    if(this.maxOutput == maxOutput) {
      return;
    }

    if (Config.debugTraceCapLimitsExtremelyDetailed) {
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
  public boolean canConnectEnergy(EnumFacing from) {
    return from != null && getIoMode(from) != IoMode.DISABLED;
  }

  public int getComparatorOutput() {
    double stored = getEnergyStored();
    return stored == 0 ? 0 : (int) (1 + stored / getMaxEnergyStored() * 14);
  }

  @Override
  public boolean displayPower() {
    return true;
  }

  // ------------------- Inventory

  @Override
  public ItemStack getStackInSlot(int slot) {
    if (network == null) {
      return null;
    }
    return network.getInventory().getStackInSlot(slot);
  }

  @Override
  public ItemStack decrStackSize(int fromSlot, int amount) {
    if (network == null) {
      return null;
    }
    return network.getInventory().decrStackSize(fromSlot, amount);
  }

  @Override
  public void setInventorySlotContents(int slot, @Nullable ItemStack itemstack) {
    if (network == null) {
      return;
    }
    network.getInventory().setInventorySlotContents(slot, itemstack);
  }

  @Override
  public ItemStack removeStackFromSlot(int index) {
    if (network == null) {
      return null;
    }
    return network.getInventory().removeStackFromSlot(index);
  }

  @Override
  public void clear() {
    if (network == null) {
      return;
    }
    network.getInventory().clear();
  }

  @Override
  public int getSizeInventory() {
    return 4;
  }

  @Override
  public boolean isEmpty() {
    for (ItemStack stack : inventory){
      if (!stack.isEmpty()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public @Nonnull String getName() {
    return blockCapBank.getBlock().getUnlocalizedName() + ".name";
  }

  @Override
  public boolean hasCustomName() {
    return false;
  }

  @Override
  public int getInventoryStackLimit() {
    return 1;
  }

  @Override
  public boolean isUsableByPlayer(EntityPlayer player) {
    return canPlayerAccess(player);
  }

  @Override
  public void openInventory(EntityPlayer e) {
  }

  @Override
  public void closeInventory(EntityPlayer e) {
  }

  @Override
  public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
    if (itemstack.isEmpty()) {
      return false;
    }
    return PowerHandlerUtil.getCapability(itemstack, null) != null;
  }

  public ItemStack[] getInventory() {
    return inventory;
  }

  public void dropItems() {
    dropItems = true;
  }

  public void doDropItems() {
    if (!dropItems) {
      return;
    }
    Vector3d dropLocation;
    EntityPlayer player = world.getClosestPlayer(getPos().getX(), getPos().getY(), getPos().getZ(), 32, false);
    if (player != null) {
      dropLocation = EntityUtil.getEntityPosition(player);
    } else {
      dropLocation = new Vector3d(getPos());
    }
    Util.dropItems(world, inventory, (int) dropLocation.x, (int) dropLocation.y, (int) dropLocation.z, false);
    for (int i = 0; i < inventory.length; i++) {
      inventory[i] = null;
    }
    dropItems = false;
    markDirty();
  }

  // ---------------- NBT


  @Override
  public void readFromItemStack(@Nonnull ItemStack stack) {
    super.readFromItemStack(stack);
    NBTTagCompound rootTag = NbtValue.getRoot(stack);
    if(stack.getTagCompound().hasKey("storedEnergyRF")) {
      energyStored = rootTag.getInteger("storedEnergyRF");
    } else {
      energyStored = NbtValue.ENERGY.getInt(rootTag);
    }
  }

  @Override
  public void writeToItemStack(@Nonnull ItemStack stack) {
    super.writeToItemStack(stack);
    NbtValue.ENERGY.setInt(NbtValue.getRoot(stack), energyStored);
  }

  @Override
  public BlockPos getLocation() {
    return getPos();
  }

  @Override
  public @Nonnull ITextComponent getDisplayName() {
    return hasCustomName() ? new TextComponentString(getName()) : new TextComponentTranslation(getName(), new Object[0]);
  }

  @Override
  public int getField(int id) {
    return 0;
  }

  @Override
  public void setField(int id, int value) {
  }

  @Override
  public int getFieldCount() {
    return 0;
  }

  @Override
  protected void readCustomNBT(NBTAction action, NBTTagCompound root) {
    super.readCustomNBT(action, root);
  }

  @Override
  protected void writeCustomNBT(NBTAction action, NBTTagCompound root) {
    super.writeCustomNBT(action, root);
  }
  
}
