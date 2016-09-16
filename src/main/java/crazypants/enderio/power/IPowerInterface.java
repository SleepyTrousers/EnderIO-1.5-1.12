package crazypants.enderio.power;

import net.minecraftforge.energy.IEnergyStorage;

public interface IPowerInterface extends IEnergyStorage {

  Object getProvider();
  
}
