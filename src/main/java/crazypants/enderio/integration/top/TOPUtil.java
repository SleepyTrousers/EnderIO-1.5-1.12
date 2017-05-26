package crazypants.enderio.integration.top;

import javax.annotation.Nonnull;

import crazypants.enderio.handler.darksteel.DarkSteelRecipeManager;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInterModComms;

public class TOPUtil {

  private TOPUtil() {
  }

  public static void create() {
    if (Loader.isModLoaded("theoneprobe")) {
      FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", "crazypants.enderio.integration.top.TOPCompatibility");
    }
  }

  public static void addUpgrades(@Nonnull DarkSteelRecipeManager manager) {
    if (TheOneProbeUpgrade.INSTANCE.isAvailable()) {
      manager.addUpgrade(TheOneProbeUpgrade.INSTANCE);
    }
  }

}
