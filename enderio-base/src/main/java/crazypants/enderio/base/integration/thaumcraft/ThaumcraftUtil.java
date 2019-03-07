package crazypants.enderio.base.integration.thaumcraft;

import javax.annotation.Nonnull;

import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.config.IntegrationConfig;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import crazypants.enderio.base.farming.FarmersRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class ThaumcraftUtil {

  static final @Nonnull String MODID_THAUMCRAFT = "thaumcraft";

  @SubscribeEvent
  public static void onPost(EnderIOLifecycleEvent.PostInit.Post event) {
    if (Loader.isModLoaded(MODID_THAUMCRAFT) && IntegrationConfig.enableThaumcraftAspects.get()) {
      ThaumcraftAspects.loadAspects();
    }
  }

  @SubscribeEvent
  public static void registerDarkSteelUpgrades(@Nonnull RegistryEvent.Register<IDarkSteelUpgrade> event) {
    if (Loader.isModLoaded(MODID_THAUMCRAFT)) {
      event.getRegistry().registerAll(ThaumaturgeRobesUpgrade.BOOTS, ThaumaturgeRobesUpgrade.LEGS, ThaumaturgeRobesUpgrade.CHEST,
          GogglesOfRevealingUpgrade.INSTANCE);
      Log.info("Dark Steel Upgrades: Thaumcraft integration loaded");
    }
  }

  @SubscribeEvent
  public static void registerHoes(@Nonnull EnderIOLifecycleEvent.Init.Pre event) {
    FarmersRegistry.registerHoes(MODID_THAUMCRAFT, "thaumium_hoe", "void_hoe", "elemental_hoe");
  }

}
