package crazypants.enderio.machine.buffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.config.Config;
import crazypants.enderio.machine.AbstractPowerConsumerEntity;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.power.IInternalPowerHandler;
import crazypants.enderio.power.PowerDistributor;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.annotations.Store.StoreFor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import static crazypants.enderio.capacitor.CapacitorKey.BUFFER_POWER_BUFFER;
import static crazypants.enderio.capacitor.CapacitorKey.BUFFER_POWER_INTAKE;

public class TileBuffer extends AbstractPowerConsumerEntity implements IInternalPowerHandler, IPaintable.IPaintableTileEntity {

  @Store({ StoreFor.CLIENT, StoreFor.SAVE })
  private boolean hasPower;
  @Store({ StoreFor.CLIENT, StoreFor.SAVE })
  private boolean hasInventory;
  @Store({ StoreFor.CLIENT, StoreFor.SAVE })
  private boolean isCreative;

  private PowerDistributor dist;

  @Store
  private int maxOut = Config.powerConduitTierThreeRF;
  @Store
  private int maxIn = maxOut;

  public TileBuffer() {
    super(new SlotDefinition(9, 0, 0), BUFFER_POWER_INTAKE, BUFFER_POWER_BUFFER, null);
  }

  @Override
  public @Nonnull String getMachineName() {
    return BufferType.get(this).getUnlocalizedName();
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
  protected boolean processTasks(boolean redstoneCheck) {
    if (!redstoneCheck || getEnergyStored() <= 0) {
      return false;
    }
    if (dist == null) {
      dist = new PowerDistributor(new BlockCoord(this));
    }
    int transmitted = dist.transmitEnergy(worldObj, Math.min(getMaxOutput(), getEnergyStored()));
    if (!isCreative()) {
      setEnergyStored(getEnergyStored() - transmitted);
    }
    return false;
  }

  @Override
  public void setIoMode(@Nullable EnumFacing faceHit, @Nullable IoMode mode) {
    super.setIoMode(faceHit, mode);
    if (dist != null) {
      dist.neighboursChanged();
    }
  }

  @Override
  public void clearAllIoModes() {
    super.clearAllIoModes();
    if (dist != null) {
      dist.neighboursChanged();
    }
  }

  @Override
  public void writeToItemStack(ItemStack stack) {
    super.writeToItemStack(stack);
    stack.setItemDamage(BufferType.get(this).ordinal());
  }

  @Override
  public boolean canInsertItem(int slot, ItemStack var2, EnumFacing side) {
    return hasInventory() && getIoMode(side).canRecieveInput() && isMachineItemValidForSlot(slot, var2);
  }

  @Override
  public boolean canExtractItem(int slot, ItemStack itemstack, EnumFacing side) {
    return hasInventory() && getIoMode(side).canOutput() && canExtractItem(slot, itemstack);
  }

  @Override
  public boolean canConnectEnergy(EnumFacing from) {
    return hasPower;
  }

  @Override
  public int getMaxEnergyRecieved(EnumFacing dir) {
    return maxIn;
  }

  @Override
  public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
    return hasPower() && getIoMode(from).canRecieveInput() ? super.receiveEnergy(from, maxReceive, simulate || isCreative()) : 0;
  }

  @Override
  protected boolean doPull(@Nullable EnumFacing dir) {
    if (isCreative()) {
      ItemStack[] invCopy = new ItemStack[inventory.length];
      for (int i = 0; i < inventory.length; i++) {
        invCopy[i] = inventory[i] == null ? null : inventory[i].copy();
      }

      boolean ret = super.doPull(dir);

      inventory = invCopy;
      return ret;
    } else {
      return super.doPull(dir);
    }
  }

  @Override
  protected boolean doPush(@Nullable EnumFacing dir) {

    if (dir == null || !shouldDoWorkThisTick(20)) {
      return false;
    }

    BlockCoord loc = getLocation().getLocation(dir);
    TileEntity te = worldObj.getTileEntity(loc.getBlockPos());

    if (isCreative()) {
      ItemStack[] invCopy = new ItemStack[inventory.length];
      for (int i = 0; i < inventory.length; i++) {
        invCopy[i] = inventory[i] == null ? null : inventory[i].copy();
      }

      boolean ret = super.doPush(dir, te, slotDefinition.minInputSlot, slotDefinition.maxInputSlot);

      inventory = invCopy;
      return ret;
    } else {
      return super.doPush(dir, te, slotDefinition.minInputSlot, slotDefinition.maxInputSlot);
    }
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

  @Override
  public boolean displayPower() {
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
    markDirty();
  }

  public int getMaxInput() {
    return maxIn;
  }

  public int getMaxOutput() {
    return maxOut;
  }

  @Override
  public int getMaxEnergyStored() {
    return hasPower ? super.getMaxEnergyStored() : 0;
  }

}
