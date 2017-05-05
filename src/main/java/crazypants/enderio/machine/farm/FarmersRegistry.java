package crazypants.enderio.machine.farm;

import crazypants.enderio.Log;
import crazypants.enderio.integration.forestry.ForestryFarmer;
import crazypants.enderio.integration.ic2e.RubberTreeFarmerIC2exp;
import crazypants.enderio.integration.immersiveengineering.HempFarmerIE;
import crazypants.enderio.integration.natura.NaturaBerryFarmer;
import crazypants.enderio.integration.techreborn.RubberTreeFarmerTechReborn;
import crazypants.enderio.machine.farm.farmers.ChorusFarmer;
import crazypants.enderio.machine.farm.farmers.CocoaFarmer;
import crazypants.enderio.machine.farm.farmers.CustomSeedFarmer;
import crazypants.enderio.machine.farm.farmers.FarmersCommune;
import crazypants.enderio.machine.farm.farmers.FlowerPicker;
import crazypants.enderio.machine.farm.farmers.MelonFarmer;
import crazypants.enderio.machine.farm.farmers.NetherWartFarmer;
import crazypants.enderio.machine.farm.farmers.OredictTreeFarmer;
import crazypants.enderio.machine.farm.farmers.PickableFarmer;
import crazypants.enderio.machine.farm.farmers.PlantableFarmer;
import crazypants.enderio.machine.farm.farmers.RubberTreeFarmer;
import crazypants.enderio.machine.farm.farmers.StemFarmer;
import crazypants.enderio.machine.farm.farmers.TreeFarmer;
import crazypants.enderio.machine.farm.farmers.TreeHarvestUtil;
import crazypants.util.Things;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.Loader;

public final class FarmersRegistry {

  private static final Things SAPLINGS = new Things("treeSapling");
  private static final Things WOODS = new Things("logWood", "blockSlimeCongealed");
  private static final Things FLOWERS = new Things("block:BiomesOPlenty:flowers", "block:BiomesOPlenty:flowers2", "block:Botany:flower", "block:botania:flower",
      "block:botania:doubleFlower1", "block:botania:doubleFlower2", "block:botania:shinyFlower", "block:botania:mushroom", "block:natura:bluebells_flower",
      "block:natura:saguaro_fruit").add(Blocks.YELLOW_FLOWER).add(Blocks.RED_FLOWER);

  public static final PlantableFarmer DEFAULT_FARMER = new PlantableFarmer();

  public static void addFarmers() {

    addTechreborn();
    addExtraUtilities2();
    addNatura();
    addIC2();
    addMFR();
    addFlowers();
    addImmersiveEngineering();

    if (Loader.isModLoaded("forestry")) {
      ForestryFarmer.init();
    }

    FarmersCommune.joinCommune(new StemFarmer(Blocks.REEDS, new ItemStack(Items.REEDS)));
    FarmersCommune.joinCommune(new StemFarmer(Blocks.CACTUS, new ItemStack(Blocks.CACTUS)));
    FarmersCommune.joinCommune(new OredictTreeFarmer(SAPLINGS, WOODS));
    FarmersCommune.joinCommune(new TreeFarmer(true, Blocks.RED_MUSHROOM, Blocks.RED_MUSHROOM_BLOCK));
    FarmersCommune.joinCommune(new TreeFarmer(true, Blocks.BROWN_MUSHROOM, Blocks.BROWN_MUSHROOM_BLOCK));
    // special case of plantables to get spacing correct
    FarmersCommune.joinCommune(new MelonFarmer(Blocks.MELON_STEM, Blocks.MELON_BLOCK, new ItemStack(Items.MELON_SEEDS)));
    FarmersCommune.joinCommune(new MelonFarmer(Blocks.PUMPKIN_STEM, Blocks.PUMPKIN, new ItemStack(Items.PUMPKIN_SEEDS)));
    // 'BlockNetherWart' is not an IGrowable
    FarmersCommune.joinCommune(new NetherWartFarmer());
    // Cocoa is odd
    FarmersCommune.joinCommune(new CocoaFarmer());
    // Chorus plant is even odder
    FarmersCommune.joinCommune(new ChorusFarmer());
    // Handles all 'vanilla' style crops
    FarmersCommune.joinCommune(DEFAULT_FARMER);
  }

  public static void addPickable(String mod, String blockName, String itemName) {
    if (Block.REGISTRY.containsKey(new ResourceLocation(mod, blockName))) {
      Block cropBlock = Block.REGISTRY.getObject(new ResourceLocation(mod, blockName));
      Item seedItem = Item.REGISTRY.getObject(new ResourceLocation(mod, itemName));
      if (seedItem != null) {
        FarmersCommune.joinCommune(new PickableFarmer(cropBlock, new ItemStack(seedItem)));
      }
    }
  }

  public static CustomSeedFarmer addSeed(String mod, String blockName, String itemName, Block... extraFarmland) {
    if (Block.REGISTRY.containsKey(new ResourceLocation(mod, blockName))) {
      Block cropBlock = Block.REGISTRY.getObject(new ResourceLocation(mod, blockName));
      Item seedItem = Item.REGISTRY.getObject(new ResourceLocation(mod, itemName));
      if (seedItem != null) {
        CustomSeedFarmer farmer = new CustomSeedFarmer(cropBlock, new ItemStack(seedItem));
        if (extraFarmland != null) {
          for (Block farmland : extraFarmland) {
            if (farmland != null) {
              farmer.addTilledBlock(farmland);
            }
          }
        }
        FarmersCommune.joinCommune(farmer);
        return farmer;
      }
    }
    return null;
  }

  private static Block findBlock(String mod, String blockName) {
    final ResourceLocation name = new ResourceLocation(mod, blockName);
    if (Block.REGISTRY.containsKey(name)) {
      return Block.REGISTRY.getObject(name);
    }
    return null;
  }

  private static Item findItem(String mod, String itemName) {
    final ResourceLocation name = new ResourceLocation(mod, itemName);
    if (Item.REGISTRY.containsKey(name)) {
      return Item.REGISTRY.getObject(name);
    }
    return null;
  }

  private static void addNatura() {
    int count = 0;

    Item overworldSeeds = findItem("natura", "overworld_seeds");
    if (overworldSeeds != null) {
      Block barleyBlock = findBlock("natura", "barley_crop");
      Block cottonBlock = findBlock("natura", "cotton_crop");
      if (barleyBlock != null) {
        DEFAULT_FARMER.addHarvestExlude(barleyBlock);
        FarmersCommune.joinCommune(new PickableFarmer(barleyBlock, 0, 3, new ItemStack(overworldSeeds, 1, 0)));
        count++;
      }
      if (cottonBlock != null) {
        DEFAULT_FARMER.addHarvestExlude(cottonBlock);
        FarmersCommune.joinCommune(new PickableFarmer(cottonBlock, 0, 4, new ItemStack(overworldSeeds, 1, 1)));
        count++;
      }
    }

    for (String berry : new String[] { "overworld_berrybush_raspberry", "overworld_berrybush_blueberry", "overworld_berrybush_blackberry",
        "overworld_berrybush_maloberry", "nether_berrybush_blightberry", "nether_berrybush_duskberry", "nether_berrybush_skyberry",
        "nether_berrybush_stingberry" }) {
      Block berryBlock = findBlock("natura", berry);
      Item berryItem = findItem("natura", berry);
      if (berryBlock != null && berryItem != null) {
        DEFAULT_FARMER.addHarvestExlude(berryBlock);
        PickableFarmer farmer = new NaturaBerryFarmer(berryBlock, 0, 3, new ItemStack(berryItem, 1, 0));
        farmer.setRequiresFarmland(false);
        FarmersCommune.joinCommune(farmer);
        TreeHarvestUtil.addLeavesExcemption(berryBlock); // berry bushes are leaves, idiotic...
        count++;
      }
    }

    Block shroomSapling = findBlock("natura", "nether_glowshroom");
    Block shroomGreenBlock = findBlock("natura", "nether_green_large_glowshroom");
    Block shroomBlueBlock = findBlock("natura", "nether_blue_large_glowshroom");
    Block shroomPurpleBlock = findBlock("natura", "nether_purple_large_glowshroom");

    if (shroomSapling != null && shroomGreenBlock != null && shroomBlueBlock != null && shroomPurpleBlock != null) {
      final TreeFarmer shroomFarmer = new TreeFarmer(shroomSapling, shroomGreenBlock, shroomBlueBlock, shroomPurpleBlock);
      shroomFarmer.setIgnoreMeta(true);
      FarmersCommune.joinCommune(shroomFarmer);
      count++;
    }

    Block saguaroBlock = findBlock("natura", "saguaro");
    Item saguaroBabyItem = findItem("natura", "saguaro_baby");
    if (saguaroBlock != null && saguaroBabyItem != null) {
      FarmersCommune.joinCommune(new StemFarmer(saguaroBlock, new ItemStack(saguaroBabyItem)) {
        @Override
        public boolean canHarvest(TileFarmStation farm, BlockPos bc, Block block, IBlockState meta) {
          return false;
        }
      });
      count++;
    }

    if (count == 12) {
      Log.info("Farming Station: Natura integration fully loaded");
    } else if (count == 0) {
      Log.info("Farming Station: Natura integration not loaded");
    } else {
      Log.info("Farming Station: Natura integration partially loaded (" + count + " of 12)");
    }
  }

  private static void addMFR() {
    String mod = "MineFactoryReloaded";
    String blockName = "rubberwood.sapling";
    if (Block.REGISTRY.containsKey(new ResourceLocation(mod, blockName))) {
      Block cropBlock = Block.REGISTRY.getObject(new ResourceLocation(mod, blockName));
      FarmersCommune.joinCommune(new TreeFarmer(cropBlock, Block.REGISTRY.getObject(new ResourceLocation(mod, "rubberwood.log"))));
    }
  }

  private static void addIC2() {
    // classic's not yet available for >1.7. wait for it before throwing away the old stuff
    // RubberTreeFarmerIC2 rtf = new RubberTreeFarmerIC2();
    // if(rtf.isValid()) {
    // FarmersCommune.joinCommune(rtf);
    // }
    RubberTreeFarmer farmer = RubberTreeFarmerIC2exp.create();
    if (farmer != null) {
      FarmersCommune.joinCommune(farmer);
    }
  }

  private static void addExtraUtilities2() {
    CustomSeedFarmer farmer = addSeed("extrautils2", "EnderLilly", "EnderLilly");
    if (farmer != null) {
      farmer.setIgnoreGroundCanSustainCheck(true);
      farmer.setRequiresFarmland(false); // disables tilling
      farmer.setCheckGroundForFarmland(true); // extra check needed when not tilling
      farmer.clearTilledBlocks(); // remove farmland
      farmer.addTilledBlock(Blocks.DIRT);
      farmer.addTilledBlock(Blocks.GRASS);
      farmer.addTilledBlock(Blocks.END_STONE);
    }
    farmer = addSeed("extrautils2", "RedOrchid", "RedOrchid");
    if (farmer != null) {
      farmer.setIgnoreGroundCanSustainCheck(true);
      farmer.setRequiresFarmland(false); // disables tilling
      farmer.setCheckGroundForFarmland(true); // extra check needed when not tilling
      farmer.clearTilledBlocks(); // remove farmland
      farmer.addTilledBlock(Blocks.REDSTONE_ORE);
      farmer.addTilledBlock(Blocks.LIT_REDSTONE_ORE);
    }
  }

  private static void addFlowers() {
    FarmersCommune.joinCommune(new FlowerPicker(FLOWERS));
  }

  private static void addImmersiveEngineering() {
    HempFarmerIE farmer = HempFarmerIE.create();
    if (farmer != null) {
      FarmersCommune.joinCommune(farmer);
    }
  }

  private static void addTechreborn() {
    RubberTreeFarmer farmer = RubberTreeFarmerTechReborn.create();
    if (farmer != null) {
      FarmersCommune.joinCommune(farmer);
    }
  }

  private FarmersRegistry() {
  }

}
