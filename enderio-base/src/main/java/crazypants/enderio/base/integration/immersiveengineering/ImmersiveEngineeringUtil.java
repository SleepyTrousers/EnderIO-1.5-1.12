package crazypants.enderio.base.integration.immersiveengineering;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.farming.farmers.IFarmerJoe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class ImmersiveEngineeringUtil {

  private ImmersiveEngineeringUtil() {
  }

  @SubscribeEvent(priority = EventPriority.NORMAL)
  public static void registerFarmers(@Nonnull RegistryEvent.Register<IFarmerJoe> event) {
    HempFarmerIE farmer = HempFarmerIE.create();
    if (farmer != null) {
      event.getRegistry().register(farmer.setRegistryName("ImmersiveEngineering", "hemp"));
      Log.info("Farming Station: Immersive Engineering integration fully loaded");
    } else {
      Log.info("Farming Station: Immersive Engineering integration not loaded");
    }
  }

}
