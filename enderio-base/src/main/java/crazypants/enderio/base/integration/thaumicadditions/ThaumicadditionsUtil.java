package crazypants.enderio.base.integration.thaumicadditions;

import javax.annotation.Nonnull;

import crazypants.enderio.api.farm.IFarmerJoe;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.farming.farmers.PlaceableFarmer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class ThaumicadditionsUtil {

  @SubscribeEvent
  public static void registerFarmers(@Nonnull RegistryEvent.Register<IFarmerJoe> event) {
    PlaceableFarmer farmer = new PlaceableFarmer("item:thaumadditions:vis_seeds");
    farmer.enableTilling();
    if (farmer.isValid()) {
      event.getRegistry().register(farmer.setRegistryName("thaumadditions", "vis_seeds"));
      Log.info("Farming Station: 'Thaumic Additions: reconstructed' integration for farming loaded");
    } else {
      Log.info("Farming Station: 'Thaumic Additions: reconstructed' integration for farming not loaded");
    }
  }

}
