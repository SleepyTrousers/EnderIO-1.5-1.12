package crazypants.enderio.power;

import javax.annotation.Nullable;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public interface IPowerApiAdapter {

  IPowerInterface getPowerInterface(@Nullable ICapabilityProvider provider, EnumFacing side);
 
  IEnergyStorage getCapability(@Nullable ICapabilityProvider provider, EnumFacing side);
  
  void attachCapabilities(AttachCapabilitiesEvent.TileEntity evt);
  
}
