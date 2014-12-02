package crazypants.enderio.machine.capbank;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderio.TileEntityEio;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.machine.IIoConfigurable;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.power.IInternalPowerReceptor;
import crazypants.enderio.power.IPowerInterface;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.util.BlockCoord;

public class TileCapBank extends TileEntityEio implements IInternalPowerReceptor, IInventory, IIoConfigurable {

  private Map<ForgeDirection, IoMode> faceModes;

  private CapBankType type;

  private int energyStored;

  private CapBankNetwork network;

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

  //---------- Multiblock

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
    CapBankNetwork network = getNetwork();
    if(network != null) {
      network.destroyNetwork();
    }
  }

  @Override
  public void updateEntity() {
    if(worldObj.isRemote) {
      return;
    }
    updateNetwork(worldObj);
  }


  private void updateNetwork(World world) {
    if(getNetwork() == null) {
      NetworkManager.ensureValidNetwork(this);
      if(getNetwork() != null && !world.isRemote) {
        //        world.notifyBlocksOfNeighborChange(bundle.getEntity().xCoord, bundle.getEntity().yCoord, bundle.getEntity().zCoord,
        //            bundle.getEntity().getBlockType());
      }
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

    //TODO
    //    if(updateReceptors) {
    //      receptorsDirty = true;
    //      getController().masterReceptorsDirty = true;
    //      notifyNeighbours = true;
    //    }
    //    render = true;    
    //    updateBlock();
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


  //----------- Power

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
  public int getMaxEnergyRecieved(ForgeDirection dir) {
    CapBankNetwork con = getNetwork();
    if(con == null) {
      getType().getMaxEnergyStored();
    }
    return con.getMaxEnergyRecieved();
  }

  @Override
  public int getMaxEnergyStored() {
    return getType().getMaxEnergyStored();
  }

  @Override
  public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
    return PowerHandlerUtil.recieveInternal(this, maxReceive, from, simulate);
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

    if(nbtRoot.hasKey("hasFaces")) {
      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        if(nbtRoot.hasKey("face" + dir.ordinal())) {
          setIoMode(dir, IoMode.values()[nbtRoot.getShort("face" + dir.ordinal())], false);
        }
      }
    }

  }

}
