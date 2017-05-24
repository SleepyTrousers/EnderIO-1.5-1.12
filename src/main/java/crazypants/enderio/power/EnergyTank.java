package crazypants.enderio.power;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.inventory.InventorySlot;

import crazypants.enderio.IModObject;
import crazypants.enderio.capacitor.CapacitorHelper;
import crazypants.enderio.capacitor.CapacitorKey;
import crazypants.enderio.capacitor.CapacitorKeyType;
import crazypants.enderio.capacitor.DefaultCapacitorData;
import crazypants.enderio.capacitor.DefaultCapacitorKey;
import crazypants.enderio.capacitor.ICapacitorData;
import crazypants.enderio.capacitor.ICapacitorKey;
import crazypants.enderio.capacitor.Scaler;
import crazypants.enderio.machine.base.te.AbstractMachineEntity;
import crazypants.util.Prep;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.energy.IEnergyStorage;

@Storable
public class EnergyTank implements IEnergyStorage {

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

  public IEnergyStorage get(@Nullable EnumFacing side) {
    if (side != null && (owner instanceof AbstractMachineEntity)) {
      return new Side(side);
    }
    return this;
  }

  private @Nonnull ICapacitorData capacitorData = DefaultCapacitorData.NONE;

  private @Nonnull final ICapacitorKey maxEnergyRecieved, maxEnergyStored, maxEnergyUsed;
  @Nullable
  TileEntity owner = null;

  @Store
  private int storedEnergy;

  public EnergyTank(TileEntity owner, @Nonnull ICapacitorKey maxEnergyRecieved, @Nonnull ICapacitorKey maxEnergyStored, @Nonnull ICapacitorKey maxEnergyUsed) {
    this.owner = owner;
    this.maxEnergyRecieved = maxEnergyRecieved;
    this.maxEnergyStored = maxEnergyStored;
    this.maxEnergyUsed = maxEnergyUsed;
  }

  public EnergyTank(TileEntity owner, @Nonnull IModObject modObject) {
    this.owner = owner;
    this.maxEnergyRecieved = new DefaultCapacitorKey(modObject, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.POWER, 80);
    this.maxEnergyStored = new DefaultCapacitorKey(modObject, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.POWER, 100000);
    this.maxEnergyUsed = new DefaultCapacitorKey(modObject, CapacitorKeyType.ENERGY_USE, Scaler.Factory.POWER, 20);
  }

  public EnergyTank(TileEntity owner) {
    this.owner = owner;
    this.maxEnergyRecieved = CapacitorKey.LEGACY_ENERGY_INTAKE;
    this.maxEnergyStored = CapacitorKey.LEGACY_ENERGY_BUFFER;
    this.maxEnergyUsed = CapacitorKey.LEGACY_ENERGY_USE;
  }

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

  private void onChange() {
    if (owner != null) {
      owner.markDirty();
    }
  }

  public int getMaxEnergyRecieved() {
    return maxEnergyRecieved.get(capacitorData);
  }

  public boolean useEnergy() {
    int toUse = maxEnergyUsed.get(capacitorData);
    if (toUse <= getEnergyStored()) {
      setEnergyStored(getEnergyStored() - toUse);
      return true;
    }
    return false;
  }

  private void setEnergyStored(int stored) {
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
    return true;
  }

  public @Nonnull ICapacitorData getCapacitorData() {
    return capacitorData;
  }

}
