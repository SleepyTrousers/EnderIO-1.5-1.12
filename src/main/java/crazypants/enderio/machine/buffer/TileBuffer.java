package crazypants.enderio.machine.buffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.config.Config;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.power.ILegacyPowerReceiver;
import crazypants.enderio.power.PowerDistributor;
import crazypants.enderio.power.forge.InternalRecieverTileWrapper;
import info.loenwind.autosave.annotations.Store;
import com.enderio.core.common.NBTAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

import static crazypants.enderio.capacitor.CapacitorKey.BUFFER_POWER_BUFFER;
import static crazypants.enderio.capacitor.CapacitorKey.BUFFER_POWER_INTAKE;

public class TileBuffer extends AbstractPowerConsumerEntity implements ILegacyPowerReceiver, IPaintable.IPaintableTileEntity {

  @Store({ NBTAction.CLIENT, NBTAction.SAVE })
  private boolean hasPower;
  @Store({ NBTAction.CLIENT, NBTAction.SAVE })
  private boolean hasInventory;
  @Store({ NBTAction.CLIENT, NBTAction.SAVE })
  private boolean isCreative;

  private PowerDistributor dist;

  @Store
  private int maxOut = Config.powerConduitTierThreeRF;
  @Store
  private int maxIn = maxOut;

  public TileBuffer() {
    super(new SlotDefinition(9), BUFFER_POWER_INTAKE, BUFFER_POWER_BUFFER, null);
  }

  @Override
  public @Nonnull String getMachineName() {
    return BufferType.get(this).getUnlocalizedName();
  }

  @Override
  public boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    return true;
  }

  @Override
  public boolean isActive() {
    return false;
  }

  @Override
  protected boolean processTasks(boolean redstoneCheck) {
    if (!redstoneCheck || getEnergyStored(null) <= 0) {
      return false;
    }
    if (dist == null) {
      dist = new PowerDistributor(new BlockCoord(this));
    }
    int transmitted = dist.transmitEnergy(world, Math.min(getMaxOutput(), getEnergyStored(null)));
    if (!isCreative()) {
      setEnergyStored(getEnergyStored(null) - transmitted);
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
  public boolean canInsertItem(int slot, ItemStack itemstack, EnumFacing side) {
    return hasInventory && super.canInsertItem(slot, itemstack, side);
  }
  
  @Override
  public boolean canExtractItem(int slot, ItemStack itemstack, EnumFacing side) {
    return hasInventory() && super.canExtractItem(slot, itemstack, side);
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
    
    if (isCreative()) {
      ItemStack[] invCopy = new ItemStack[inventory.length];
      for (int i = 0; i < inventory.length; i++) {
        invCopy[i] = inventory[i] == null ? null : inventory[i].copy();
      }
      boolean ret = super.doPush(dir);

      inventory = invCopy;
      return ret;
    } else {
      return super.doPush(dir);
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

  @Override
  public boolean hasCapability(Capability<?> capability, EnumFacing facingIn) {
    if (capability == CapabilityEnergy.ENERGY) {
      return hasPower;
    }
    return super.hasCapability(capability, facingIn);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getCapability(Capability<T> capability, EnumFacing facingIn) {
    if (capability == CapabilityEnergy.ENERGY) {
      return hasPower ? (T) new InternalRecieverTileWrapper(this, facingIn) : null;
    }
    return super.getCapability(capability, facingIn);
  }

}
