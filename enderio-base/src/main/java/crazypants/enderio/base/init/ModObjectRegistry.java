package crazypants.enderio.base.init;

import java.util.Arrays;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.api.IModTileEntity;
import crazypants.enderio.api.addon.IEnderIOAddon;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import crazypants.enderio.util.O2OMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

@EventBusSubscriber(modid = EnderIO.MODID)
public class ModObjectRegistry {

  private static final @Nonnull ResourceLocation NAME = new ResourceLocation(EnderIO.DOMAIN, "modobject");
  private static IForgeRegistry<IModObject> REGISTRY = null;

  @SubscribeEvent
  public static void registerRegistry(@Nonnull RegistryEvent.NewRegistry event) {
    REGISTRY = new RegistryBuilder<IModObject>().setName(NAME).setType(IModObject.class).setIDRange(0, 0x00FFFFFF).create();
  }

  @SubscribeEvent
  public static void registerRegistry(@Nonnull EnderIOLifecycleEvent.PreInit event) {
    MinecraftForge.EVENT_BUS.post(new RegisterModObject(NAME, NullHelper.notnullF(REGISTRY, "RegistryBuilder.create()")));
  }

  // ---

  private static final @Nonnull NNList<IModTileEntity> tileEntities = new NNList<>();

  private static final @Nonnull O2OMap<ResourceLocation, IModObject> blockMap = new O2OMap<>();
  private static final @Nonnull O2OMap<ResourceLocation, IModObject> itemMap = new O2OMap<>();

  public static <T extends Enum<T> & IModTileEntity> void addModTileEntities(Class<T> enumClass) {
    tileEntities.addAll(Arrays.asList(enumClass.getEnumConstants()));
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void registerBlocksEarly(@Nonnull RegisterModObject event) {
    event.register(ModObject.class);
  }

  @SubscribeEvent(priority = EventPriority.NORMAL)
  public static void registerBlocks(@Nonnull RegistryEvent.Register<Block> event) {
    for (IModObject mo : REGISTRY) {
      Block block = mo.getBlockCreator().apply(mo);
      if (block != null) {
        event.getRegistry().register(block);
        blockMap.putNoOverride(block.getRegistryName(), mo);
        checkUseNeighborBrightness(mo, block);
      }
    }
  }

  private static void checkUseNeighborBrightness(IModObject mo, Block block) {
    if (Log.isInDev() && !block.getUseNeighborBrightness(block.getDefaultState())) {
      boolean flag1 = block instanceof BlockStairs;
      boolean flag2 = block instanceof BlockSlab;
      boolean flag4 = !block.getMaterial(block.getDefaultState()).blocksLight();
      boolean flag5 = block.getLightOpacity(block.getDefaultState()) == 0;
      if (flag1 || flag2 || flag4 || flag5) {
        Log.error("Block " + mo.getRegistryName() + " doesn't set useNeighborBrightness but it seems it should because: " + (flag1 ? "stairs " : "")
            + (flag2 ? "slab " : "") + (flag4 ? "translucent " : "") + (flag5 ? "lightOpacity " : ""));
      }
    }
  }

  @SubscribeEvent(priority = EventPriority.LOW)
  public static void registerAddonBlocks(@Nonnull RegistryEvent.Register<Block> event) {
    for (ModContainer modContainer : Loader.instance().getModList()) {
      Object mod = modContainer.getMod();
      if (mod instanceof IEnderIOAddon) {
        ((IEnderIOAddon) mod).injectBlocks(NullHelper.notnullF(event.getRegistry(), "RegistryEvent.Register<Block>.getRegistry()"));
      }
    }
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public static void registerTileEntities(@Nonnull RegistryEvent.Register<Block> event) {
    registerTeClasses(); // Note: Lex says this goes into the block register event
  }

  @SubscribeEvent
  public static void registerItems(@Nonnull RegistryEvent.Register<Item> event) {
    for (IModObject mo : REGISTRY) {
      Item item = mo.getItemCreator().apply(mo, mo.getBlock());
      if (item != null) {
        event.getRegistry().register(item);
        itemMap.putNoOverride(item.getRegistryName(), mo);
      }
    }
  }

  @SuppressWarnings("null")
  public static void dumpItems() {
    for (IModObject mo : REGISTRY) {
      Item item = mo.getItem();
      if (item != null) {
        for (CreativeTabs tab : item.getCreativeTabs()) {
          NNList<ItemStack> list = new NNList<>();
          item.getSubItems(tab, list);
          for (ItemStack itemStack : list) {
            final int[] oreIDs = OreDictionary.getOreIDs(itemStack);
            if (oreIDs.length == 0) {
              System.out.println("  <item name=\"" + itemStack.getItem().getRegistryName() + ":" + itemStack.getItemDamage() + "\" show=\"false\" />");
            } else {
              System.out.println("  <item name=\"" + itemStack.getItem().getRegistryName() + ":" + itemStack.getItemDamage() + "\" show=\"false\" /><!-- "
                  + OreDictionary.getOreName(oreIDs[0]) + " -->");
            }
            for (int i : oreIDs) {
              System.out.println("    <item name=\"" + OreDictionary.getOreName(i) + "\" show=\"false\" />");
            }
          }
        }
      }
    }
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public static void registerOredict(@Nonnull RegistryEvent.Register<Item> event) {
    // oredict registration goes here
  }

  @SubscribeEvent(priority = EventPriority.HIGH)
  public static void init(@Nonnull EnderIOLifecycleEvent.Init.Pre event) {
    for (IModObject mo : REGISTRY) {
      final Block block = mo.getBlock();
      if (block instanceof IModObject.LifecycleInit) {
        ((IModObject.LifecycleInit) block).init(mo, event.getEvent());
      }
      Item item = mo.getItem();
      if (item instanceof IModObject.LifecycleInit) {
        ((IModObject.LifecycleInit) item).init(mo, event.getEvent());
      }
    }
  }

  public static void init(@Nonnull FMLPostInitializationEvent event) {
    for (IModObject mo : REGISTRY) {
      final Block block = mo.getBlock();
      if (block instanceof IModObject.LifecyclePostInit) {
        ((IModObject.LifecyclePostInit) block).init(mo, event);
      }
      Item item = mo.getItem();
      if (item instanceof IModObject.LifecyclePostInit) {
        ((IModObject.LifecyclePostInit) item).init(mo, event);
      }
    }
  }

  private static void registerTeClasses() {
    for (IModTileEntity te : tileEntities) {
      Log.debug("Registering TileEntity " + te.getUnlocalisedName() + " as " + te.getRegistryName().toString());
      GameRegistry.registerTileEntity(te.getTileEntityClass(), te.getRegistryName());
    }
  }

  public static @Nonnull String sanitizeName(@Nonnull String name) {
    return name.replaceAll("([A-Z])", "_$0").replaceFirst("^_", "").toLowerCase(Locale.ENGLISH);
  }

  public static @Nullable IModObject getModObject(@Nonnull Block forBlock) {
    return blockMap.getValue(forBlock.getRegistryName());
  }

  public static @Nonnull IModObject getModObjectNN(@Nonnull Block forBlock) {
    return NullHelper.notnull(getModObject(forBlock), "missing modObject for block " + forBlock.getRegistryName());
  }

  public static @Nullable Block getBlock(@Nonnull IModObject forModObject) {
    final ResourceLocation key = blockMap.getKey(forModObject);
    return key != null && Block.REGISTRY.containsKey(key) ? Block.REGISTRY.getObject(key) : null;
  }

  public static @Nonnull Block getBlockNN(@Nonnull IModObject forModObject) {
    return NullHelper.notnull(getBlock(forModObject), "missing block for modObject " + forModObject.getRegistryName());
  }

  public static @Nullable IModObject getModObject(@Nonnull Item forItem) {
    return itemMap.getValue(forItem.getRegistryName());
  }

  public static @Nonnull IModObject getModObjectNN(@Nonnull Item forItem) {
    return NullHelper.notnull(getModObject(forItem), "missing modObject for item " + forItem.getRegistryName());
  }

  public static @Nullable Item getItem(@Nonnull IModObject forModObject) {
    final ResourceLocation key = itemMap.getKey(forModObject);
    return key != null && Item.REGISTRY.containsKey(key) ? Item.REGISTRY.getObject(key) : null;
  }

  public static @Nonnull Item getItemNN(@Nonnull IModObject forModObject) {
    return NullHelper.notnull(getItem(forModObject), "missing item for modObject " + forModObject.getRegistryName());
  }

  public static @Nonnull ForgeRegistry<IModObject> getRegistry() {
    return (ForgeRegistry<IModObject>) NullHelper.notnull(REGISTRY, "accessing modobject registry too early");
  }

}
