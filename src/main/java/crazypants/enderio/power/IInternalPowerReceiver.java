package crazypants.enderio.power;

import net.minecraft.util.EnumFacing;

public interface IInternalPowerReceiver extends IInternalPoweredTile { //, IEnergyReceiver

  int getMaxEnergyRecieved(EnumFacing dir);
  
  //RF
  int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate);
    
}
