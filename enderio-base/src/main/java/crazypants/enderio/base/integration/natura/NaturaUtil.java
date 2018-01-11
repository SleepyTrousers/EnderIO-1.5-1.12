package crazypants.enderio.base.integration.natura;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;

import crazypants.enderio.api.farm.IFarmer;
import crazypants.enderio.api.farm.IFarmerJoe;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.farming.FarmersRegistry;
import crazypants.enderio.base.farming.farmers.PickableFarmer;
import crazypants.enderio.base.farming.farmers.PlantableFarmer;
import crazypants.enderio.base.farming.farmers.StemFarmer;
import crazypants.enderio.base.farming.farmers.TreeFarmer;
import crazypants.enderio.base.farming.harvesters.IHarvestingTarget;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class NaturaUtil {

  private NaturaUtil() {
  }

  @SubscribeEvent(priority = EventPriority.NORMAL)
  public static void registerFarmers(@Nonnull RegistryEvent.Register<IFarmerJoe> event) {
    int count = 0;

    Item overworldSeeds = FarmersRegistry.findItem("natura", "overworld_seeds");
    if (overworldSeeds != null) {
      Block barleyBlock = FarmersRegistry.findBlock("natura", "barley_crop");
      Block cottonBlock = FarmersRegistry.findBlock("natura", "cotton_crop");
      if (barleyBlock != null) {
        new PlantableFarmer().addHarvestExlude(barleyBlock);
        event.getRegistry().register(new PickableFarmer(barleyBlock, 0, 3, new ItemStack(overworldSeeds, 1, 0)).setRegistryName("natura", "barley"));
        count++;
      }
      if (cottonBlock != null) {
        new PlantableFarmer().addHarvestExlude(cottonBlock);
        event.getRegistry().register(new PickableFarmer(cottonBlock, 0, 4, new ItemStack(overworldSeeds, 1, 1)).setRegistryName("natura", "cotton"));
        count++;
      }
    }

    NNIterator<String> iterator = new NNList<>("overworld_berrybush_raspberry", "overworld_berrybush_blueberry", "overworld_berrybush_blackberry",
        "overworld_berrybush_maloberry", "nether_berrybush_blightberry", "nether_berrybush_duskberry", "nether_berrybush_skyberry",
        "nether_berrybush_stingberry").iterator();
    while (iterator.hasNext()) {
      String berry = iterator.next();
      Block berryBlock = FarmersRegistry.findBlock("natura", berry);
      Item berryItem = FarmersRegistry.findItem("natura", berry);
      if (berryBlock != null && berryItem != null) {
        new PlantableFarmer().addHarvestExlude(berryBlock);
        PickableFarmer farmer = new NaturaBerryFarmer(berryBlock, 0, 3, new ItemStack(berryItem, 1, 0));
        farmer.setRequiresTilling(false);
        event.getRegistry().register(farmer.setRegistryName("natura", "berries"));
        IHarvestingTarget.addLeavesExcemption(berryBlock); // berry bushes are leaves, idiotic...
        count++;
      }
    }

    Block shroomSapling = FarmersRegistry.findBlock("natura", "nether_glowshroom");
    Block shroomGreenBlock = FarmersRegistry.findBlock("natura", "nether_green_large_glowshroom");
    Block shroomBlueBlock = FarmersRegistry.findBlock("natura", "nether_blue_large_glowshroom");
    Block shroomPurpleBlock = FarmersRegistry.findBlock("natura", "nether_purple_large_glowshroom");

    if (shroomSapling != null && shroomGreenBlock != null && shroomBlueBlock != null && shroomPurpleBlock != null) {
      final TreeFarmer shroomFarmer = new TreeFarmer(shroomSapling, shroomGreenBlock, shroomBlueBlock, shroomPurpleBlock);
      shroomFarmer.setIgnoreMeta(true);
      event.getRegistry().register(shroomFarmer.setRegistryName("natura", "shroom"));
      count++;
    }

    FarmersRegistry.registerFlower("block:natura:saguaro_fruit"); // TODO add farmer for the whole thing
    Block saguaroBlock = FarmersRegistry.findBlock("natura", "saguaro");
    Item saguaroBabyItem = FarmersRegistry.findItem("natura", "saguaro_baby");
    if (saguaroBlock != null && saguaroBabyItem != null) {
      event.getRegistry().register(new StemFarmer(saguaroBlock, new ItemStack(saguaroBabyItem)) {
        @Override
        public boolean canHarvest(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull Block block, @Nonnull IBlockState meta) {
          return false;
        }
      }.setRegistryName("natura", "saguaro"));
      count++;
    }

    FarmersRegistry.registerFlower("block:natura:bluebells_flower");

    if (count == 12) {
      Log.info("Farming Station: Natura integration fully loaded");
    } else if (count == 0) {
      Log.info("Farming Station: Natura integration not loaded");
    } else {
      Log.info("Farming Station: Natura integration partially loaded (" + count + " of 12)");
    }
  }

}
