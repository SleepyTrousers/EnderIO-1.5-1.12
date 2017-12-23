package crazypants.enderio.machines.machine.buffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.machine.baselegacy.AbstractPowerConsumerEntity;
import crazypants.enderio.base.machine.baselegacy.SlotDefinition;
import crazypants.enderio.base.machine.modes.IoMode;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.power.ILegacyPowerReceiver;
import crazypants.enderio.base.power.PowerDistributor;
import crazypants.enderio.base.power.forge.InternalRecieverTileWrapper;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

import static crazypants.enderio.machines.capacitor.CapacitorKey.BUFFER_POWER_BUFFER;
import static crazypants.enderio.machines.capacitor.CapacitorKey.BUFFER_POWER_INTAKE;
import static crazypants.enderio.machines.capacitor.CapacitorKey.BUFFER_POWER_USE;

@Storable
public abstract class TileBuffer extends AbstractPowerConsumerEntity implements ILegacyPowerReceiver, IPaintable.IPaintableTileEntity {

  @Storable
  public static class TileBufferItem extends TileBuffer {
    public TileBufferItem() {
      super(new SlotDefinition(9), BufferType.ITEM);
    }
  }

  @Storable
  public static class TileBufferPower extends TileBuffer {
    public TileBufferPower() {
      super(new SlotDefinition(0), BufferType.POWER);
    }
  }

  @Storable
  public static class TileBufferOmni extends TileBuffer {
    public TileBufferOmni() {
      super(new SlotDefinition(9), BufferType.OMNI);
    }
  }

  @Storable
  public static class TileBufferCreative extends TileBuffer {
    public TileBufferCreative() {
      super(new SlotDefinition(9), BufferType.CREATIVE);
    }
  }

  private final @Nonnull BufferType type;
  private transient PowerDistributor dist;
  @Store
  private int maxOut;
  @Store
  private int maxIn;

  public TileBuffer(@Nonnull SlotDefinition slotDefinition, @Nonnull BufferType type) {
    super(slotDefinition, BUFFER_POWER_INTAKE, BUFFER_POWER_BUFFER, BUFFER_POWER_USE);
    this.type = type;
    if (type.isCreative) {
      setEnergyStored(getMaxEnergyStored() / 2);
    }
    maxOut = maxIn = maxEnergyRecieved.get(getCapacitorData());
  }

  @Override
  public @Nonnull String getMachineName() {
    return getType().getUnlocalizedName();
  }

  @Override
  public boolean isMachineItemValidForSlot(int i, @Nonnull ItemStack itemstack) {
    return true;
  }

  @Override
  public boolean isActive() {
    return false;
  }

  @Override
  protected boolean processTasks(boolean redstoneCheck) {
    if (!redstoneCheck || getEnergyStored() <= 0 || !tryToUsePower()) {
      return false;
    }
    if (dist == null) {
      dist = new PowerDistributor(getPos());
    }
    int transmitted = dist.transmitEnergy(world, Math.min(getMaxOutput(), getEnergyStored()));
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
  public void writeToItemStack(@Nonnull ItemStack stack) {
    super.writeToItemStack(stack);
    stack.setItemDamage(getType().ordinal());
  }

  @Override
  public void readFromItemStack(@Nonnull ItemStack stack) {
    super.readFromItemStack(stack);
    if (type.isCreative) {
      setEnergyStored(getMaxEnergyStored() / 2);
    }
  }

  @Override
  public boolean canInsertItem(int slot, @Nonnull ItemStack itemstack, @Nonnull EnumFacing side) {
    return hasInventory() && super.canInsertItem(slot, itemstack, side);
  }

  @Override
  public boolean canExtractItem(int slot, @Nonnull ItemStack itemstack, @Nonnull EnumFacing side) {
    return hasInventory() && super.canExtractItem(slot, itemstack, side);
  }

  @Override
  public boolean canConnectEnergy(@Nonnull EnumFacing from) {
    return hasPower();
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
    return getType().hasInventory;
  }

  @Override
  public boolean hasPower() {
    return getType().hasPower;
  }

  public boolean isCreative() {
    return getType().isCreative;
  }

  @Override
  public boolean displayPower() {
    return hasPower();
  }

  public void setIO(int in, int out) {
    this.maxIn = MathHelper.clamp(in, 0, maxEnergyRecieved.get(getCapacitorData()));
    this.maxOut = MathHelper.clamp(out, 0, maxEnergyRecieved.get(getCapacitorData()));
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
    return hasPower() ? super.getMaxEnergyStored() : 0;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facingIn) {
    if (capability == CapabilityEnergy.ENERGY) {
      return hasPower() ? (T) new InternalRecieverTileWrapper(this, facingIn) : null;
    }
    return super.getCapability(capability, facingIn);
  }

  private BufferType getType() {
    return type;
  }

}
