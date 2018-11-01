package crazypants.enderio.base.integration.ic2e;

import javax.annotation.Nonnull;

import crazypants.enderio.api.farm.IFarmerJoe;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import crazypants.enderio.base.farming.FarmersRegistry;
import crazypants.enderio.base.farming.farmers.RubberTreeFarmer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class IC2eUtil {

  @SubscribeEvent
  public static void registerFarmers(@Nonnull RegistryEvent.Register<IFarmerJoe> event) {
    RubberTreeFarmer farmer = RubberTreeFarmerIC2exp.create();
    if (farmer != null) {
      event.getRegistry().register(farmer.setRegistryName("ic2", "trees"));
      Log.info("Farming Station: IC2 integration fully loaded");
    } else {
      Log.info("Farming Station: IC2 integration not loaded");
    }
  }

  @SubscribeEvent
  public static void registerTools(@Nonnull EnderIOLifecycleEvent.Init.Pre event) {
    FarmersRegistry.registerHoes("ic2", "bronze_hoe");
    FarmersRegistry.registerTreetaps("ic2", "treetap");
  }

}
