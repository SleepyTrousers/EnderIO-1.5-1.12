package crazypants.enderio.power;

import cofh.api.energy.IEnergyReceiver;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Optional.Interface;

@Interface(iface = "cofh.api.energy.IEnergyReceiver", modid = "CoFHAPI|energy")
public interface IInternalPowerReceiver extends IInternalPoweredTile, IEnergyReceiver {

  int getMaxEnergyRecieved(EnumFacing dir);
  
  //RF: Not optional as used even when RF is present
  @Override
  int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate);
    
}
