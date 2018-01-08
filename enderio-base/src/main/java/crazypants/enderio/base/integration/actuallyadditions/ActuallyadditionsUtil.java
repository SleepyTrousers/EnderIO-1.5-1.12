package crazypants.enderio.base.integration.actuallyadditions;

import javax.annotation.Nonnull;

import crazypants.enderio.api.farm.IFertilizer;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.farming.FarmersRegistry;
import crazypants.enderio.base.farming.fertilizer.Bonemeal;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class ActuallyadditionsUtil {

  private ActuallyadditionsUtil() {
  }

  @SubscribeEvent
  public static void registerFertilizer(@Nonnull RegistryEvent.Register<IFertilizer> event) {
    final Bonemeal fertilizer = new Bonemeal(FarmersRegistry.findItem("actuallyadditions", "item_fertilizer"));
    if (fertilizer.isValid()) {
      event.getRegistry().register(fertilizer);
      Log.info("Farming Station: Actually Additions integration loaded");
    } else {
      Log.info("Farming Station: Actually Additions integration not loaded");
    }
  }

}
