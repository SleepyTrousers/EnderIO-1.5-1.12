package crazypants.enderio.integration.ic2e;

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
public class IC2eUtil {

  private IC2eUtil() {
  }

  @SubscribeEvent(priority = EventPriority.NORMAL)
  public static void registerFarmers(@Nonnull RegistryEvent.Register<IFarmerJoe> event) {
    RubberTreeFarmer farmer = RubberTreeFarmerIC2exp.create();
    if (farmer != null) {
      event.getRegistry().register(farmer.setRegistryName("ic2", "trees"));
      Log.info("Farming Station: IC2 integration fully loaded");
    } else {
      Log.info("Farming Station: IC2 integration not loaded");
    }
  }

}
