package crazypants.enderio.base.integration.magicalcrops;

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
public class MagicalcropsUtil {

  private MagicalcropsUtil() {
  }

  @SubscribeEvent
  public static void registerFertilizer(@Nonnull RegistryEvent.Register<IFertilizer> event) {
    final Bonemeal fertilizer = new Bonemeal(FarmersRegistry.findItem("magicalcrops", "magicalcrops_magicalcropfertilizer"));
    if (fertilizer.isValid()) {
      event.getRegistry().register(fertilizer);
      Log.info("Farming Station: Magicalcrops integration loaded");
    } else {
      Log.info("Farming Station: Magicalcrops integration not loaded");
    }
  }

}
