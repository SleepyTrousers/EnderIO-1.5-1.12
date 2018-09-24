package crazypants.enderio.base.integration.bop;

import javax.annotation.Nonnull;

import crazypants.enderio.api.farm.IFarmerJoe;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.IntegrationConfig;
import crazypants.enderio.base.farming.FarmersRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class BoPUtil {

  private BoPUtil() {
  }

  @SubscribeEvent
  public static void registerFarmers(@Nonnull RegistryEvent.Register<IFarmerJoe> event) {
    if (IntegrationConfig.enableBoP.get()) {
      FarmersRegistry.registerFlower("block:biomesoplenty:flowers", "block:biomesoplenty:flowers2");
    }
  }

}
