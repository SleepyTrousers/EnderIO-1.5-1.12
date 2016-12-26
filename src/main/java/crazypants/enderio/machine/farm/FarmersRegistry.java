package crazypants.enderio.machine.farm;

import crazypants.enderio.machine.farm.farmers.CocoaFarmer;
import crazypants.enderio.machine.farm.farmers.CustomSeedFarmer;
import crazypants.enderio.machine.farm.farmers.FarmersCommune;
import crazypants.enderio.machine.farm.farmers.FlowerPicker;
import crazypants.enderio.machine.farm.farmers.ForestryFarmer;
import crazypants.enderio.machine.farm.farmers.HempFarmerIE;
import crazypants.enderio.machine.farm.farmers.MelonFarmer;
import crazypants.enderio.machine.farm.farmers.NaturaBerryFarmer;
import crazypants.enderio.machine.farm.farmers.NetherWartFarmer;
import crazypants.enderio.machine.farm.farmers.OredictTreeFarmer;
import crazypants.enderio.machine.farm.farmers.PickableFarmer;
import crazypants.enderio.machine.farm.farmers.PlantableFarmer;
import crazypants.enderio.machine.farm.farmers.RubberTreeFarmer;
import crazypants.enderio.machine.farm.farmers.RubberTreeFarmerIC2exp;
import crazypants.enderio.machine.farm.farmers.RubberTreeFarmerTechReborn;
import crazypants.enderio.machine.farm.farmers.StemFarmer;
import crazypants.enderio.machine.farm.farmers.TreeFarmer;
import crazypants.util.Things;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;

public final class FarmersRegistry {

  private static final Things SAPLINGS = new Things("treeSapling");
  private static final Things WOODS = new Things("logWood", "blockSlimeCongealed");
  private static final Things FLOWERS = new Things("block:BiomesOPlenty:flowers", "block:BiomesOPlenty:flowers2", "block:Botany:flower", "block:botania:flower",
      "block:botania:doubleFlower1", "block:botania:doubleFlower2", "block:botania:shinyFlower", "block:botania:mushroom").add(Blocks.YELLOW_FLOWER)
          .add(Blocks.RED_FLOWER);

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

  private static void addNatura() {
    String mod = "Natura";

    String blockName = "N Crops";
    String seedName = "barley.seed";
    if (Block.REGISTRY.containsKey(new ResourceLocation(mod, blockName))) {
      Block cropBlock = Block.REGISTRY.getObject(new ResourceLocation(mod, blockName));
      DEFAULT_FARMER.addHarvestExlude(cropBlock);
      Item seedItem = Item.REGISTRY.getObject(new ResourceLocation(mod, seedName));
      if (seedItem != null) {
        // barley
        FarmersCommune.joinCommune(new CustomSeedFarmer(cropBlock, 3, new ItemStack(seedItem)));
        // cotton
        FarmersCommune.joinCommune(new PickableFarmer(cropBlock, 4, 8, new ItemStack(seedItem, 1, 1)));
      }
    }

    blockName = "BerryBush";
    if (Block.REGISTRY.containsKey(new ResourceLocation(mod, blockName))) {
      Block cropBlock = Block.REGISTRY.getObject(new ResourceLocation(mod, blockName));
      Item seedItem = Item.REGISTRY.getObject(new ResourceLocation(mod, blockName));
      if (seedItem != null) {
        for (int i = 0; i < 4; i++) {
          PickableFarmer farmer = new NaturaBerryFarmer(cropBlock, i, 12 + i, new ItemStack(seedItem, 1, 12 + i));
          farmer.setRequiresFarmland(false);
          FarmersCommune.joinCommune(farmer);
        }
      }
    }

    blockName = "florasapling";
    if (Block.REGISTRY.containsKey(new ResourceLocation(mod, blockName))) {
      Block cropBlock = Block.REGISTRY.getObject(new ResourceLocation(mod, blockName));
      FarmersCommune.joinCommune(new TreeFarmer(cropBlock, Block.REGISTRY.getObject(new ResourceLocation(mod, "tree")),
          Block.REGISTRY.getObject(new ResourceLocation(mod, "willow")), Block.REGISTRY.getObject(new ResourceLocation(mod, "Dark Tree"))));
    }

    blockName = "Rare Sapling";
    if (Block.REGISTRY.containsKey(new ResourceLocation(mod, blockName))) {
      Block cropBlock = Block.REGISTRY.getObject(new ResourceLocation(mod, blockName));
      FarmersCommune.joinCommune(new TreeFarmer(cropBlock, Block.REGISTRY.getObject(new ResourceLocation(mod, "Rare Tree"))));
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
