package crazypants.enderio.base.integration.mfr;

import javax.annotation.Nonnull;

import crazypants.enderio.api.farm.IFarmerJoe;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.farming.FarmersRegistry;
import crazypants.enderio.base.farming.farmers.TreeFarmer;
import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class MFRUtil {

  private MFRUtil() {
  }

  @SubscribeEvent
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
