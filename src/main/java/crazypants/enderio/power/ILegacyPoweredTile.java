package crazypants.enderio.power;

import com.enderio.core.common.util.BlockCoord;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public interface ILegacyPoweredTile {

  boolean canConnectEnergy(EnumFacing from);
  
  int getEnergyStored(EnumFacing from);
  
  int getMaxEnergyStored(EnumFacing from);
  
  void setEnergyStored(int storedEnergy);

  BlockPos getLocation();
  
  /**
   * Should the power be displayed in WAILA or other places
   */
  boolean displayPower();
  
}
