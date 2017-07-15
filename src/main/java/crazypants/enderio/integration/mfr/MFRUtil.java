package crazypants.enderio.integration.mfr;

import javax.annotation.Nonnull;

import crazypants.enderio.EnderIO;
import crazypants.enderio.Log;
import crazypants.enderio.farming.FarmersRegistry;
import crazypants.enderio.farming.farmers.IFarmerJoe;
import crazypants.enderio.farming.farmers.TreeFarmer;
import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class MFRUtil {

  private MFRUtil() {
  }

  @SubscribeEvent(priority = EventPriority.NORMAL)
  public static void registerFarmers(@Nonnull RegistryEvent.Register<IFarmerJoe> event) {
    Block cropBlock = FarmersRegistry.findBlock("minefactoryreloaded", "rubberwood.sapling");
    Block woodBlock = FarmersRegistry.findBlock("minefactoryreloaded", "rubberwood.log");
    if (cropBlock != null && woodBlock != null) {
      event.getRegistry().register(new TreeFarmer(cropBlock, woodBlock).setRegistryName("minefactoryreloaded", "trees"));
      Log.info("Farming Station: MFR integration fully loaded");
    } else {
      Log.info("Farming Station: MFR integration not loaded");
    }
  }

}
