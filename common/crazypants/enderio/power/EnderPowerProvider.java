package crazypants.enderio.power;

import buildcraft.api.power.PowerProvider;

public class EnderPowerProvider extends PowerProvider implements MutablePowerProvider {

  public void setEnergy(float energyStored) {
    this.energyStored = energyStored;
  }

}
