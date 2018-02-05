package crazypants.enderio.base.power;

import javax.annotation.Nonnull;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.power.forge.ForgeAdapter;
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

}
