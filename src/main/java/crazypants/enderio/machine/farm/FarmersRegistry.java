package crazypants.enderio.machine.farm;

import java.util.Iterator;
import java.util.regex.Pattern;

import crazypants.enderio.config.Config;
import crazypants.enderio.machine.farm.farmers.CustomSeedFarmer;
import crazypants.enderio.machine.farm.farmers.FarmersCommune;
import crazypants.enderio.machine.farm.farmers.FlowerPicker;
import crazypants.enderio.machine.farm.farmers.ManaBeanFarmer;
import crazypants.enderio.machine.farm.farmers.MelonFarmer;
import crazypants.enderio.machine.farm.farmers.NaturaBerryFarmer;
import crazypants.enderio.machine.farm.farmers.NetherWartFarmer;
import crazypants.enderio.machine.farm.farmers.PickableFarmer;
import crazypants.enderio.machine.farm.farmers.PlantableFarmer;
import crazypants.enderio.machine.farm.farmers.RubberTreeFarmerIC2;
import crazypants.enderio.machine.farm.farmers.StemFarmer;
import crazypants.enderio.machine.farm.farmers.TreeFarmer;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;

public final class FarmersRegistry {

  public static final PlantableFarmer DEFAULT_FARMER = new PlantableFarmer();
  
  public static void addFarmers() {

//    addExtraUtilities();
//    addNatura();
//    addTiC();
//    addStillHungry();
//    addIC2();
//    addMFR();
//    addThaumcraft();
//    addFlowers();
//    addGrowableOres();
//    addImmersiveEngineering();
//
    FarmersCommune.joinCommune(new StemFarmer(Blocks.REEDS, new ItemStack(Items.REEDS)));
    FarmersCommune.joinCommune(new StemFarmer(Blocks.CACTUS, new ItemStack(Blocks.CACTUS)));
    FarmersCommune.joinCommune(new TreeFarmer(Blocks.SAPLING, Blocks.LOG));
    FarmersCommune.joinCommune(new TreeFarmer(Blocks.SAPLING, Blocks.LOG2));
    FarmersCommune.joinCommune(new TreeFarmer(true,Blocks.RED_MUSHROOM, Blocks.RED_MUSHROOM_BLOCK));
    FarmersCommune.joinCommune(new TreeFarmer(true,Blocks.BROWN_MUSHROOM, Blocks.BROWN_MUSHROOM_BLOCK));
    //special case of plantables to get spacing correct
    FarmersCommune.joinCommune(new MelonFarmer(Blocks.MELON_STEM, Blocks.MELON_BLOCK, new ItemStack(Items.MELON_SEEDS)));
    FarmersCommune.joinCommune(new MelonFarmer(Blocks.PUMPKIN_STEM, Blocks.PUMPKIN, new ItemStack(Items.PUMPKIN_SEEDS)));
    //'BlockNetherWart' is not an IGrowable
    FarmersCommune.joinCommune(new NetherWartFarmer());
    //Cocoa is odd
//    FarmersCommune.joinCommune(new CocoaFarmer());
    //Handles all 'vanilla' style crops
    FarmersCommune.joinCommune(DEFAULT_FARMER);
  }

  public static void addPickable(String mod, String blockName, String itemName) {    
    Block cropBlock = Block.REGISTRY.getObject(new ResourceLocation(mod, blockName));
    if(cropBlock != null) {      
      Item seedItem = Item.REGISTRY.getObject(new ResourceLocation(mod, itemName));
      if(seedItem != null) {
        FarmersCommune.joinCommune(new PickableFarmer(cropBlock, new ItemStack(seedItem)));
      }
    }
  }

  public static CustomSeedFarmer addSeed(String mod, String blockName, String itemName, Block... extraFarmland) {
    Block cropBlock = Block.REGISTRY.getObject(new ResourceLocation(mod, blockName));
    if(cropBlock != null) {
      Item seedItem = Item.REGISTRY.getObject(new ResourceLocation(mod, itemName));
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

    Block cropBlock = Block.REGISTRY.getObject(new ResourceLocation(mod, blockName));
    if(cropBlock != null) {
      Item seedItem = Item.REGISTRY.getObject(new ResourceLocation(mod, blockName));
      if(seedItem != null) {
        for (int i = 0; i < 2; i++) {
          PickableFarmer farmer = new NaturaBerryFarmer(cropBlock, i, 12 + i, new ItemStack(seedItem, 1, 8 + i));
          farmer.setRequiresFarmland(false);
          FarmersCommune.joinCommune(farmer);
        }
      }
    }

    blockName = "ore.berries.one";
    cropBlock = Block.REGISTRY.getObject(new ResourceLocation(mod, blockName));
    if(cropBlock != null) {
      Item seedItem = Item.REGISTRY.getObject(new ResourceLocation(mod, blockName));
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

    Block cropBlock =Block.REGISTRY.getObject(new ResourceLocation(mod, blockName));
    if(cropBlock != null) {
      DEFAULT_FARMER.addHarvestExlude(cropBlock);
      Item seedItem = Item.REGISTRY.getObject(new ResourceLocation(mod, "barley.seed"));
      if(seedItem != null) {
        //barley
        FarmersCommune.joinCommune(new CustomSeedFarmer(cropBlock, 3, new ItemStack(seedItem)));
        // cotton
        FarmersCommune.joinCommune(new PickableFarmer(cropBlock, 4, 8, new ItemStack(seedItem, 1, 1)));
      }
    }

    blockName = "BerryBush";
    cropBlock = Block.REGISTRY.getObject(new ResourceLocation(mod, blockName));
    if(cropBlock != null) {
      Item seedItem = Item.REGISTRY.getObject(new ResourceLocation(mod, blockName));
      if(seedItem != null) {
        for (int i = 0; i < 4; i++) {
          PickableFarmer farmer = new NaturaBerryFarmer(cropBlock, i, 12 + i, new ItemStack(seedItem, 1, 12 + i));
          farmer.setRequiresFarmland(false);
          FarmersCommune.joinCommune(farmer);
        }
      }
    }

    blockName = "florasapling";
    Block saplingBlock = Block.REGISTRY.getObject(new ResourceLocation(mod, blockName));
    if(saplingBlock != null) {
      FarmersCommune.joinCommune(new TreeFarmer(saplingBlock,
          Block.REGISTRY.getObject(new ResourceLocation(mod, "tree")),
          Block.REGISTRY.getObject(new ResourceLocation(mod, "willow")),
          Block.REGISTRY.getObject(new ResourceLocation(mod, "Dark Tree"))));
    }
    blockName = "Rare Sapling";
    saplingBlock = Block.REGISTRY.getObject(new ResourceLocation(mod, blockName));
    if(saplingBlock != null) {
      FarmersCommune.joinCommune(new TreeFarmer(saplingBlock, Block.REGISTRY.getObject(new ResourceLocation(mod, "Rare Tree"))));
    }

  }

  private static void addThaumcraft()
  {
    String mod = "Thaumcraft";
    String manaBean = "ItemManaBean";
    String manaPod = "blockManaPod";
    Block block = Block.REGISTRY.getObject(new ResourceLocation(mod, manaPod));
    Item item = Item.REGISTRY.getObject(new ResourceLocation(mod,manaBean));
    if (Config.farmManaBeansEnabled && block!=null && item!=null)
    {
      FarmersCommune.joinCommune(new ManaBeanFarmer(block, new ItemStack(item)));
    }
  }
  
  private static void addMFR() {
    String mod = "MineFactoryReloaded";
    String blockName = "rubberwood.sapling";
    Block saplingBlock = Block.REGISTRY.getObject(new ResourceLocation(mod, blockName));
    if(saplingBlock != null) {
      FarmersCommune.joinCommune(new TreeFarmer(saplingBlock, Block.REGISTRY.getObject(new ResourceLocation(mod, "rubberwood.log"))));
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

    CustomSeedFarmer farmer = addSeed(mod, name, name, Blocks.END_STONE, Block.REGISTRY.getObject(new ResourceLocation(mod, "decorativeBlock1")));
    if(farmer != null) {
      farmer.setIgnoreGroundCanSustainCheck(true);
    }
  }

  private static void addFlowers() {
    
    FarmersCommune.joinCommune(new FlowerPicker().add(
        Block.REGISTRY.getObject(new ResourceLocation("minecraft", "yellow_flower")), 
        Block.REGISTRY.getObject(new ResourceLocation("minecraft", "red_flower")), 
        Block.REGISTRY.getObject(new ResourceLocation("BiomesOPlenty", "flowers")), 
        Block.REGISTRY.getObject(new ResourceLocation("BiomesOPlenty", "flowers2")), 
        Block.REGISTRY.getObject(new ResourceLocation("Botany", "flower")), 
        Block.REGISTRY.getObject(new ResourceLocation("Botania", "flower")) ) );
  }
  
  private static void addGrowableOres() {
    String mod = "B0bGrowsOre";
    if (!Loader.isModLoaded(mod)) {
      return;
    }
    Pattern[] growableOres = { Pattern.compile("(.+)Reed"), Pattern.compile("oreGrowable(.+)") };

    Iterator<Block> blockIter = Block.REGISTRY.iterator();
    while (blockIter.hasNext()) {
      Block block = blockIter.next();
      String name = Block.REGISTRY.getNameForObject(block).toString();
      if (name != null && name.startsWith(mod)) {
        for (Pattern blockPattern : growableOres) {
          if (blockPattern.matcher(name).find()) {
            FarmersCommune.joinCommune(new StemFarmer(block, new ItemStack(block)));
          }
        }
      }
    }
  }

  private static void addImmersiveEngineering() {
    Block hemp = Block.REGISTRY.getObject(new ResourceLocation("ImmersiveEngineering", "hemp"));
    Item hempSeed = Item.REGISTRY.getObject(new ResourceLocation("ImmersiveEngineering", "hemp"));
    if (hemp != null && hempSeed != null) {
      FarmersCommune.joinCommune(new StemFarmer(hemp, new ItemStack(hempSeed)));
    }
  }
  
  private FarmersRegistry() {
  }

}
