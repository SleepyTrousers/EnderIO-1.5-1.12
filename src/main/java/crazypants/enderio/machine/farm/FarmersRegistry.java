package crazypants.enderio.machine.farm;

import com.enderio.core.common.util.stackable.Things;
import crazypants.enderio.integration.bop.BoPUtil;
import crazypants.enderio.integration.botania.BotaniaUtil;
import crazypants.enderio.integration.botany.BotanyUtil;
import crazypants.enderio.integration.exu2.ExU2Util;
import crazypants.enderio.integration.forestry.ForestryUtil;
import crazypants.enderio.integration.ic2e.IC2eUtil;
import crazypants.enderio.integration.immersiveengineering.ImmersiveEngineeringUtil;
import crazypants.enderio.integration.mfr.MFRUtil;
import crazypants.enderio.integration.natura.NaturaUtil;
import crazypants.enderio.integration.techreborn.TechRebornUtil;
import crazypants.enderio.integration.tic.TicUtil;
import crazypants.enderio.machine.farm.farmers.*;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

import javax.annotation.Nonnull;

public final class FarmersRegistry {

  private static final Things SAPLINGS = new Things("treeSapling");
  private static final Things WOODS = new Things("logWood");
  private static final Things FLOWERS = new Things().add(Blocks.YELLOW_FLOWER).add(Blocks.RED_FLOWER);

  public static final PlantableFarmer DEFAULT_FARMER = new PlantableFarmer();

  public static void init(@Nonnull FMLPostInitializationEvent event) {

    TechRebornUtil.addTechreborn();
    ExU2Util.addExtraUtilities2();
    NaturaUtil.addNatura();
    IC2eUtil.addIC2();
    MFRUtil.addMFR();
    ImmersiveEngineeringUtil.addImmersiveEngineering();
    ForestryUtil.addForestry();
    BotaniaUtil.addBotania();
    BoPUtil.addBoP();
    BotanyUtil.addBotany();
    TicUtil.addTic();

    FarmersCommune.joinCommune(new FlowerPicker(FLOWERS));
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
    Block cropBlock = findBlock(mod, blockName);
    Item seedItem = findItem(mod, itemName);
    if (cropBlock != null && seedItem != null) {
      FarmersCommune.joinCommune(new PickableFarmer(cropBlock, new ItemStack(seedItem)));
    }
  }

  public static CustomSeedFarmer addSeed(String mod, String blockName, String itemName, Block... extraFarmland) {
    Block cropBlock = findBlock(mod, blockName);
    Item seedItem = findItem(mod, itemName);
    if (cropBlock != null && seedItem != null) {
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
    return null;
  }

  public static Block findBlock(String mod, String blockName) {
    final ResourceLocation name = new ResourceLocation(mod, blockName);
    if (Block.REGISTRY.containsKey(name)) {
      return Block.REGISTRY.getObject(name);
    }
    return null;
  }

  public static Item findItem(String mod, String itemName) {
    final ResourceLocation name = new ResourceLocation(mod, itemName);
    if (Item.REGISTRY.containsKey(name)) {
      return Item.REGISTRY.getObject(name);
    }
    return null;
  }

  public static void registerFlower(String... names) {
    for (String name : names) {
      FLOWERS.add(name);
    }
  }

  public static void registerSaplings(String... names) {
    for (String name : names) {
      SAPLINGS.add(name);
    }
  }

  public static void registerLogs(String... names) {
    for (String name : names) {
      WOODS.add(name);
    }
  }

  private FarmersRegistry() {
  }

}
