package crazypants.enderio.power;

import cofh.api.energy.IEnergyConnection;
import net.minecraft.util.EnumFacing;

//IEnergyHandler, 
public interface IInternalPoweredTile extends IPowerContainer, IEnergyConnection {

  int getMaxEnergyRecieved(EnumFacing dir);
  
  int getMaxEnergyStored();
  
  /**
   * Should the power be displayed in WAILA or other places
   */
  boolean displayPower();
  
}
