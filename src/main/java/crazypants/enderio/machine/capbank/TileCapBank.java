package crazypants.enderio.machine.capbank;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.TileEntityEio;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.machine.IIoConfigurable;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.machine.capbank.network.CapBankNetwork;
import crazypants.enderio.machine.capbank.network.ClientNetworkManager;
import crazypants.enderio.machine.capbank.network.EnergyReceptor;
import crazypants.enderio.machine.capbank.network.NetworkUtil;
import crazypants.enderio.machine.capbank.packet.PacketNetworkIdRequest;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.power.IInternalPowerReceptor;
import crazypants.enderio.power.IPowerInterface;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.util.BlockCoord;

public class TileCapBank extends TileEntityEio implements IInternalPowerReceptor, IInventory, IIoConfigurable {

  private Map<ForgeDirection, IoMode> faceModes;

  private CapBankType type;

  private int energyStored;
  private int maxInput = -1;
  private int maxOutput = -1;

  private RedstoneControlMode inputControlMode = RedstoneControlMode.IGNORE;
  private RedstoneControlMode outputControlMode = RedstoneControlMode.IGNORE;

  private boolean redstoneStateDirty = true;
  private boolean isRecievingRedstoneSignal;

  private final List<EnergyReceptor> receptors = new ArrayList<EnergyReceptor>();
  private boolean receptorsDirty = true;

  private CapBankNetwork network;

  //Client side refernce to look up network state
  private int networkId = -1;
  private int idRequestTimer = 0;

  @Override
  public BlockCoord getLocation() {
    return new BlockCoord(this);
  }

  public CapBankType getType() {
    if(type == null) {
      type = CapBankType.getTypeFromMeta(getBlockMetadata());
    }
    return type;
  }

  public void onNeighborBlockChange(Block blockId) {
    redstoneStateDirty = true;
    receptorsDirty = true;
  }

  //---------- Multiblock

  @SideOnly(Side.CLIENT)
  public void setNetworkId(int networkId) {
    this.networkId = networkId;
    if(networkId != -1) {
      ClientNetworkManager.getInstance().addToNetwork(networkId, this);
    }
  }

  @SideOnly(Side.CLIENT)
  public int getNetworkId() {
    return networkId;
  }

  public CapBankNetwork getNetwork() {
    return network;
  }

  public boolean setNetwork(CapBankNetwork network) {
    this.network = network;
    return true;
  }

  public boolean canConnectTo(TileCapBank cap) {
    CapBankType t = getType();
    return t.isMultiblock() && t.getUid().equals(cap.getType().getUid());
  }

  @Override
  public void onChunkUnload() {
    if(network != null) {
      network.destroyNetwork();
    }
  }

  @Override
  public void invalidate() {
    super.invalidate();
    if(network != null) {
      network.destroyNetwork();
    }
  }

  @Override
  public void updateEntity() {
    if(worldObj.isRemote) {
      if(networkId == -1) {
        if(idRequestTimer <= 0) {
          PacketHandler.INSTANCE.sendToServer(new PacketNetworkIdRequest(this));
          idRequestTimer = 5;
        } else {
          --idRequestTimer;
        }
      }
      return;
    }
    updateNetwork(worldObj);
    if(network == null) {
      return;
    }

    if(redstoneStateDirty) {
      int sig = worldObj.getStrongestIndirectPower(xCoord, yCoord, zCoord);
      boolean recievingSignal = sig > 0;
      network.updateRedstoneSignal(this, recievingSignal);
      redstoneStateDirty = false;
    }

    if(receptorsDirty) {
      updateReceptors();
    }
  }

  private void updateNetwork(World world) {
    if(getNetwork() == null) {
      NetworkUtil.ensureValidNetwork(this);
    }
    if(getNetwork() != null) {
      getNetwork().onUpdateEntity(this);
    }

  }

  //---------- IO

  @Override
  public IoMode toggleIoModeForFace(ForgeDirection faceHit) {
    IPowerInterface rec = getReceptorForFace(faceHit);
    IoMode curMode = getIoMode(faceHit);
    if(curMode == IoMode.PULL) {
      setIoMode(faceHit, IoMode.PUSH, true);
      return IoMode.PUSH;
    }
    if(curMode == IoMode.PUSH) {
      setIoMode(faceHit, IoMode.DISABLED, true);
      return IoMode.DISABLED;
    }
    if(curMode == IoMode.DISABLED) {
      if(rec == null || rec.getDelegate() instanceof IConduitBundle) {
        setIoMode(faceHit, IoMode.NONE, true);
        return IoMode.NONE;
      }
    }
    setIoMode(faceHit, IoMode.PULL, true);
    return IoMode.PULL;
  }

  @Override
  public boolean supportsMode(ForgeDirection faceHit, IoMode mode) {
    IPowerInterface rec = getReceptorForFace(faceHit);
    if(mode == IoMode.NONE) {
      return rec == null || rec.getDelegate() instanceof IConduitBundle;
    }
    return true;
  }

  @Override
  public void setIoMode(ForgeDirection faceHit, IoMode mode) {
    setIoMode(faceHit, mode, true);
  }

  public void setIoMode(ForgeDirection faceHit, IoMode mode, boolean updateReceptors) {
    if(mode == IoMode.NONE && faceModes == null) {
      return;
    }
    if(faceModes == null) {
      faceModes = new EnumMap<ForgeDirection, IoMode>(ForgeDirection.class);
    }
    faceModes.put(faceHit, mode);
    if(updateReceptors) {
      receptorsDirty = true;
    }
    if(worldObj != null) {
      worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
      worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, blockType);
    }
  }

  @Override
  public IoMode getIoMode(ForgeDirection face) {
    if(faceModes == null) {
      return IoMode.NONE;
    }
    IoMode res = faceModes.get(face);
    if(res == null) {
      return IoMode.NONE;
    }
    return res;
  }

  private IPowerInterface getReceptorForFace(ForgeDirection faceHit) {
    BlockCoord checkLoc = new BlockCoord(this).getLocation(faceHit);
    TileEntity te = worldObj.getTileEntity(checkLoc.x, checkLoc.y, checkLoc.z);
    if(!(te instanceof TileCapBank)) {
      return PowerHandlerUtil.create(te);
    }
    return null;
  }

  //----------- Redstone

  public RedstoneControlMode getInputControlMode() {
    return inputControlMode;
  }

  public void setInputControlMode(RedstoneControlMode inputControlMode) {
    this.inputControlMode = inputControlMode;
  }

  public RedstoneControlMode getOutputControlMode() {
    return outputControlMode;
  }

  public void setOutputControlMode(RedstoneControlMode outputControlMode) {
    this.outputControlMode = outputControlMode;
  }

  //----------- Power

  public List<EnergyReceptor> getReceptors() {
    if(receptorsDirty) {
      updateReceptors();
    }
    return receptors;
  }

  private void updateReceptors() {

    if(network == null) {
      return;
    }
    network.removeReceptors(receptors);

    receptors.clear();
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      IoMode ioMode = getIoMode(dir);
      if(ioMode != IoMode.DISABLED && ioMode != IoMode.PULL) {
        IPowerInterface pi = getReceptorForFace(dir);
        if(pi != null) {
          EnergyReceptor er = new EnergyReceptor(this, pi, dir);
          receptors.add(er);
        }
      }
    }
    network.addReceptors(receptors);

    receptorsDirty = false;
  }

  public void addEnergy(int energy) {
    if(network == null) {
      setEnergyStored(getEnergyStored() + energy);
    } else {
      network.addEnergy(energy);
    }
  }

  @Override
  public void setEnergyStored(int stored) {
    energyStored = MathHelper.clamp_int(stored, 0, getMaxEnergyStored());
  }

  @Override
  public int getEnergyStored() {
    return energyStored;
  }

  @Override
  public int getEnergyStored(ForgeDirection from) {
    return getEnergyStored();
  }

  @Override
  public int getMaxEnergyStored() {
    return getType().getMaxEnergyStored();
  }

  @Override
  public int getMaxEnergyRecieved(ForgeDirection dir) {
    if(network == null) {
      return getType().getMaxIO();
    }
    return network.getMaxEnergyRecieved();
  }

  public int getMaxEnergySent() {
    if(network == null) {
      return getType().getMaxIO();
    }
    return network.getMaxEnergySent();
  }

  public void setMaxEnergyRecieved(int maxInput) {
    this.maxInput = maxInput;
  }

  public void setMaxEnergySend(int maxOutput) {
    this.maxOutput = maxOutput;
  }

  public int getMaxEnergySentOverride() {
    return maxOutput;
  }

  public int getMaxEnergyRecievedOverride() {
    return maxInput;
  }

  @Override
  public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
    if(network == null) {
      return 0;
    }
    IoMode mode = getIoMode(from);
    if(mode == IoMode.DISABLED || mode == IoMode.PUSH) {
      return 0;
    }
    return network.recieveEnergy(maxReceive, simulate);
  }

  @Override
  public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
    return 0;
  }

  @Override
  public int getMaxEnergyStored(ForgeDirection from) {
    return getType().getMaxEnergyStored();
  }

  @Override
  public boolean canConnectEnergy(ForgeDirection from) {
    return getIoMode(from) != IoMode.DISABLED;
  }

  public int getComparatorOutput() {
    return (int) (((double) getEnergyStored() / (double) getMaxEnergyStored()) * 15);
  }

  @Override
  public boolean displayPower() {
    return true;
  }

  //------------------- Inventory

  @Override
  public int getSizeInventory() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public ItemStack getStackInSlot(int p_70301_1_) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
    // TODO Auto-generated method stub

  }

  @Override
  public String getInventoryName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean hasCustomInventoryName() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public int getInventoryStackLimit() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void openInventory() {
  }

  @Override
  public void closeInventory() {
  }

  @Override
  public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
    // TODO Auto-generated method stub
    return false;
  }

  //---------------- NBT

  @Override
  protected void writeCustomNBT(NBTTagCompound nbtRoot) {

    nbtRoot.setString("type", getType().getUid());
    nbtRoot.setInteger("energyStored", energyStored);

    if(maxInput != -1) {
      nbtRoot.setInteger("maxInput", maxInput);
    }
    if(maxOutput != -1) {
      nbtRoot.setInteger("maxOutput", maxOutput);
    }
    if(inputControlMode != RedstoneControlMode.IGNORE) {
      nbtRoot.setShort("inputControlMode", (short) inputControlMode.ordinal());
    }
    if(outputControlMode != RedstoneControlMode.IGNORE) {
      nbtRoot.setShort("outputControlMode", (short) outputControlMode.ordinal());
    }

    //face modes
    if(faceModes != null) {
      nbtRoot.setByte("hasFaces", (byte) 1);
      for (Entry<ForgeDirection, IoMode> e : faceModes.entrySet()) {
        nbtRoot.setShort("face" + e.getKey().ordinal(), (short) e.getValue().ordinal());
      }
    }
  }

  @Override
  protected void readCustomNBT(NBTTagCompound nbtRoot) {

    type = CapBankType.getTypeFromUID(nbtRoot.getString("type"));
    energyStored = nbtRoot.getInteger("energyStored");

    if(nbtRoot.hasKey("maxInput")) {
      maxInput = nbtRoot.getInteger("maxInput");
    } else {
      maxInput = -1;
    }
    if(nbtRoot.hasKey("maxOutput")) {
      maxOutput = nbtRoot.getInteger("maxOutput");
    } else {
      maxOutput = -1;
    }

    if(nbtRoot.hasKey("inputControlMode")) {
      inputControlMode = RedstoneControlMode.values()[nbtRoot.getShort("inputControlMode")];
    } else {
      inputControlMode = RedstoneControlMode.IGNORE;
    }
    if(nbtRoot.hasKey("outputControlMode")) {
      outputControlMode = RedstoneControlMode.values()[nbtRoot.getShort("outputControlMode")];
    } else {
      outputControlMode = RedstoneControlMode.IGNORE;
    }

    if(nbtRoot.hasKey("hasFaces")) {
      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        if(nbtRoot.hasKey("face" + dir.ordinal())) {
          setIoMode(dir, IoMode.values()[nbtRoot.getShort("face" + dir.ordinal())], false);
        }
      }
    }

  }

}
