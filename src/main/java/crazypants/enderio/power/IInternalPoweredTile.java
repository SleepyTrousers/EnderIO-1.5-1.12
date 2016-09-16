package crazypants.enderio.power;

import com.enderio.core.common.util.BlockCoord;

//import cofh.api.energy.IEnergyConnection;
import net.minecraft.util.EnumFacing;


public interface IInternalPoweredTile {//, IEnergyConnection, IEnergyHandler {

  //RF
  boolean canConnectEnergy(EnumFacing from);
  
  //RF
  int getEnergyStored(EnumFacing from);
  
  //RF
  int getMaxEnergyStored(EnumFacing from);
  
  void setEnergyStored(int storedEnergy);

  BlockCoord getLocation();
  
  /**
   * Should the power be displayed in WAILA or other places
   */
  boolean displayPower();
  
}
