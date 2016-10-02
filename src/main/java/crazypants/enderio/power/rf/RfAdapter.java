package crazypants.enderio.power.rf;

import javax.annotation.Nullable;

import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyContainerItem;
import crazypants.enderio.Log;
import crazypants.enderio.power.IPowerApiAdapter;
import crazypants.enderio.power.IPowerInterface;
import crazypants.enderio.power.PowerHandlerUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.IEnergyStorage;

public class RfAdapter implements IPowerApiAdapter {
  

  public static void create() throws Exception {
    // Make sure we can load these classes or throw an exception
    Class.forName("cofh.api.energy.IEnergyConnection");
    Class.forName("cofh.api.energy.IEnergyContainerItem");
    PowerHandlerUtil.addAdapter(new RfAdapter());
    Log.info("RF integration loaded");
  }

  @Override
  public IPowerInterface getPowerInterface(@Nullable ICapabilityProvider provider, EnumFacing side) {
    if (provider instanceof IEnergyConnection) {
      IEnergyConnection con = (IEnergyConnection)provider;
      if(con.canConnectEnergy(side)) {
        return new PowerInterfaceRF((IEnergyConnection) provider, side);
      }
    }
    return null;
  }

  @Override
  public IEnergyStorage getCapability(@Nullable ICapabilityProvider provider, EnumFacing side) {
    if (provider instanceof ItemStack && ((ItemStack) provider).getItem() instanceof IEnergyContainerItem) {
      return new ItemWrapperRF((IEnergyContainerItem) ((ItemStack) provider).getItem(), (ItemStack) provider);
    }
    return null;
  }

}