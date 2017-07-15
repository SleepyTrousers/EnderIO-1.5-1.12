package crazypants.enderio.integration.actuallyadditions;

import javax.annotation.Nonnull;

import crazypants.enderio.EnderIO;
import crazypants.enderio.Log;
import crazypants.enderio.farming.FarmersRegistry;
import crazypants.enderio.farming.farmers.IFarmerJoe;
import crazypants.enderio.farming.fertilizer.Bonemeal;
import crazypants.enderio.farming.fertilizer.Fertilizer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class ActuallyadditionsUtil {

  private ActuallyadditionsUtil() {
  }

  @SubscribeEvent(priority = EventPriority.NORMAL)
  public static void registerFarmers(@Nonnull RegistryEvent.Register<IFarmerJoe> event) {
    final Bonemeal fertilizer = new Bonemeal(FarmersRegistry.findItem("actuallyadditions", "item_fertilizer"));
    if (fertilizer.isValid()) {
      Fertilizer.registerFertilizer(fertilizer);
      Log.info("Farming Station: Actually Additions integration loaded");
    } else {
      Log.info("Farming Station: Actually Additions integration not loaded");
    }
  }

}
