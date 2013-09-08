package crazypants.enderio.power;

import buildcraft.api.power.IPowerProvider;

public interface MutablePowerProvider extends IPowerProvider {

  public void setEnergy(float energyStored);
  
}
