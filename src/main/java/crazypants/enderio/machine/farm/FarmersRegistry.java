package crazypants.enderio.machine.farm;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.machine.farm.farmers.CustomSeedFarmer;
import crazypants.enderio.machine.farm.farmers.FarmersCommune;
import crazypants.enderio.machine.farm.farmers.MelonFarmer;
import crazypants.enderio.machine.farm.farmers.NaturaBerryFarmer;
import crazypants.enderio.machine.farm.farmers.NetherWartFarmer;
import crazypants.enderio.machine.farm.farmers.PickableFarmer;
import crazypants.enderio.machine.farm.farmers.PlantableFarmer;
import crazypants.enderio.machine.farm.farmers.RubberTreeFarmerIC2;
import crazypants.enderio.machine.farm.farmers.StemFarmer;
import crazypants.enderio.machine.farm.farmers.TreeFarmer;

public final class FarmersRegistry {

  public static final PlantableFarmer DEFAULT_FARMER = new PlantableFarmer();
  
  public static void addFarmers() {

    addExtraUtilities();
    addNatura();
    addTiC();
    addStillHungry();
    addIC2();
    addMFR();
    addBoP();

    FarmersCommune.joinCommune(new StemFarmer(Blocks.reeds, new ItemStack(Items.reeds)));
    FarmersCommune.joinCommune(new StemFarmer(Blocks.cactus, new ItemStack(Blocks.cactus)));
    FarmersCommune.joinCommune(new TreeFarmer(Blocks.sapling, Blocks.log));
    FarmersCommune.joinCommune(new TreeFarmer(Blocks.sapling, Blocks.log2));
    //special case of plantables to get spacing correct
    FarmersCommune.joinCommune(new MelonFarmer(Blocks.melon_stem, Blocks.melon_block, new ItemStack(Items.melon_seeds)));
    FarmersCommune.joinCommune(new MelonFarmer(Blocks.pumpkin_stem, Blocks.pumpkin, new ItemStack(Items.pumpkin_seeds)));
    //'BlockNetherWart' is not an IGrowable
    FarmersCommune.joinCommune(new NetherWartFarmer());    
    //Handles all 'vanilla' style crops
    FarmersCommune.joinCommune(DEFAULT_FARMER);
  }

  public static void addPickable(String mod, String blockName, String itemName) {
    Block cropBlock = GameRegistry.findBlock(mod, blockName);
    if(cropBlock != null) {
      Item seedItem = GameRegistry.findItem(mod, itemName);
      if(seedItem != null) {
        FarmersCommune.joinCommune(new PickableFarmer(cropBlock, new ItemStack(seedItem)));
      }
    }
  }

  public static CustomSeedFarmer addSeed(String mod, String blockName, String itemName, Block... extraFarmland) {
    Block cropBlock = GameRegistry.findBlock(mod, blockName);
    if(cropBlock != null) {
      Item seedItem = GameRegistry.findItem(mod, itemName);
      if(seedItem != null) {
        CustomSeedFarmer farmer = new CustomSeedFarmer(cropBlock, new ItemStack(seedItem));
        if(extraFarmland != null) {
          for (Block farmland : extraFarmland) {
            if(farmland != null) {
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

  private static void addTiC() {
    String mod = "TConstruct";
    String blockName = "ore.berries.two";

    Block cropBlock = GameRegistry.findBlock(mod, blockName);
    if(cropBlock != null) {
      Item seedItem = GameRegistry.findItem(mod, blockName);
      if(seedItem != null) {
        for (int i = 0; i < 2; i++) {
          PickableFarmer farmer = new NaturaBerryFarmer(cropBlock, i, 12 + i, new ItemStack(seedItem, 1, 8 + i));
          farmer.setRequiresFarmland(false);
          FarmersCommune.joinCommune(farmer);
        }
      }
    }

    blockName = "ore.berries.one";
    cropBlock = GameRegistry.findBlock(mod, blockName);
    if(cropBlock != null) {
      Item seedItem = GameRegistry.findItem(mod, blockName);
      if(seedItem != null) {
        for (int i = 0; i < 4; i++) {
          PickableFarmer farmer = new NaturaBerryFarmer(cropBlock, i, 12 + i, new ItemStack(seedItem, 1, 8 + i));
          farmer.setRequiresFarmland(false);
          FarmersCommune.joinCommune(farmer);
        }
      }
    }

  }

  private static void addNatura() {
    String mod = "Natura";
    String blockName = "N Crops";

    Block cropBlock = GameRegistry.findBlock(mod, blockName);
    if(cropBlock != null) {
      DEFAULT_FARMER.addHarvestExlude(cropBlock);
      Item seedItem = GameRegistry.findItem(mod, "barley.seed");
      if(seedItem != null) {
        //barley
        FarmersCommune.joinCommune(new CustomSeedFarmer(cropBlock, 3, new ItemStack(seedItem)));
        // cotton
        FarmersCommune.joinCommune(new PickableFarmer(cropBlock, 4, 8, new ItemStack(seedItem, 1, 1)));
      }
    }

    blockName = "BerryBush";
    cropBlock = GameRegistry.findBlock(mod, blockName);
    if(cropBlock != null) {
      Item seedItem = GameRegistry.findItem(mod, blockName);
      if(seedItem != null) {
        for (int i = 0; i < 4; i++) {
          PickableFarmer farmer = new NaturaBerryFarmer(cropBlock, i, 12 + i, new ItemStack(seedItem, 1, 12 + i));
          farmer.setRequiresFarmland(false);
          FarmersCommune.joinCommune(farmer);
        }
      }
    }

    blockName = "florasapling";
    Block saplingBlock = GameRegistry.findBlock(mod, blockName);
    if(saplingBlock != null) {
      FarmersCommune.joinCommune(new TreeFarmer(saplingBlock,
          GameRegistry.findBlock(mod, "tree"),
          GameRegistry.findBlock(mod, "willow"),
          GameRegistry.findBlock(mod, "Dark Tree")));
    }
    blockName = "Rare Sapling";
    saplingBlock = GameRegistry.findBlock(mod, blockName);
    if(saplingBlock != null) {
      FarmersCommune.joinCommune(new TreeFarmer(saplingBlock, GameRegistry.findBlock(mod, "Rare Tree")));
    }

  }
  
  private static void addMFR() {
    String mod = "MineFactoryReloaded";
    String blockName = "rubberwood.sapling";
    Block saplingBlock = GameRegistry.findBlock(mod, blockName);
    if(saplingBlock != null) {
      FarmersCommune.joinCommune(new TreeFarmer(saplingBlock, GameRegistry.findBlock(mod, "rubberwood.log")));
    }
    
  }

  private static void addIC2() {
    RubberTreeFarmerIC2 rtf = new RubberTreeFarmerIC2();
    if(rtf.isValid()) {
      FarmersCommune.joinCommune(rtf);
    }
  }

  private static void addStillHungry() {
    String mod = "stillhungry";
    addPickable(mod, "grapeBlock", "StillHungry_grapeSeed");
  }

  private static void addExtraUtilities() {
    String mod = "ExtraUtilities";
    String name = "plant/ender_lilly";

    CustomSeedFarmer farmer = addSeed(mod, name, name, Blocks.end_stone, GameRegistry.findBlock(mod, "decorativeBlock1"));
    if(farmer != null) {
      farmer.setIgnoreGroundCanSustainCheck(true);
    }
  }

  //Support for Biomes o' Plenty plants
  private static void addBoP() {
    String mod = "BiomesOPlenty";
    String blockName = "saplings";
    String blockColorizedName = "colorizedSaplings";

    Block saplingBlock = GameRegistry.findBlock(mod, blockName);
    Block colorizedSaplingBlock = GameRegistry.findBlock(mod, blockColorizedName);

    if (saplingBlock != null) {
      FarmersCommune.joinCommune(new TreeFarmer(saplingBlock,
              GameRegistry.findBlock(mod, "logs1"),
              GameRegistry.findBlock(mod, "logs2"),
              GameRegistry.findBlock(mod, "logs3"),
              GameRegistry.findBlock(mod, "logs4"),
              GameRegistry.findBlock(mod, "bamboo")));
    }

    if (colorizedSaplingBlock != null){
      FarmersCommune.joinCommune(new TreeFarmer(colorizedSaplingBlock,
              GameRegistry.findBlock(mod, "logs1"),
              GameRegistry.findBlock(mod, "logs2"),
              GameRegistry.findBlock(mod, "logs3"),
              GameRegistry.findBlock(mod, "logs4"),
              GameRegistry.findBlock(mod, "bamboo")));
    }
  }

  private FarmersRegistry() {
  }

}
