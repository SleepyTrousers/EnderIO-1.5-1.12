package crazypants.enderio.farming;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.config.Config;
import crazypants.enderio.farming.farmers.ChorusFarmer;
import crazypants.enderio.farming.farmers.CocoaFarmer;
import crazypants.enderio.farming.farmers.CustomSeedFarmer;
import crazypants.enderio.farming.farmers.FarmersCommune;
import crazypants.enderio.farming.farmers.FlowerPicker;
import crazypants.enderio.farming.farmers.MelonFarmer;
import crazypants.enderio.farming.farmers.NetherWartFarmer;
import crazypants.enderio.farming.farmers.OredictTreeFarmer;
import crazypants.enderio.farming.farmers.PickableFarmer;
import crazypants.enderio.farming.farmers.PlantableFarmer;
import crazypants.enderio.farming.farmers.StemFarmer;
import crazypants.enderio.farming.farmers.TreeFarmer;
import crazypants.enderio.init.ModObject;
import crazypants.enderio.integration.actuallyadditions.ActuallyadditionsUtil;
import crazypants.enderio.integration.bop.BoPUtil;
import crazypants.enderio.integration.botania.BotaniaUtil;
import crazypants.enderio.integration.botany.BotanyUtil;
import crazypants.enderio.integration.exu2.ExU2Util;
import crazypants.enderio.integration.forestry.ForestryUtil;
import crazypants.enderio.integration.gardencore.GardencoreUtil;
import crazypants.enderio.integration.ic2e.IC2eUtil;
import crazypants.enderio.integration.immersiveengineering.ImmersiveEngineeringUtil;
import crazypants.enderio.integration.magicalcrops.MagicalcropsUtil;
import crazypants.enderio.integration.metallurgy.MetallurgyUtil;
import crazypants.enderio.integration.mfr.MFRUtil;
import crazypants.enderio.integration.natura.NaturaUtil;
import crazypants.enderio.integration.techreborn.TechRebornUtil;
import crazypants.enderio.integration.tic.TicUtil;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

public final class FarmersRegistry {

  public static final @Nonnull Things slotItemsAxeTools = new Things().add(Items.WOODEN_HOE).add(Items.STONE_HOE).add(Items.IRON_HOE).add(Items.GOLDEN_HOE)
      .add(Items.DIAMOND_HOE).add(Config.farmHoes);
  public static final @Nonnull Things slotItemsHoeTools = new Things().add(Items.WOODEN_AXE).add(Items.STONE_AXE).add(Items.IRON_AXE).add(Items.GOLDEN_AXE)
      .add(Items.DIAMOND_AXE).add(ModObject.itemDarkSteelAxe.getItemNN());
  public static final @Nonnull Things slotItemsExtraTools = new Things().add(Items.SHEARS).add(ModObject.itemDarkSteelShears.getItemNN());
  public static final @Nonnull Things slotItemsSeeds = new Things("treeSapling").add(Items.WHEAT_SEEDS).add(Items.CARROT).add(Items.POTATO)
      .add(Blocks.RED_MUSHROOM)
      .add(Blocks.BROWN_MUSHROOM).add(Items.NETHER_WART).add(Blocks.SAPLING).add(Items.REEDS).add(Items.MELON_SEEDS).add(Items.PUMPKIN_SEEDS);
  public static final @Nonnull Things slotItemsProduce = new Things("logWood").add(new ItemStack(Blocks.LOG, 1, 0)).add(Blocks.WHEAT)
      .add(new ItemStack(Blocks.LEAVES, 1, 0)).add(Items.APPLE).add(Items.MELON).add(Blocks.PUMPKIN).add(Blocks.YELLOW_FLOWER).add(Blocks.RED_FLOWER);
  public static final @Nonnull Things slotItemsFertilizer = new Things().add(new ItemStack(Items.DYE, 1, 15));

  // TODO 1.11: move those treetaps somewhere else
  // slotItemsStacks3.addAll(TileFarmStation.TREETAPS.getItemStacks());

  private static final @Nonnull Things SAPLINGS = new Things("treeSapling");
  private static final @Nonnull Things WOODS = new Things("logWood");
  private static final @Nonnull Things FLOWERS = new Things().add(Blocks.YELLOW_FLOWER).add(Blocks.RED_FLOWER);

  public static final @Nonnull PlantableFarmer DEFAULT_FARMER = new PlantableFarmer();

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
    MetallurgyUtil.addMetallurgy();
    GardencoreUtil.addGardencore();
    MagicalcropsUtil.addMagicalcrops();
    ActuallyadditionsUtil.addActuallyadditions();

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

  public static void addPickable(@Nonnull String mod, @Nonnull String blockName, @Nonnull String itemName) {
    Block cropBlock = findBlock(mod, blockName);
    Item seedItem = findItem(mod, itemName);
    if (cropBlock != null && seedItem != null) {
      FarmersCommune.joinCommune(new PickableFarmer(cropBlock, new ItemStack(seedItem)));
    }
  }

  public static CustomSeedFarmer addSeed(@Nonnull String mod, @Nonnull String blockName, @Nonnull String itemName, Block... extraFarmland) {
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

  public static Block findBlock(@Nonnull String mod, @Nonnull String blockName) {
    final ResourceLocation name = new ResourceLocation(mod, blockName);
    if (Block.REGISTRY.containsKey(name)) {
      return Block.REGISTRY.getObject(name);
    }
    return null;
  }

  public static Item findItem(@Nonnull String mod, @Nonnull String itemName) {
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
