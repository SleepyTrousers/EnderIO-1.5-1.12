package crazypants.enderio.machine.farm;

import crazypants.enderio.machine.farm.farmers.CustomSeedFarmer;
import crazypants.enderio.machine.farm.farmers.FarmersCommune;
import crazypants.enderio.machine.farm.farmers.FlowerPicker;
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
import net.minecraftforge.fml.common.registry.GameRegistry.ItemStackHolder;

public final class FarmersRegistry {

  public static final PlantableFarmer DEFAULT_FARMER = new PlantableFarmer();
  
  public static void addFarmers() {

    addTechreborn();
    addExtraUtilities2();
    addNatura();
    addIC2();
    addMFR();
    addFlowers();
    addImmersiveEngineering();

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
    if (Block.REGISTRY.containsKey(new ResourceLocation(mod, blockName))) {
      Block cropBlock = Block.REGISTRY.getObject(new ResourceLocation(mod, blockName));
      Item seedItem = Item.REGISTRY.getObject(new ResourceLocation(mod, itemName));
      if(seedItem != null) {
        FarmersCommune.joinCommune(new PickableFarmer(cropBlock, new ItemStack(seedItem)));
      }
    }
  }

  public static CustomSeedFarmer addSeed(String mod, String blockName, String itemName, Block... extraFarmland) {
    if (Block.REGISTRY.containsKey(new ResourceLocation(mod, blockName))) {
      Block cropBlock = Block.REGISTRY.getObject(new ResourceLocation(mod, blockName));
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

  private static void addNatura() {
    String mod = "Natura";
    String blockName = "N Crops";

    if (Block.REGISTRY.containsKey(new ResourceLocation(mod, blockName))) {
      Block cropBlock = Block.REGISTRY.getObject(new ResourceLocation(mod, blockName));
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
    if (Block.REGISTRY.containsKey(new ResourceLocation(mod, blockName))) {
      Block cropBlock = Block.REGISTRY.getObject(new ResourceLocation(mod, blockName));
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
    if (Block.REGISTRY.containsKey(new ResourceLocation(mod, blockName))) {
      Block cropBlock = Block.REGISTRY.getObject(new ResourceLocation(mod, blockName));
      FarmersCommune.joinCommune(new TreeFarmer(cropBlock,
          Block.REGISTRY.getObject(new ResourceLocation(mod, "tree")),
          Block.REGISTRY.getObject(new ResourceLocation(mod, "willow")),
          Block.REGISTRY.getObject(new ResourceLocation(mod, "Dark Tree"))));
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
    RubberTreeFarmerIC2 rtf = new RubberTreeFarmerIC2();
    if(rtf.isValid()) {
      FarmersCommune.joinCommune(rtf);
    }
  }


  private static void addExtraUtilities2() {
    String mod = "extrautils2";
    String name = "EnderLilly";

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
  


  private static void addImmersiveEngineering() {
    if (Block.REGISTRY.containsKey(new ResourceLocation("ImmersiveEngineering", "hemp"))) {
      Block hemp = Block.REGISTRY.getObject(new ResourceLocation("ImmersiveEngineering", "hemp"));
      Item hempSeed = Item.REGISTRY.getObject(new ResourceLocation("ImmersiveEngineering", "hemp"));
      if (hempSeed != null) {
        FarmersCommune.joinCommune(new StemFarmer(hemp, new ItemStack(hempSeed)));
      }
    }
  }
  
  private FarmersRegistry() {
  }

  @ItemStackHolder("techreborn:techreborn.rubberlog")
  public static final ItemStack techreborn_rubberlog = null;

  @ItemStackHolder("techreborn:techreborn.rubbersapling")
  public static final ItemStack techreborn_rubbersapling = null;

  private static void addTechreborn() { // untested
    if (techreborn_rubberlog != null && techreborn_rubbersapling != null) {
      FarmersCommune.joinCommune(new TreeFarmer(techreborn_rubbersapling, techreborn_rubberlog));
    }

  }

}
