package crazypants.enderio.base.integration.top;

import javax.annotation.Nonnull;

import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class TOPUtil {

  private static final @Nonnull String MODID_TOP = "theoneprobe";

  @SubscribeEvent
  public static void create(EnderIOLifecycleEvent.PreInit event) {
    if (Loader.isModLoaded(MODID_TOP)) {
      FMLInterModComms.sendFunctionMessage(MODID_TOP, "getTheOneProbe", "crazypants.enderio.base.integration.top.TOPCompatibility");
    }
  }

  @SubscribeEvent
  public static void registerDarkSteelUpgrades(@Nonnull RegistryEvent.Register<IDarkSteelUpgrade> event) {
    if (Loader.isModLoaded(MODID_TOP)) {
      event.getRegistry().register(TheOneProbeUpgrade.INSTANCE);
      Log.info("Dark Steel Upgrades: TOP integration loaded");
    }
  }

}
