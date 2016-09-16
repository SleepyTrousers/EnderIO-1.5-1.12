package crazypants.enderio.power;

import com.enderio.core.common.util.BlockCoord;

import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyHandler;
//import cofh.api.energy.IEnergyConnection;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.common.Optional.InterfaceList;

@InterfaceList({
  @Interface(iface = "cofh.api.energy.IEnergyConnection", modid = "CoFHAPI|energy"),
  @Interface(iface = "cofh.api.energy.IEnergyHandler", modid = "CoFHAPI|energy")
})
public interface IInternalPoweredTile extends IEnergyConnection, IEnergyHandler {

  //RF: Not optional as used even is RF is not available
  @Override
  boolean canConnectEnergy(EnumFacing from);
  
  //RF Not optional as used even is RF is not available
  @Override
  int getEnergyStored(EnumFacing from);
  
  //RF Not optional as used even is RF is not available
  @Override
  int getMaxEnergyStored(EnumFacing from);
  
  void setEnergyStored(int storedEnergy);

  BlockCoord getLocation();
  
  /**
   * Should the power be displayed in WAILA or other places
   */
  boolean displayPower();
  
}
