package crazypants.enderio.base.integration.basemetals;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.IntegrationConfig;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import crazypants.enderio.base.farming.FarmersRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class BaseMetalsUtil {

  @SubscribeEvent
  public static void registerHoes(@Nonnull EnderIOLifecycleEvent.Init.Pre event) {
    if (IntegrationConfig.enableBaseMetals.get()) {
      FarmersRegistry.registerHoes("basemetals", "adamantine_hoe", "aquarium_hoe", "brass_hoe", "bronze_hoe", "coldiron_hoe", "copper_hoe", "cupronickel_hoe",
          "electrum_hoe", "invar_hoe", "lead_hoe", "mithril_hoe", "nickel_hoe", "platinum_hoe", "silver_hoe", "starsteel_hoe", "steel_hoe", "tin_hoe");
    }
  }

}
