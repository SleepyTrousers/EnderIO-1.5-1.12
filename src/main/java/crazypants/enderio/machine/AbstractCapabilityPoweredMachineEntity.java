package crazypants.enderio.machine;

import com.enderio.core.common.inventory.EnderInventory;
import com.enderio.core.common.inventory.InventorySlot;
import com.enderio.core.common.vecmath.VecmathUtil;
import crazypants.enderio.IModObject;
import crazypants.enderio.capability.Filters;
import crazypants.enderio.capacitor.ICapacitorData;
import crazypants.enderio.capacitor.ICapacitorKey;
import crazypants.enderio.power.EnergyTank;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Storable
public abstract class AbstractCapabilityPoweredMachineEntity extends AbstractCapabilityMachineEntity {

  protected static final String CAPSLOT = "cap";

  @Store
  @Nonnull
  private final EnergyTank energy;
  protected float lastSyncPowerStored = -1;

  protected AbstractCapabilityPoweredMachineEntity() {
    this(null, null, null, null, null);
  }

  protected AbstractCapabilityPoweredMachineEntity(@Nullable IModObject modObject) {
    this(null, modObject, null, null, null);
  }

  protected AbstractCapabilityPoweredMachineEntity(@Nonnull ICapacitorKey maxEnergyRecieved, @Nonnull ICapacitorKey maxEnergyStored,
      @Nonnull ICapacitorKey maxEnergyUsed) {
    this(null, null, maxEnergyRecieved, maxEnergyStored, maxEnergyUsed);
  }

  protected AbstractCapabilityPoweredMachineEntity(@Nullable EnderInventory subclassInventory, IModObject modObject) {
    this(subclassInventory, modObject, null, null, null);
  }

  protected AbstractCapabilityPoweredMachineEntity(@Nullable EnderInventory subclassInventory, @Nonnull ICapacitorKey maxEnergyRecieved,
      @Nonnull ICapacitorKey maxEnergyStored, @Nonnull ICapacitorKey maxEnergyUsed) {
    this(subclassInventory, null, maxEnergyRecieved, maxEnergyStored, maxEnergyUsed);
  }

  private AbstractCapabilityPoweredMachineEntity(@Nullable EnderInventory subclassInventory, @Nullable IModObject modObject,
      @Nullable ICapacitorKey maxEnergyRecieved, @Nullable ICapacitorKey maxEnergyStored, @Nullable ICapacitorKey maxEnergyUsed) {
    super(subclassInventory);
    getInventory().add(Type.UPGRADE, CAPSLOT, new InventorySlot(Filters.CAPACITORS, null, 1));
    if (modObject != null) {
      energy = new EnergyTank(this, modObject);
    } else if (maxEnergyRecieved != null && maxEnergyStored != null && maxEnergyUsed != null) {
      energy = new EnergyTank(this, maxEnergyRecieved, maxEnergyStored, maxEnergyUsed);
    } else {
      energy = new EnergyTank(this);
    }
    getEnergy().updateCapacitorFromSlot(getInventory().getSlot(CAPSLOT));
  }

  //----- Common Machine Functions

  public boolean displayPower() {
    return true;
  }

  public boolean hasPower() {
    return getEnergy().getEnergyStored() > 0;
  }

  public ICapacitorData getCapacitorData() {
    return getEnergy().getCapacitorData();
  }

  public EnergyTank getEnergy() {
    return energy;
  }

  public int getEnergyStoredScaled(int scale) {
    final int maxEnergyStored2 = getEnergy().getMaxEnergyStored();
    return maxEnergyStored2 == 0 ? 0 : VecmathUtil.clamp(Math.round(scale * ((float) getEnergy().getEnergyStored() / maxEnergyStored2)), 0, scale);
  }

  @Override
  public void markDirty() {
    super.markDirty();
    updateCapacitorFromSlot();
  }

  protected void onCapacitorDataChange() {
  };

  private void updateCapacitorFromSlot() {
    if (getEnergy().updateCapacitorFromSlot(getInventory().getSlot(CAPSLOT))) {
      forceClientUpdate.set();
      onCapacitorDataChange();
    }
  }

  //--------- NBT

  @Override
  public void readCommon(NBTTagCompound nbtRoot) {
    super.readCommon(nbtRoot);
    updateCapacitorFromSlot();
  }

  @Override
  public boolean hasCapability(Capability<?> capability, EnumFacing facingIn) {
    if (capability == CapabilityEnergy.ENERGY) {
      return true;
    }
    return super.hasCapability(capability, facingIn);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getCapability(Capability<T> capability, EnumFacing facingIn) {
    if (capability == CapabilityEnergy.ENERGY) {
      return (T) energy.get(facingIn);
    }
    return super.getCapability(capability, facingIn);
  }

}
