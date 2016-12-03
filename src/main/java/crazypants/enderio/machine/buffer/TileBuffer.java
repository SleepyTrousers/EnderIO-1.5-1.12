package crazypants.enderio.machine.buffer;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.config.Config;
import crazypants.enderio.machine.AbstractPowerConsumerEntity;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.machine.painter.IPaintableTileEntity;
import crazypants.enderio.machine.painter.PainterUtil;
import crazypants.enderio.power.IInternalPowerHandler;
import crazypants.enderio.power.PowerDistributor;

public class TileBuffer extends AbstractPowerConsumerEntity implements IPaintableTileEntity, IInternalPowerHandler {

  private Block sourceBlock;
  private int sourceBlockMetadata;

  private boolean hasPower, hasInventory, isCreative;

  private PowerDistributor dist;

  private int maxOut = Config.powerConduitTierThreeRF;
  private int maxIn = maxOut;

  public TileBuffer() {
    super(new SlotDefinition(9, 0, 0));
  }

  @Override
  public String getMachineName() {
    return BlockItemBuffer.Type.get(this).getUnlocalizedName();
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    return true;
  }

  @Override
  public boolean isActive() {
    return false;
  }

  @Override
  protected boolean processTasks(boolean redstoneCheckPassed) {
    if(getEnergyStored() <= 0 || !redstoneCheckPassed) {
      return false;
    }
    if(dist == null) {
      dist = new PowerDistributor(new BlockCoord(this));
    }
    int transmitted = dist.transmitEnergy(worldObj, Math.min(getMaxOutput(), getEnergyStored()));
    if (!isCreative()) {
      setEnergyStored(getEnergyStored() - transmitted);
    }
    return false;
  }

  @Override
  public void setIoMode(ForgeDirection faceHit, IoMode mode) {
    super.setIoMode(faceHit, mode);
    if(dist != null) {
      dist.neighboursChanged();
    }
  }

  @Override
  public void clearAllIoModes() {
    super.clearAllIoModes();
    if(dist != null) {
      dist.neighboursChanged();
    }
  }
  
  @Override
  public void writeToItemStack(ItemStack stack) {
      super.writeToItemStack(stack);
      stack.setItemDamage(BlockItemBuffer.Type.get(this).ordinal());
  }

  @Override
  public boolean canInsertItem(int slot, ItemStack var2, int side) {
    return hasInventory() && getIoMode(ForgeDirection.VALID_DIRECTIONS[side]).canRecieveInput() && isMachineItemValidForSlot(slot, var2);
  }

  @Override
  public boolean canExtractItem(int slot, ItemStack itemstack, int side) {
    return hasInventory() && getIoMode(ForgeDirection.VALID_DIRECTIONS[side]).canOutput() && canExtractItem(slot, itemstack);
  }

  @Override
  public boolean canConnectEnergy(ForgeDirection from) {
    return hasPower;
  }

  @Override
  public int getMaxEnergyRecieved(ForgeDirection dir) {
    return maxIn;
  }

  @Override
  public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
    return hasPower() && getIoMode(from).canRecieveInput() ? super.receiveEnergy(from, maxReceive, isCreative() || simulate) : 0;
  }

  @Override
  public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
    return 0;
  }

  @Override
  protected boolean doPull(ForgeDirection dir) {
    ItemStack[] invCopy = new ItemStack[inventory.length];
    for (int i = 0; i < inventory.length; i++) {
      invCopy[i] = inventory[i] == null ? null : inventory[i].copy();
    }

    boolean ret = super.doPull(dir);

    if(isCreative()) {
      inventory = invCopy;
    }

    return ret;
  }

  @Override
  protected boolean doPush(ForgeDirection dir) {

    if(!shouldDoWorkThisTick(20)) {
      return false;
    }

    ItemStack[] invCopy = new ItemStack[inventory.length];
    for (int i = 0; i < inventory.length; i++) {
      invCopy[i] = inventory[i] == null ? null : inventory[i].copy();
    }

    BlockCoord loc = getLocation().getLocation(dir);
    TileEntity te = worldObj.getTileEntity(loc.x, loc.y, loc.z);

    boolean ret = super.doPush(dir, te, slotDefinition.minInputSlot, slotDefinition.maxInputSlot);

    if(isCreative()) {
      inventory = invCopy;
    }

    return ret;
  }

  @Override
  public void writeCustomNBT(NBTTagCompound nbtRoot) {
    super.writeCustomNBT(nbtRoot);
    nbtRoot.setBoolean("hasInv", hasInventory);
    nbtRoot.setBoolean("hasPower", hasPower);
    nbtRoot.setBoolean("creative", isCreative);
  }

  @Override
  public void writeCommon(NBTTagCompound nbtRoot) {
    super.writeCommon(nbtRoot);
    PainterUtil.setSourceBlock(nbtRoot, sourceBlock, sourceBlockMetadata);
    nbtRoot.setInteger("maxIn", maxIn);
    nbtRoot.setInteger("maxOut", maxOut);
  }

  @Override
  public void readCustomNBT(NBTTagCompound nbtRoot) {
    super.readCustomNBT(nbtRoot);
    this.hasInventory = nbtRoot.getBoolean("hasInv");
    this.hasPower = nbtRoot.getBoolean("hasPower");
    this.isCreative = nbtRoot.getBoolean("creative");
  }

  @Override
  public void readCommon(NBTTagCompound nbtRoot) {
    super.readCommon(nbtRoot);
    this.sourceBlock = PainterUtil.getSourceBlock(nbtRoot);
    this.sourceBlockMetadata = PainterUtil.getSourceBlockMetadata(nbtRoot);
    this.maxIn = nbtRoot.getInteger("maxIn");
    this.maxOut = nbtRoot.getInteger("maxOut");
  }

  @Override
  public void setSourceBlockMetadata(int sourceBlockMetadata) {
    this.sourceBlockMetadata = sourceBlockMetadata;
  }

  @Override
  public int getSourceBlockMetadata() {
    return sourceBlockMetadata;
  }

  @Override
  public void setSourceBlock(Block sourceBlock) {
    this.sourceBlock = sourceBlock;
  }

  @Override
  @Nullable
  public Block getSourceBlock() {
    return sourceBlock;
  }

  public boolean hasInventory() {
    return hasInventory;
  }

  public void setHasInventory(boolean hasInventory) {
    this.hasInventory = hasInventory;
  }

  @Override
  public boolean hasPower() {
    return hasPower;
  }

  public void setHasPower(boolean hasPower) {
    this.hasPower = hasPower;
  }

  public boolean isCreative() {
    return isCreative;
  }

  public void setCreative(boolean isCreative) {
    this.isCreative = isCreative;
    if (isCreative) {
      this.setEnergyStored(getMaxEnergyStored() / 2);
    }
  }

  public void setIO(int in, int out) {
    this.maxIn = in;
    this.maxOut = out;
  }

  public int getMaxInput() {
    return maxIn;
  }

  public int getMaxOutput() {
    return maxOut;
  }
}
