package crazypants.enderio.integration.natura;

import crazypants.enderio.Log;
import crazypants.enderio.machine.farm.FarmersRegistry;
import crazypants.enderio.machine.farm.TileFarmStation;
import crazypants.enderio.machine.farm.farmers.FarmersCommune;
import crazypants.enderio.machine.farm.farmers.PickableFarmer;
import crazypants.enderio.machine.farm.farmers.StemFarmer;
import crazypants.enderio.machine.farm.farmers.TreeFarmer;
import crazypants.enderio.machine.farm.farmers.TreeHarvestUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class NaturaUtil {

  private NaturaUtil() {
  }

  public static void addNatura() {
    int count = 0;

    Item overworldSeeds = FarmersRegistry.findItem("natura", "overworld_seeds");
    if (overworldSeeds != null) {
      Block barleyBlock = FarmersRegistry.findBlock("natura", "barley_crop");
      Block cottonBlock = FarmersRegistry.findBlock("natura", "cotton_crop");
      if (barleyBlock != null) {
        FarmersRegistry.DEFAULT_FARMER.addHarvestExlude(barleyBlock);
        FarmersCommune.joinCommune(new PickableFarmer(barleyBlock, 0, 3, new ItemStack(overworldSeeds, 1, 0)));
        count++;
      }
      if (cottonBlock != null) {
        FarmersRegistry.DEFAULT_FARMER.addHarvestExlude(cottonBlock);
        FarmersCommune.joinCommune(new PickableFarmer(cottonBlock, 0, 4, new ItemStack(overworldSeeds, 1, 1)));
        count++;
      }
    }

    for (String berry : new String[] { "overworld_berrybush_raspberry", "overworld_berrybush_blueberry", "overworld_berrybush_blackberry",
        "overworld_berrybush_maloberry", "nether_berrybush_blightberry", "nether_berrybush_duskberry", "nether_berrybush_skyberry",
        "nether_berrybush_stingberry" }) {
      Block berryBlock = FarmersRegistry.findBlock("natura", berry);
      Item berryItem = FarmersRegistry.findItem("natura", berry);
      if (berryBlock != null && berryItem != null) {
        FarmersRegistry.DEFAULT_FARMER.addHarvestExlude(berryBlock);
        PickableFarmer farmer = new NaturaBerryFarmer(berryBlock, 0, 3, new ItemStack(berryItem, 1, 0));
        farmer.setRequiresFarmland(false);
        FarmersCommune.joinCommune(farmer);
        TreeHarvestUtil.addLeavesExcemption(berryBlock); // berry bushes are leaves, idiotic...
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
      FarmersCommune.joinCommune(shroomFarmer);
      count++;
    }

    FarmersRegistry.registerFlower("block:natura:saguaro_fruit"); // TODO add farmer for the whole thing
    Block saguaroBlock = FarmersRegistry.findBlock("natura", "saguaro");
    Item saguaroBabyItem = FarmersRegistry.findItem("natura", "saguaro_baby");
    if (saguaroBlock != null && saguaroBabyItem != null) {
      FarmersCommune.joinCommune(new StemFarmer(saguaroBlock, new ItemStack(saguaroBabyItem)) {
        @Override
        public boolean canHarvest(TileFarmStation farm, BlockPos bc, Block block, IBlockState meta) {
          return false;
        }
      });
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
