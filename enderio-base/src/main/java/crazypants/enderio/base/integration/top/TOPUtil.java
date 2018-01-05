package crazypants.enderio.base.integration.top;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.handler.darksteel.IDarkSteelUpgrade;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.IForgeRegistry;

@EventBusSubscriber(modid = EnderIO.MODID)
public class TOPUtil {

  private TOPUtil() {
  }

  public static void create() {
    if (Loader.isModLoaded("theoneprobe")) {
      FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", "crazypants.enderio.base.integration.top.TOPCompatibility");
    }
  }

  @SubscribeEvent
  public static void registerDarkSteelUpgrades(@Nonnull RegistryEvent.Register<IDarkSteelUpgrade> event) {
    if (TheOneProbeUpgrade.INSTANCE.isAvailable()) {
      final IForgeRegistry<IDarkSteelUpgrade> registry = event.getRegistry();
      registry.register(TheOneProbeUpgrade.INSTANCE);
      Log.info("Dark Steel Upgrades: TOP integration loaded");
    }
  }

}
