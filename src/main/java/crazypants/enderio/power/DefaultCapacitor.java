package crazypants.enderio.power;

import crazypants.enderio.capacitor.CapacitorKey;
import crazypants.enderio.capacitor.ICapacitorData;

public class DefaultCapacitor implements ICapacitor {
  
  private final ICapacitorData capacitorData;
  
  public DefaultCapacitor(ICapacitorData capacitorData) {
    this.capacitorData = capacitorData;
  }

  @Override
  public int getMaxEnergyReceived() {
    return CapacitorKey.LEGACY_ENERGY_INTAKE.get(capacitorData);
  }

  @Override
  public int getMaxEnergyStored() {
    return CapacitorKey.LEGACY_ENERGY_BUFFER.get(capacitorData);
  }

  @Override
  public int getMaxEnergyExtracted() {
    return CapacitorKey.LEGACY_ENERGY_USE.get(capacitorData);
  }

}
