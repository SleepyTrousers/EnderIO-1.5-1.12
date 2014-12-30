package crazypants.enderio.power;

import crazypants.util.BlockCoord;

public interface IPowerContainer {

  int getEnergyStored();

  void setEnergyStored(int storedEnergy);

  BlockCoord getLocation();

}
