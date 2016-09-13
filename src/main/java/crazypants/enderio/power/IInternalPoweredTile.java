package crazypants.enderio.power;

import com.enderio.core.common.util.BlockCoord;

//import cofh.api.energy.IEnergyConnection;
import net.minecraft.util.EnumFacing;


public interface IInternalPoweredTile {//, IEnergyConnection {

  //RF
  boolean canConnectEnergy(EnumFacing from);
  
  int getEnergyStored();
  
  int getMaxEnergyStored();

  void setEnergyStored(int storedEnergy);

  BlockCoord getLocation();
  
  /**
   * Should the power be displayed in WAILA or other places
   */
  boolean displayPower();
  
}
