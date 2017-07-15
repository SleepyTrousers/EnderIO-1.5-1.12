package crazypants.enderio.integration.bop;

import javax.annotation.Nonnull;

import crazypants.enderio.EnderIO;
import crazypants.enderio.farming.FarmersRegistry;
import crazypants.enderio.farming.farmers.IFarmerJoe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class BoPUtil {

  private BoPUtil() {
  }

  @SubscribeEvent(priority = EventPriority.NORMAL)
  public static void registerFarmers(@Nonnull RegistryEvent.Register<IFarmerJoe> event) {
    FarmersRegistry.registerFlower("block:biomesoplenty:flowers", "block:biomesoplenty:flowers2");
  }

}
