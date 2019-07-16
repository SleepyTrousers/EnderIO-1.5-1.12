package crazypants.enderio.base.power;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.inventory.InventorySlot;

import crazypants.enderio.api.capacitor.ICapacitorData;
import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.base.capacitor.CapacitorHelper;
import crazypants.enderio.base.capacitor.CapacitorKey;
import crazypants.enderio.base.capacitor.DefaultCapacitorData;
import crazypants.enderio.base.machine.base.te.AbstractMachineEntity;
import crazypants.enderio.util.Prep;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.energy.IEnergyStorage;

@Storable // Note: This extends the null tank so we can use the Storable handler
public class EnergyTank extends NullEnergyTank implements IEnergyTank {

  private class Side implements IEnergyStorage {

    private final @Nonnull EnumFacing side;

    public Side(@Nonnull EnumFacing side) {
      this.side = side;
    }

    private boolean isEnabled() {
      if (owner != null) {
        switch (((AbstractMachineEntity) owner).getIoMode(side)) {
        case DISABLED:
        case PUSH:
          return false;
        case NONE:
        case PULL:
        case PUSH_PULL:
          return true;
        }
      }
      throw new RuntimeException("The fat lady has sung.");
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
      if (isEnabled()) {
        return EnergyTank.this.receiveEnergy(maxReceive, simulate);
      }
      return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
      if (isEnabled()) {
        return EnergyTank.this.extractEnergy(maxExtract, simulate);
      }
      return 0;
    }

    @Override
    public int getEnergyStored() {
      if (isEnabled()) {
        return EnergyTank.this.getEnergyStored();
      }
      return 0;
    }

    @Override
    public int getMaxEnergyStored() {
      if (isEnabled()) {
        return EnergyTank.this.getMaxEnergyStored();
      }
      return 0;
    }

    @Override
    public boolean canExtract() {
      if (isEnabled()) {
        return EnergyTank.this.canExtract();
      }
      return false;
    }

    @Override
    public boolean canReceive() {
      if (isEnabled()) {
        return EnergyTank.this.canReceive();
      }
      return false;
    }

  }

  @Override
  public IEnergyStorage get(@Nullable EnumFacing side) {
    if (side != null && (owner instanceof AbstractMachineEntity)) {
      return new Side(side);
    }
    return this;
  }

  private @Nonnull ICapacitorData capacitorData = DefaultCapacitorData.NONE;

  private @Nonnull final ICapacitorKey maxEnergyRecieved, maxEnergyStored, maxEnergyUsed;
  private ICapacitorKey energyLoss = null;

  @Nullable
  TileEntity owner = null;

  @Store
  private int storedEnergy;

  public EnergyTank(@Nonnull ICapacitorKey maxEnergyRecieved, @Nonnull ICapacitorKey maxEnergyStored, @Nonnull ICapacitorKey maxEnergyUsed) {
    this(null, maxEnergyRecieved, maxEnergyStored, maxEnergyUsed);
  }

  public EnergyTank(TileEntity owner, @Nonnull ICapacitorKey maxEnergyRecieved, @Nonnull ICapacitorKey maxEnergyStored, @Nonnull ICapacitorKey maxEnergyUsed) {
    this.owner = owner;
    this.maxEnergyRecieved = maxEnergyRecieved;
    this.maxEnergyStored = maxEnergyStored;
    this.maxEnergyUsed = maxEnergyUsed;
  }

  public EnergyTank(TileEntity owner) {
    this(owner, CapacitorKey.LEGACY_ENERGY_INTAKE, CapacitorKey.LEGACY_ENERGY_BUFFER, CapacitorKey.LEGACY_ENERGY_USE);
  }

  @Override
  public void setEnergyLoss(ICapacitorKey energyLoss) {
    this.energyLoss = energyLoss;
  }

  @Override
  public boolean updateCapacitorFromSlot(@Nonnull InventorySlot slot) {
    int oldValue = maxEnergyStored.get(capacitorData);

    if (Prep.isInvalid(slot.getStackInSlot(0))) {
      capacitorData = DefaultCapacitorData.NONE;
    } else {
      ICapacitorData newData = CapacitorHelper.getCapacitorDataFromItemStack(slot.getStackInSlot(0));
      if (newData == null) {
        capacitorData = DefaultCapacitorData.NONE;
      } else {
        capacitorData = newData;
      }
    }

    if (oldValue != maxEnergyStored.get(capacitorData) || getEnergyStored() > oldValue) {
      setEnergyStored(getEnergyStored());
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean hasCapacitor() {
    return capacitorData != DefaultCapacitorData.NONE;
  }

  private void onChange() {
    if (owner != null) {
      owner.markDirty();
    }
  }

  public int getMaxEnergyRecieved() {
    return maxEnergyRecieved.get(capacitorData);
  }

  public boolean canUseEnergy() {
    return canUseEnergy(maxEnergyUsed);
  }

  @Override
  public boolean canUseEnergy(@Nonnull ICapacitorKey key) {
    int toUse = getMaxUsage(key);
    if (toUse <= getEnergyStored()) {
      return true;
    }
    return false;
  }

  @Override
  public boolean useEnergy() {
    return useEnergy(maxEnergyUsed);
  }

  @Override
  public boolean useEnergy(@Nonnull ICapacitorKey key) {
    int toUse = getMaxUsage(key);
    if (toUse <= getEnergyStored()) {
      setEnergyStored(getEnergyStored() - toUse);
      return true;
    }
    return false;
  }

  @Override
  public int getMaxUsage() {
    return getMaxUsage(maxEnergyUsed);
  }

  @Override
  public int getMaxUsage(@Nonnull ICapacitorKey key) {
    return key.get(capacitorData);
  }

  @Override
  public void loseEnergy() {
    if (storedEnergy > 0) {
      if (energyLoss != null) {
        setEnergyStored(getEnergyStored() - energyLoss.get(capacitorData));
      }
    }
  }

  @Override
  public void setEnergyStored(int stored) {
    int newEnergy = MathHelper.clamp(stored, 0, getMaxEnergyStored());
    if (newEnergy != storedEnergy) {
      storedEnergy = newEnergy;
      onChange();
    }
  }

  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    int result = Math.max(0, Math.min(getMaxEnergyStored() - getEnergyStored(), Math.min(getMaxEnergyRecieved(), maxReceive)));
    if (result > 0 && !simulate) {
      setEnergyStored(getEnergyStored() + result);
    }
    return result;
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {
    return 0;
  }

  @Override
  public int getEnergyStored() {
    return storedEnergy;
  }

  @Override
  public int getMaxEnergyStored() {
    return maxEnergyStored.get(capacitorData);
  }

  @Override
  public boolean canExtract() {
    return false;
  }

  @Override
  public boolean canReceive() {
    return maxEnergyRecieved != CapacitorKey.NO_POWER;
  }

  @Override
  public @Nonnull ICapacitorData getCapacitorData() {
    return capacitorData;
  }

  @Override
  public boolean isFull() {
    return getEnergyStored() >= getMaxEnergyStored();
  }

}
