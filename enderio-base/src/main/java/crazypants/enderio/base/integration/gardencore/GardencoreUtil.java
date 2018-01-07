package crazypants.enderio.base.integration.gardencore;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.farming.FarmersRegistry;
import crazypants.enderio.base.farming.fertilizer.Bonemeal;
import crazypants.enderio.base.farming.fertilizer.IFertilizer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class GardencoreUtil {

  private GardencoreUtil() {
  }

  @SubscribeEvent
  public static void registerFertilizer(@Nonnull RegistryEvent.Register<IFertilizer> event) {
    final Bonemeal fertilizer = new Bonemeal(FarmersRegistry.findItem("gardencore", "compost_pile"));
    if (fertilizer.isValid()) {
      event.getRegistry().register(fertilizer);
      Log.info("Farming Station: Gardencore integration loaded");
    } else {
      Log.info("Farming Station: Gardencore integration not loaded");
    }
  }

}
