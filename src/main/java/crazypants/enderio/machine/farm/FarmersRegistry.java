package crazypants.enderio.machine.farm;

import crazypants.enderio.config.Config;
import crazypants.enderio.machine.farm.farmers.*;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;

public final class FarmersRegistry {

  public static final PlantableFarmer DEFAULT_FARMER = new PlantableFarmer();
  
  public static void addFarmers() {

    addExtraUtilities();
    addNatura();
    addTiC();
    addStillHungry();
    addIC2();
    addMFR();
    addThaumcraft();
    addFlowers();
    addGrowableOres();

    FarmersCommune.joinCommune(new StemFarmer(Blocks.reeds, new ItemStack(Items.reeds)));
    FarmersCommune.joinCommune(new StemFarmer(Blocks.cactus, new ItemStack(Blocks.cactus)));
    FarmersCommune.joinCommune(new TreeFarmer(Blocks.sapling, Blocks.log));
    FarmersCommune.joinCommune(new TreeFarmer(Blocks.sapling, Blocks.log2));
    FarmersCommune.joinCommune(new TreeFarmer(true,Blocks.red_mushroom, Blocks.red_mushroom_block));
    FarmersCommune.joinCommune(new TreeFarmer(true,Blocks.brown_mushroom, Blocks.brown_mushroom_block));
    //special case of plantables to get spacing correct
    FarmersCommune.joinCommune(new MelonFarmer(Blocks.melon_stem, Blocks.melon_block, new ItemStack(Items.melon_seeds)));
    FarmersCommune.joinCommune(new MelonFarmer(Blocks.pumpkin_stem, Blocks.pumpkin, new ItemStack(Items.pumpkin_seeds)));
    //'BlockNetherWart' is not an IGrowable
    FarmersCommune.joinCommune(new NetherWartFarmer());
    //Cocoa is odd
    FarmersCommune.joinCommune(new CocoaFarmer());
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

  private static void addThaumcraft()
  {
    String mod = "Thaumcraft";
    String manaBean = "ItemManaBean";
    String manaPod = "blockManaPod";
    Block block = GameRegistry.findBlock(mod,manaPod);
    Item item = GameRegistry.findItem(mod,manaBean);
    if (Config.farmManaBeansEnabled && block!=null && item!=null)
    {
      FarmersCommune.joinCommune(new ManaBeanFarmer(block, new ItemStack(item)));
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

  private static void addFlowers() {
    FarmersCommune.joinCommune(new FlowerPicker().add(
        GameRegistry.findBlock("minecraft", "yellow_flower"), 
        GameRegistry.findBlock("minecraft", "red_flower"), 
        GameRegistry.findBlock("BiomesOPlenty", "flowers"), 
        GameRegistry.findBlock("BiomesOPlenty", "flowers2"), 
        GameRegistry.findBlock("Botany", "flower"), 
        GameRegistry.findBlock("Botania", "flower") ) );
  }

  private static void addGrowableOres() {
    String mod = "B0bGrowsOre";
    String[] growableOresList = {
            "coalReed", // Coal Ore Reed
            "copperReed",  // Copper Ore Reed
            "goldReed",  // Gold Ore Reed
            "ironReed",  // Iron Ore Reed
            "oreGrowableAir",  // Air Infused Stone Reed
            "oreGrowableAluminium",  // Aluminum Ore Reed
            "oreGrowableAmberBop",  // Amber Ore Reed
            "oreGrowableAmethyst",  // Amethyst Ore Reed
            "oreGrowableApatite",  // Apatite Ore Reed
            "oreGrowableArdite",  // Ardite Ore Reed
            "oreGrowableBlaze",  // Blaze Reed
            "oreGrowableCheese",  // Cheese Ore Reed
            "oreGrowableCinnibar",  // Cinnibar Ore Reed
            "oreGrowableClay",  // Clay Reed
            "oreGrowableCobalt",  // Cobalt Ore Reed
            "oreGrowableDesh",  // Desh Ore Reed
            "oreGrowableDiamond",  // Diamond Ore Reed
            "oreGrowableEarth",  // Earth Infused Stone Reed
            "oreGrowableEmerald",  // Emerald Ore Reed
            "oreGrowableEnder",  // Ender Reed
            "oreGrowableEntropy",  // Entropy Infused Stone Reed
            "oreGrowableFerrous",  // Ferrous Ore Reed
            "oreGrowableFire",  // Fire Infused Stone Reed
            "oreGrowableGlowstone",  // Glowstone Reed
            "oreGrowableIlmenite",  // Ilmenite Ore Reed
            "oreGrowableLapis",  // Lapis Lazuli Ore Reed
            "oreGrowableLead",  // Lead Ore Reed
            "oreGrowableMalachite",  // Malachite Ore Reed
            "oreGrowableMithril",  // Mithril Ore Reed
            "oreGrowableNQuartz",  // Nether Quartz Ore Reed
            "oreGrowableObsidian",  // Obsidian Reed
            "oreGrowableOrder",  // Order Infused Stone Reed
            "oreGrowablePeridot",  // Peridot Ore Reed
            "oreGrowablePlatinum",  // Platinum Ore Reed
            "oreGrowableQuartz",  // Certus Quartz Ore Reed
            "oreGrowableRedstone",  // Redstone Ore Reed
            "oreGrowableRuby",  // Ruby Ore Reed
            "oreGrowableSaltpeterM",  // Saltpeter Ore Reed
            "oreGrowableSapphire",  // Sapphire Ore Reed
            "oreGrowableSilicon",  // Silicon Ore Reed
            "oreGrowableSulfur",  // Sulfur Ore Reed
            "oreGrowableTanzanite",  // Tanzanite Ore Reed
            "oreGrowableTopaz",  // Topaz Ore Reed
            "oreGrowableUranium",  // Uranium Ore Reed
            "oreGrowableWater",  // Water Infused Stone Reed
            "oreGrowableYellorium",  // Yellorite Ore Reed
            "oreGrowableZinc",  // Zinc Ore Reed
            "silverReed",  // Silver Ore Reed
            "tinReed"};  // Tin Ore Reed

      for (String  growableOre : growableOresList) {
          Block growableOresBlock = GameRegistry.findBlock(mod, growableOre);
          Item growableOresItem = GameRegistry.findItem(mod, growableOre);
          if (growableOresBlock !=null && growableOresItem !=null)
              FarmersCommune.joinCommune(new StemFarmer( growableOresBlock, new ItemStack(growableOresItem)));
      }
  }

  
  private FarmersRegistry() {
  }

}
