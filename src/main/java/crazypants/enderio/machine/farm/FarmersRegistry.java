package crazypants.enderio.machine.farm;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;

public final class FarmersRegistry {

  public static void addFarmers() {
    //vanilla
    FarmersComune.instance.joinComune(new SeedFarmer(Blocks.potatoes, new ItemStack(Items.potato)));
    FarmersComune.instance.joinComune(new SeedFarmer(Blocks.wheat, new ItemStack(Items.wheat_seeds)));
    FarmersComune.instance.joinComune(new SeedFarmer(Blocks.carrots, new ItemStack(Items.carrot)));
    FarmersComune.instance.joinComune(new NetherWartFarmer());
    FarmersComune.instance.joinComune(new StemFarmer(Blocks.reeds, new ItemStack(Items.reeds)));
    FarmersComune.instance.joinComune(new StemFarmer(Blocks.cactus, new ItemStack(Blocks.cactus)));
    FarmersComune.instance.joinComune(new TreeFarmer(Blocks.sapling, Blocks.log));
    FarmersComune.instance.joinComune(new TreeFarmer(Blocks.sapling, Blocks.log2));

    addExtraUtilities();
    addNutura();
    addTiC();
    addStillHungry();
  }

  private static void addStillHungry() {
    String mod = "stillhungry";          
    addSeed(mod, "strawberryBlock", "StillHungry_strawberrySeed");        
    addPickable(mod, "grapeBlock", "StillHungry_grapeSeed");
    addSeed(mod, "riceBlock", "StillHungry_riceSeed");
  }


  private static void addExtraUtilities() {
    String mod = "ExtraUtilities";
    String name = "plant/ender_lilly";
    addSeed(mod, name, name);
  }
  
  private static void addPickable(String mod, String blockName, String itemName) {
    Block cropBlock = GameRegistry.findBlock(mod, blockName);
    if(cropBlock != null) {
      Item seedItem = GameRegistry.findItem(mod, itemName);
      if(seedItem != null) {
        FarmersComune.instance.joinComune(new PickableFarmer(cropBlock, new ItemStack(seedItem)));
      }
    }
  }
  
  private static void addSeed(String mod, String blockName, String itemName) {
    Block cropBlock = GameRegistry.findBlock(mod, blockName);
    if(cropBlock != null) {
      Item seedItem = GameRegistry.findItem(mod, itemName);
      if(seedItem != null) {
        FarmersComune.instance.joinComune(new SeedFarmer(cropBlock, new ItemStack(seedItem)));
      }
    }
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
          FarmersComune.instance.joinComune(farmer);
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
          FarmersComune.instance.joinComune(farmer);
        }
      }
    }

  }

  private static void addNutura() {
    String mod = "Natura";
    String blockName = "N Crops";

    Block cropBlock = GameRegistry.findBlock(mod, blockName);
    if(cropBlock != null) {
      Item seedItem = GameRegistry.findItem(mod, "barley.seed");
      if(seedItem != null) {
        //barley
        FarmersComune.instance.joinComune(new SeedFarmer(cropBlock, 3, new ItemStack(seedItem)));
        // cotton
        FarmersComune.instance.joinComune(new PickableFarmer(cropBlock, 4, 8, new ItemStack(seedItem, 1, 1)));
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
          FarmersComune.instance.joinComune(farmer);
        }
      }
    }
  }

  private FarmersRegistry() {
  }

}
