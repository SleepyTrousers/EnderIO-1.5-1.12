package crazypants.enderio.power;

import net.minecraftforge.common.ForgeDirection;

public interface IPowerInterface {

  Object getDelegate();

  boolean canConduitConnect(ForgeDirection direction);

  float getEnergyStored(ForgeDirection dir);

  float getMaxEnergyStored(ForgeDirection dir);

  float getPowerRequest(ForgeDirection dir);

  float getMinEnergyReceived(ForgeDirection dir);

  float recieveEnergy(ForgeDirection opposite, float canOffer);

}
