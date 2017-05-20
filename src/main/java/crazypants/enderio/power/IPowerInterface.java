package crazypants.enderio.power;

import javax.annotation.Nonnull;

import net.minecraftforge.energy.IEnergyStorage;

public interface IPowerInterface extends IEnergyStorage {

  @Nonnull
  Object getProvider();

}
