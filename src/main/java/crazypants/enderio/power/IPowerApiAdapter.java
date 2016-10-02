package crazypants.enderio.power;

import javax.annotation.Nullable;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.IEnergyStorage;

public interface IPowerApiAdapter {

  IPowerInterface getPowerInterface(@Nullable ICapabilityProvider provider, EnumFacing side);
 
  IEnergyStorage getCapability(@Nullable ICapabilityProvider provider, EnumFacing side);
  
}
