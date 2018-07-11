package crazypants.enderio.base.machine.gui;

import javax.annotation.Nonnull;

import crazypants.enderio.base.capacitor.ICapacitorData;

public interface IPowerBarData {

  int getMaxEnergyStored();

  @Nonnull
  ICapacitorData getCapacitorData();

  int getEnergyStored();

  int getMaxUsage();

  default float getPowerLossPerTick() {
    return 0;
  }

}
