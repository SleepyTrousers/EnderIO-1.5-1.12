package crazypants.enderio.base.power;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.inventory.InventorySlot;

import crazypants.enderio.api.capacitor.ICapacitorData;
import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.base.capacitor.DefaultCapacitorData;
import info.loenwind.autosave.annotations.Factory;
import info.loenwind.autosave.annotations.Storable;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.IEnergyStorage;

@Storable
public class NullEnergyTank implements IEnergyTank {

  private static final @Nonnull NullEnergyTank INSTANCE = new NullEnergyTank();

  @Factory
  public static @Nonnull NullEnergyTank getInstance() {
    return INSTANCE;
  }

  private NullEnergyTank() {
  }

  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    return 0;
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {
    return 0;
  }

  @Override
  public int getEnergyStored() {
    return 0;
  }

  @Override
  public int getMaxEnergyStored() {
    return 0;
  }

  @Override
  public boolean canExtract() {
    return false;
  }

  @Override
  public boolean canReceive() {
    return false;
  }

  @Override
  @Nonnull
  public ICapacitorData getCapacitorData() {
    return DefaultCapacitorData.NONE;
  }

  @Override
  public int getMaxUsage() {
    return 0;
  }

  @Override
  public boolean canUseEnergy(@Nonnull ICapacitorKey key) {
    return false;
  }

  @Override
  public IEnergyStorage get(@Nullable EnumFacing side) {
    return this;
  }

  @Override
  public int getMaxUsage(@Nonnull ICapacitorKey key) {
    return 0;
  }

  @Override
  public boolean hasCapacitor() {
    return false;
  }

  @Override
  public boolean isFull() {
    return false;
  }

  @Override
  public void loseEnergy() {
  }

  @Override
  public void setEnergyLoss(ICapacitorKey energyLoss) {
  }

  @Override
  public void setEnergyStored(int stored) {
  }

  @Override
  public boolean updateCapacitorFromSlot(@Nonnull InventorySlot slot) {
    return false;
  }

  @Override
  public boolean useEnergy() {
    return false;
  }

  @Override
  public boolean useEnergy(@Nonnull ICapacitorKey key) {
    return false;
  }

}
