package crazypants.enderio.integration.techreborn;

import javax.annotation.Nonnull;

import crazypants.enderio.EnderIO;
import crazypants.enderio.Log;
import crazypants.enderio.farming.farmers.IFarmerJoe;
import crazypants.enderio.farming.farmers.RubberTreeFarmer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class TechRebornUtil {

  private TechRebornUtil() {
  }

  @SubscribeEvent(priority = EventPriority.NORMAL)
  public static void registerFarmers(@Nonnull RegistryEvent.Register<IFarmerJoe> event) {
    RubberTreeFarmer farmer = RubberTreeFarmerTechReborn.create();
    if (farmer != null) {
      event.getRegistry().register(farmer.setRegistryName("techreborn", "trees"));
      Log.info("Farming Station: TechReborn integration fully loaded");
    } else {
      Log.info("Farming Station: TechReborn integration not loaded");
    }
  }

}
