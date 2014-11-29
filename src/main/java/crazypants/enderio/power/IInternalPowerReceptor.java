package crazypants.enderio.power;

import net.minecraftforge.common.util.ForgeDirection;
import cofh.api.energy.IEnergyHandler;

public interface IInternalPowerReceptor extends IEnergyHandler, IPowerContainer {

  int getMaxEnergyRecieved(ForgeDirection dir);
  
  @Override
  int getEnergyStored();
  
  int getMaxEnergyStored();
  
  @Override
  void setEnergyStored(int stored);
  
  /**
   * Should the power be displayed in WAILA or other places
   */
  boolean displayPower();
  
}
