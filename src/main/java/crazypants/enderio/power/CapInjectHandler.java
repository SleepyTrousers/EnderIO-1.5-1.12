package crazypants.enderio.power;

import javax.annotation.Nonnull;

import crazypants.enderio.Log;
import crazypants.enderio.power.forge.ForgeAdapter;
import crazypants.enderio.power.tesla.TeslaAdapter;
import net.darkhax.tesla.api.ITeslaHolder;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * This class MUST be safe to be class-loaded before the full classpath is in place!
 *
 */
public class CapInjectHandler {

  public static void loadClass() {
  }

  private CapInjectHandler() {
  }

  @CapabilityInject(IEnergyStorage.class)
  private static void capRegistered1(@Nonnull Capability<IEnergyStorage> cap) {
    try {
      ForgeAdapter.capRegistered(cap);
    } catch (Throwable e) {
      Log.error("Forge Energy failed to load. Forge Energy integration disabled. Reason: ");
      e.printStackTrace();
    }
  }

  @CapabilityInject(ITeslaHolder.class)
  private static void capRegistered2(@Nonnull Capability<ITeslaHolder> cap) {
    try {
      TeslaAdapter.capRegistered(cap);
    } catch (Throwable e) {
      Log.error("Tesla API failed to load. Tesla integration disabled. Reason:");
      e.printStackTrace();
    }

  }

}
