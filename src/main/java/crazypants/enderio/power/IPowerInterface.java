package crazypants.enderio.power;

import net.minecraft.util.EnumFacing;

public interface IPowerInterface {

  Object getDelegate();

  boolean canConnect(EnumFacing direction);

  int getEnergyStored(EnumFacing dir);

  int getMaxEnergyStored(EnumFacing dir);

  int recieveEnergy(EnumFacing opposite, int canOffer);

  boolean isOutputOnly();

  boolean isInputOnly();

}
