package crazypants.enderio.base.integration.exu2;

import javax.annotation.Nonnull;

import crazypants.enderio.api.farm.IFarmerJoe;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.farming.FarmersRegistry;
import crazypants.enderio.base.farming.farmers.CustomSeedFarmer;
import net.minecraft.init.Blocks;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class ExU2Util {

  private ExU2Util() {
  }

  @SubscribeEvent(priority = EventPriority.NORMAL)
  public static void registerFarmers(@Nonnull RegistryEvent.Register<IFarmerJoe> event) {
    int count = 0;
    CustomSeedFarmer farmer = FarmersRegistry.addSeed(event, "extrautils2", "enderlilly", "enderlilly");
    if (farmer != null) {
      farmer.setIgnoreGroundCanSustainCheck(true);
      farmer.setRequiresTilling(false); // disables tilling
      farmer.setCheckGroundForFarmland(true); // extra check needed when not tilling
      farmer.clearTilledBlocks(); // remove farmland
      farmer.addTilledBlock(Blocks.DIRT);
      farmer.addTilledBlock(Blocks.GRASS);
      farmer.addTilledBlock(Blocks.END_STONE);
      count++;
    }
    farmer = FarmersRegistry.addSeed(event, "extrautils2", "redorchid", "redorchid");
    if (farmer != null) {
      farmer.setIgnoreGroundCanSustainCheck(true);
      farmer.setRequiresTilling(false); // disables tilling
      farmer.setCheckGroundForFarmland(true); // extra check needed when not tilling
      farmer.clearTilledBlocks(); // remove farmland
      farmer.addTilledBlock(Blocks.REDSTONE_ORE);
      farmer.addTilledBlock(Blocks.LIT_REDSTONE_ORE);
      count++;
    }

    if (count == 2) {
      Log.info("Farming Station: Extra Utilities 2 integration fully loaded");
    } else if (count == 0) {
      Log.info("Farming Station: Extra Utilities 2 integration not loaded");
    } else {
      Log.info("Farming Station: Extra Utilities 2 integration partially loaded (" + count + " of 2)");
    }

  }

}
