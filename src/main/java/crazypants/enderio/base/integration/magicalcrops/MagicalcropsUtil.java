package crazypants.enderio.base.integration.magicalcrops;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.farming.FarmersRegistry;
import crazypants.enderio.base.farming.farmers.IFarmerJoe;
import crazypants.enderio.base.farming.fertilizer.Bonemeal;
import crazypants.enderio.base.farming.fertilizer.Fertilizer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class MagicalcropsUtil {

  private MagicalcropsUtil() {
  }

  @SubscribeEvent(priority = EventPriority.NORMAL)
  public static void registerFarmers(@Nonnull RegistryEvent.Register<IFarmerJoe> event) {
    final Bonemeal fertilizer = new Bonemeal(FarmersRegistry.findItem("magicalcrops", "magicalcrops_magicalcropfertilizer"));
    if (fertilizer.isValid()) {
      Fertilizer.registerFertilizer(fertilizer);
      Log.info("Farming Station: Magicalcrops integration loaded");
    } else {
      Log.info("Farming Station: Magicalcrops integration not loaded");
    }
  }

}
