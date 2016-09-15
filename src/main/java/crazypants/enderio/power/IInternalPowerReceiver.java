package crazypants.enderio.power;

import net.minecraft.util.EnumFacing;

public interface IInternalPowerReceiver extends IInternalPoweredTile {

  int getMaxEnergyRecieved(EnumFacing dir);
  
  int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate);
    
}
