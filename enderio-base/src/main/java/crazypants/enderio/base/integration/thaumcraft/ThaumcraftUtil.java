package crazypants.enderio.base.integration.thaumcraft;

import javax.annotation.Nonnull;

import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.base.EnderIO;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class ThaumcraftUtil {

  public static void create() {
    if (Loader.isModLoaded("thaumcraft")) {
      ThaumcraftAspects.loadAspects();
    }
  }

  @SubscribeEvent
  public static void registerDarkSteelUpgrades(@Nonnull RegistryEvent.Register<IDarkSteelUpgrade> event) {
    if (Loader.isModLoaded("thaumcraft")) {
      // final IForgeRegistry<IDarkSteelUpgrade> registry = event.getRegistry();
      // registry.register(GogglesOfRevealingUpgrade.INSTANCE);
      // Log.info("Dark Steel Upgrades: Thaumcraft integration loaded");
    }
  }

}
