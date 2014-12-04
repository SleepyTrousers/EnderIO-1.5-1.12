package crazypants.enderio.power;

import net.minecraftforge.common.util.ForgeDirection;

public interface IPowerStorage {

  IPowerStorage getController();

  long getEnergyStoredL();

  long getMaxEnergyStoredL();

  boolean isOutputEnabled(ForgeDirection direction);

  boolean isInputEnabled(ForgeDirection direction);

  int getMaxOutput();

  int getMaxInput();

  void addEnergy(int amount);

  boolean isCreative();

}
