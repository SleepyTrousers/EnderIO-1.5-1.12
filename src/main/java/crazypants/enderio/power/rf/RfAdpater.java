package crazypants.enderio.power.rf;

import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyContainerItem;
import crazypants.enderio.power.IPowerApiAdapter;
import crazypants.enderio.power.IPowerInterface;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.IEnergyStorage;

public class RfAdpater implements IPowerApiAdapter {
  

  public RfAdpater() throws Exception {
    //Make sure we can load these classes or throw an excpetion
    Class.forName("cofh.api.energy.IEnergyConnection");
    Class.forName("cofh.api.energy.IEnergyContainerItem");
  }

  @Override
  public IPowerInterface getPowerInterface(ICapabilityProvider provider, EnumFacing side) {
    if (provider instanceof IEnergyConnection) {
      IEnergyConnection con = (IEnergyConnection)provider;
      if(con.canConnectEnergy(side)) {
        return new PowerInterfaceRF((IEnergyConnection) provider, side);
      }
    }
    return null;
  }

  @Override
  public IEnergyStorage getCapability(ICapabilityProvider provider, EnumFacing side) {
    if (provider instanceof ItemStack && ((ItemStack) provider).getItem() instanceof IEnergyContainerItem) {
      return new ItemWrapperRF((IEnergyContainerItem) ((ItemStack) provider).getItem(), (ItemStack) provider);
    }
    return null;
  }
  
}