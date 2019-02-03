package crazypants.enderio.base.power;

import javax.annotation.Nonnull;

import net.minecraftforge.energy.IEnergyStorage;

/**
 * Wrapper to allow more data to be attached to an energy cap object.
 * <p>
 * Used by energy conduits to talk to capacitor banks.
 *
 */
public interface IPowerInterface extends IEnergyStorage {

  @Nonnull
  Object getProvider();

}
