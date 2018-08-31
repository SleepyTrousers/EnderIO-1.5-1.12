package crazypants.enderio.base.integration.ic2c;

import javax.annotation.Nonnull;

import crazypants.enderio.api.farm.IFarmerJoe;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.farming.farmers.RubberTreeFarmer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class IC2cUtil {

  @SubscribeEvent
  public static void registerFarmers(@Nonnull RegistryEvent.Register<IFarmerJoe> event) {
    RubberTreeFarmer farmer = RubberTreeFarmerIC2classic.create();
    if (farmer != null) {
      event.getRegistry().register(farmer.setRegistryName("ic2c", "trees"));
      Log.info("Farming Station: IC2 classic integration fully loaded");
    } else {
      Log.info("Farming Station: IC2 classic integration not loaded");
    }
  }

}
