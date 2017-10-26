package crazypants.enderio.init;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.BlockEnder;
import com.enderio.core.common.util.NNList;
import com.google.common.base.Strings;

import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.Log;
import crazypants.enderio.init.IModObject.Registerable;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@EventBusSubscriber(modid = EnderIO.MODID)
public class ModObjectRegistry {

  private static final NNList<IModObject.Registerable> objects = new NNList<IModObject.Registerable>();

  public static <T extends Enum<T> & IModObject.Registerable> void addModObjects(Class<T> enumClass) {
    objects.addAll(Arrays.asList(enumClass.getEnumConstants()));
  }

  public static void init(@Nonnull FMLPreInitializationEvent event) {
    Log.info("ModObjectRegistry:FMLPreInitializationEvent");
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void registerBlocksEarly(@Nonnull RegistryEvent.Register<Block> event) {
    addModObjects(ModObject.class);
  }

  @SubscribeEvent(priority = EventPriority.NORMAL)
  public static void registerBlocks(@Nonnull RegistryEvent.Register<Block> event) {
    Log.info("ModObjectRegistry:RegistryEvent.Register<Block>");
    for (IModObject elem : objects) {
      if (elem instanceof IModObject.Registerable) {
        IModObject.Registerable mo = (IModObject.Registerable) elem;
        final String blockMethodName = mo.getBlockMethodName();
        if (blockMethodName != null) {
          createBlock(mo, blockMethodName, event);
        }
      }
    }
  }

  @SubscribeEvent(priority = EventPriority.LOW)
  public static void registerTileEntities(@Nonnull RegistryEvent.Register<Block> event) {
    registerTeClasses(); // Note: Lex says this goes into the block register event
  }

  @SubscribeEvent
  public static void registerItems(@Nonnull RegistryEvent.Register<Item> event) {
    Log.info("ModObjectRegistry:RegistryEvent.Register<Item>");
    for (IModObject elem : objects) {
      if (elem instanceof IModObject.Registerable) {
        IModObject.Registerable mo = (IModObject.Registerable) elem;

        final String itemMethodName = mo.getItemMethodName();
        if (itemMethodName != null) {
          createItem(mo, itemMethodName, event);
        } else {
          createBlockItem(mo, event);
        }
      }
    }
  }

  public static void init(@Nonnull FMLInitializationEvent event) {
    for (IModObject mo : objects) {
      final Block block = mo.getBlock();
      if (block instanceof IModObject.LifecycleInit) {
        ((IModObject.LifecycleInit) block).init(mo, event);
      }
      Item item = mo.getItem();
      if (item instanceof IModObject.LifecycleInit) {
        ((IModObject.LifecycleInit) item).init(mo, event);
      }

      // TODO 1.11: The following code should go once we're done porting.
      if (block != null) {
        Log.debug("Block " + block.getRegistryName() + " has localized name " + block.getLocalizedName());
      }
      if (item != null) {
        NonNullList<ItemStack> list = new NNList<>();
        EnderIO.proxy.getSubItems(item, EnderIOTab.tabNoTab, list);
        if (list.isEmpty()) {
          Log.debug("Item " + item.getRegistryName() + " has localized name " + new ItemStack(item).getDisplayName());
        } else {
          for (ItemStack itemStack : list) {
            Log.debug("Item " + item.getRegistryName() + ":" + itemStack.getItemDamage() + " has localized name " + itemStack.getDisplayName());
          }
        }
      }
    }
  }

  public static void init(@Nonnull FMLPostInitializationEvent event) {
    for (IModObject mo : objects) {
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
    Map<Class<? extends TileEntity>, List<String>> clazzes = new HashMap<Class<? extends TileEntity>, List<String>>();

    for (IModObject elem : objects) {
      Class<? extends TileEntity> teClazz = (elem instanceof IModObject.Registerable) ? ((IModObject.Registerable) elem).getTileClass() : null;
      if (teClazz == null) {
        final Block block = elem.getBlock();
        if (block instanceof BlockEnder) {
          teClazz = ((BlockEnder<?>) block).getTeClass();
        }
      }
      if (teClazz != null) {
        if (!clazzes.containsKey(teClazz)) {
          clazzes.put(teClazz, new ArrayList<String>());
        }
        clazzes.get(teClazz).add(elem.getUnlocalisedName() + "_tileentity");
      }
    }

    for (Entry<Class<? extends TileEntity>, List<String>> entry : clazzes.entrySet()) {
      if (entry.getValue().size() == 1) {
        GameRegistry.registerTileEntity(entry.getKey(), entry.getValue().get(0));
      } else {
        Collections.sort(entry.getValue());
        String[] params = new String[entry.getValue().size() - 1];
        for (int i = 0; i < params.length; i++) {
          params[i] = entry.getValue().get(i + 1);
        }
        Log.debug("Registering TileEntity " + entry.getKey() + " as " + entry.getValue().get(0) + " with aliases " + Arrays.asList(params));
        GameRegistry.registerTileEntityWithAlternatives(entry.getKey(), entry.getValue().get(0), params);
      }
    }
  }
  
  private static RuntimeException throwCreationError(@Nonnull IModObject.Registerable mo, @Nonnull String blockMethodName, @Nullable Object ex) {
    String str = "ModObject:create: Could not create instance for " + mo.getClazz() + " using method " + blockMethodName;
    Log.error(str + (ex instanceof Exception ? " Exception: " : " Object: ") + Objects.toString(ex));
    if (ex instanceof Exception) {
      throw new RuntimeException(str, (Exception) ex);
    } else {
      throw new RuntimeException(str);
    }
  }
  
  private static Object createObject(@Nonnull IModObject.Registerable mo, @Nonnull String methodName) {
    Object obj;
    try {
      obj = mo.getClazz().getDeclaredMethod(methodName, new Class<?>[] { IModObject.class }).invoke(null, new Object[] { mo });
    } catch (NoSuchMethodException e) {
      try {
        obj = mo.getClazz().getDeclaredMethod(methodName).invoke(null);
      } catch (Exception e2) {
        throw throwCreationError(mo, methodName, e2);
      }
    } catch (Exception e) {
      throw throwCreationError(mo, methodName, e);
    }
    return obj;
  }

  private static void createBlock(@Nonnull IModObject.Registerable mo, @Nonnull String blockMethodName, @Nonnull Register<Block> event) {
    Object obj = createObject(mo, blockMethodName);

    if (obj instanceof Block) {
      mo.setBlock((Block) obj);
      event.getRegistry().register((Block) obj);
    } else {
      throwCreationError(mo, blockMethodName, obj);
    }
  }

  private static void createItem(@Nonnull IModObject.Registerable mo, @Nonnull String itemMethodName, @Nonnull Register<Item> event) {
    Object obj = createObject(mo, itemMethodName);

    if (obj instanceof Item) {
      mo.setItem((Item) obj);
      event.getRegistry().register((Item) obj);
    } else {
      throwCreationError(mo, itemMethodName, obj);
    }
  }

  private static void createBlockItem(Registerable mo, @Nonnull Register<Item> event) {
    Block block = mo.getBlock();
    if (block instanceof IModObject.WithBlockItem) {
      final Item item = ((IModObject.WithBlockItem) block).createBlockItem(mo);
      if (item != null) {
        mo.setItem(item);
        event.getRegistry().register(item);
      }
    } else if (block == null) {
      Log.warn("ModObject:create: " + mo + " is does neither have a block nor an item");
    } else { // TODO: Remove this branch once all blocks have been changed
      final Item itemFromBlock = Item.getItemFromBlock(block);
      if (itemFromBlock != Items.AIR) {
        Log.error("ModObject:create: " + mo + " is still creating its blockItem in the block phase");
        mo.setItem(itemFromBlock);
      }
    }
  }

  public static @Nonnull String sanitizeName(@Nonnull String name) {
    return name.replaceAll("([A-Z])", "_$0").toLowerCase(Locale.ENGLISH);
  }
}
