package crazypants.enderio.base.farming;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.api.farm.IFarmerJoe;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.farming.farmers.ChorusFarmer;
import crazypants.enderio.base.farming.farmers.CocoaFarmer;
import crazypants.enderio.base.farming.farmers.CustomSeedFarmer;
import crazypants.enderio.base.farming.farmers.FlowerPicker;
import crazypants.enderio.base.farming.farmers.MelonFarmer;
import crazypants.enderio.base.farming.farmers.PickableFarmer;
import crazypants.enderio.base.farming.farmers.PlantableFarmer;
import crazypants.enderio.base.farming.farmers.StemFarmer;
import crazypants.enderio.base.farming.farmers.TreeFarmer;
import crazypants.enderio.base.init.ModObject;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

@EventBusSubscriber(modid = EnderIO.MODID)
public final class FarmersRegistry {

  public static final @Nonnull Things slotItemsHoeTools = new Things("toolHoe").add(Items.WOODEN_HOE).add(Items.STONE_HOE).add(Items.IRON_HOE)
      .add(Items.GOLDEN_HOE).add(Items.DIAMOND_HOE);
  public static final @Nonnull Things slotItemsAxeTools = new Things().add(Items.WOODEN_AXE).add(Items.STONE_AXE).add(Items.IRON_AXE).add(Items.GOLDEN_AXE)
      .add(Items.DIAMOND_AXE).add(ModObject.itemDarkSteelAxe);
  public static final @Nonnull Things slotItemsExtraTools = new Things("toolShears").add(Items.SHEARS).add(ModObject.itemDarkSteelShears).add("toolTreetap");
  public static final @Nonnull Things slotItemsSeeds = new Things("treeSapling").add(Items.WHEAT_SEEDS).add(Items.CARROT).add(Items.POTATO)
      .add(Blocks.RED_MUSHROOM).add(Blocks.BROWN_MUSHROOM).add(Items.NETHER_WART).add(Blocks.SAPLING).add(Items.REEDS).add(Items.MELON_SEEDS)
      .add(Items.PUMPKIN_SEEDS);
  public static final @Nonnull Things slotItemsProduce = new Things("logWood").add(new ItemStack(Blocks.LOG, 1, 0)).add(Blocks.WHEAT)
      .add(new ItemStack(Blocks.LEAVES, 1, 0)).add(Items.APPLE).add(Items.MELON).add(Blocks.PUMPKIN).add(Blocks.YELLOW_FLOWER).add(Blocks.RED_FLOWER);

  // TODO 1.11: move those treetaps somewhere else
  // slotItemsStacks3.addAll(TileFarmStation.TREETAPS.getItemStacks());

  private static final @Nonnull Things SAPLINGS = new Things("treeSapling");
  private static final @Nonnull Things WOODS = new Things("logWood");
  private static final @Nonnull Things FLOWERS = new Things().add(Blocks.YELLOW_FLOWER).add(Blocks.RED_FLOWER);

  @SubscribeEvent
  public static void registerFarmers(@Nonnull RegistryEvent.Register<IFarmerJoe> event) {
    event.getRegistry().register(new FlowerPicker(FLOWERS).setPriority(EventPriority.LOW).setRegistryName(EnderIO.DOMAIN, "flowers"));
    event.getRegistry()
        .register(new StemFarmer(Blocks.REEDS, new ItemStack(Items.REEDS)).setPriority(EventPriority.LOW).setRegistryName(EnderIO.DOMAIN, "reeds"));
    event.getRegistry()
        .register(new StemFarmer(Blocks.CACTUS, new ItemStack(Blocks.CACTUS)).setPriority(EventPriority.LOW).setRegistryName(EnderIO.DOMAIN, "cactus"));
    event.getRegistry().register(new TreeFarmer(SAPLINGS, WOODS).setPriority(EventPriority.LOW).setRegistryName(EnderIO.DOMAIN, "trees"));
    event.getRegistry().register(
        new TreeFarmer(true, Blocks.RED_MUSHROOM, Blocks.RED_MUSHROOM_BLOCK).setPriority(EventPriority.LOW).setRegistryName(EnderIO.DOMAIN, "red_mushrooms"));
    event.getRegistry().register(new TreeFarmer(true, Blocks.BROWN_MUSHROOM, Blocks.BROWN_MUSHROOM_BLOCK).setPriority(EventPriority.LOW)
        .setRegistryName(EnderIO.DOMAIN, "brown_mushrooms"));
    // special case of plantables to get spacing correct
    event.getRegistry().register(new MelonFarmer(Blocks.MELON_STEM, Blocks.MELON_BLOCK, new ItemStack(Items.MELON_SEEDS)).setPriority(EventPriority.LOW)
        .setRegistryName(EnderIO.DOMAIN, "melons"));
    event.getRegistry().register(new MelonFarmer(Blocks.PUMPKIN_STEM, Blocks.PUMPKIN, new ItemStack(Items.PUMPKIN_SEEDS)).setPriority(EventPriority.LOW)
        .setRegistryName(EnderIO.DOMAIN, "pumpkins"));
    // 'BlockNetherWart' is not an IGrowable
    event.getRegistry().register(new CustomSeedFarmer(Blocks.NETHER_WART, 3, new ItemStack(Items.NETHER_WART)).setRequiresTilling(false)
        .setPriority(EventPriority.LOW).setRegistryName(EnderIO.DOMAIN, "netherwart"));
    // Cocoa is odd
    event.getRegistry().register(new CocoaFarmer().setPriority(EventPriority.LOW).setRegistryName(EnderIO.DOMAIN, "cocoa"));
    // Chorus plant is even odder
    event.getRegistry().register(new ChorusFarmer().setPriority(EventPriority.LOW).setRegistryName(EnderIO.DOMAIN, "chorus"));

    // Handles all 'vanilla' style crops
    event.getRegistry().register(new PlantableFarmer().setPriority(EventPriority.LOWEST).setRegistryName(EnderIO.DOMAIN, "default"));
  }

  public static void addPickable(@Nonnull RegistryEvent.Register<IFarmerJoe> event, @Nonnull String mod, @Nonnull String blockName, @Nonnull String itemName) {
    Block cropBlock = findBlock(mod, blockName);
    Item seedItem = findItem(mod, itemName);
    if (cropBlock != null && seedItem != null) {
      event.getRegistry().register(new PickableFarmer(cropBlock, new ItemStack(seedItem)).setRegistryName(mod, blockName));
    }
  }

  public static CustomSeedFarmer addSeed(@Nonnull RegistryEvent.Register<IFarmerJoe> event, @Nonnull String mod, @Nonnull String blockName,
      @Nonnull String itemName, Block... extraFarmland) {
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
      event.getRegistry().register(farmer.setRegistryName(mod, blockName));
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

  public static boolean isLog(Block block) {
    return WOODS.contains(block);
  }

  public static boolean isLog(Item item) {
    return WOODS.contains(item);
  }

  public static boolean isLog(ItemStack stack) {
    return WOODS.contains(stack);
  }

  private FarmersRegistry() {
  }

  /**
   * Register the given items as hoes that can be used in the farming station. Ignores non-existing items.
   * 
   * @param modid
   *          domain part of the item RL
   * @param hoes
   *          any number of item names
   * @return The number of hoes found
   */
  public static int registerHoes(@Nonnull final String modid, final @Nonnull String... hoes) {
    int count = 0;
    for (String hoe : hoes) {
      Item item = findItem(modid, NullHelper.first(hoe, ""));
      if (item != null) {
        OreDictionary.registerOre("toolHoe", new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE));
        count++;
      }
    }
    return count;
  }

}
