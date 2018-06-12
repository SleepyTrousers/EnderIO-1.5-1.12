package crazypants.enderio.base.init;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.addon.IEnderIOAddon;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.init.IModObject.Registerable;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@EventBusSubscriber(modid = EnderIO.MODID)
public class ModObjectRegistry {

  private static final NNList<IModObject.Registerable> objects = new NNList<IModObject.Registerable>();
  private static final NNList<IModObject.Registerable> wrappedObjects = NNList.wrap(Collections.unmodifiableList(objects));
  private static final Map<Object, IModObject.Registerable> reverseMapping = new IdentityHashMap<>();

  public static <T extends Enum<T> & IModObject.Registerable> void addModObjects(Class<T> enumClass) {
    objects.addAll(Arrays.asList(enumClass.getEnumConstants()));
  }

  private static final NNList<IModTileEntity> tileEntities = new NNList<>();

  public static <T extends Enum<T> & IModTileEntity> void addModTileEntities(Class<T> enumClass) {
    tileEntities.addAll(Arrays.asList(enumClass.getEnumConstants()));
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void registerBlocksEarly(@Nonnull RegistryEvent.Register<Block> event) {
    addModObjects(ModObject.class);
  }

  @SubscribeEvent(priority = EventPriority.NORMAL)
  public static void registerBlocks(@Nonnull RegistryEvent.Register<Block> event) {
    for (IModObject elem : objects) {
      if (elem instanceof IModObject.Registerable) {
        IModObject.Registerable mo = (IModObject.Registerable) elem;
        final String blockMethodName = mo.getBlockMethodName();
        if (blockMethodName != null) {
          createBlock(mo, blockMethodName, event);
        }
      }
    }
    throw new RuntimeException();
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

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public static void registerOredict(@Nonnull RegistryEvent.Register<Item> event) {
    // oredict registration gos here
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
    for (IModTileEntity te : tileEntities) {
      Log.debug("Registering TileEntity " + te.getUnlocalisedName() + " as " + te.getRegistryName().toString());
      GameRegistry.registerTileEntity(te.getTileEntityClass(), te.getRegistryName().toString());
    }
  }

  private static RuntimeException throwCreationError(@Nonnull IModObject.Registerable mo, @Nonnull String blockMethodName, @Nullable Object ex) {
    String str = "ModObject:create: Could not create instance for " + mo.getClazz() + " using method " + blockMethodName;
    Log.error(str + (ex instanceof Throwable ? " Exception: " : " Object: ") + Objects.toString(ex));
    if (ex instanceof Throwable) {
      disectClass(mo);
      throw new RuntimeException(str, (Throwable) ex);
    } else {
      throw new RuntimeException(str);
    }
  }

  private static Object createObject(@Nonnull IModObject.Registerable mo, @Nonnull String methodName) {
    Object obj;
    try {
      obj = mo.getClazz().getDeclaredMethod(methodName, new Class<?>[] { IModObject.class }).invoke(null, new Object[] { mo });
    } catch (Exception | Error e) {
      throw throwCreationError(mo, methodName, e);
    }
    return obj;
  }

  private static void disectClass(IModObject.Registerable mo) {
    try {
      Log.debug("Modobject class is " + mo.getClazz());
      Method[] declaredMethods = mo.getClazz().getDeclaredMethods();
      for (Method method : declaredMethods) {
        Log.debug("  with method " + method);
        Annotation[] declaredAnnotations = method.getDeclaredAnnotations();
        for (Annotation annotation : declaredAnnotations) {
          Log.debug("    with annotation " + annotation);
        }
      }
    } catch (Exception | Error e) {
    }
  }

  private static void createBlock(@Nonnull IModObject.Registerable mo, @Nonnull String blockMethodName, @Nonnull Register<Block> event) {
    Object obj = createObject(mo, blockMethodName);

    if (obj instanceof Block) {
      mo.setBlock((Block) obj);
      event.getRegistry().register((Block) obj);
      reverseMapping.put(obj, mo);
    } else {
      throwCreationError(mo, blockMethodName, obj);
    }
  }

  private static void createItem(@Nonnull IModObject.Registerable mo, @Nonnull String itemMethodName, @Nonnull Register<Item> event) {
    Object obj = createObject(mo, itemMethodName);

    if (obj instanceof Item) {
      mo.setItem((Item) obj);
      event.getRegistry().register((Item) obj);
      reverseMapping.put(obj, mo);
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
        reverseMapping.put(item, mo);
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
    return name.replaceAll("([A-Z])", "_$0").replaceFirst("^_", "").toLowerCase(Locale.ENGLISH);
  }

  public static NNList<IModObject.Registerable> getObjects() {
    return wrappedObjects;
  }

  public static @Nullable IModObject.Registerable getModObject(@Nonnull Block forBlock) {
    return reverseMapping.get(forBlock);
  }

  public static @Nonnull IModObject.Registerable getModObjectNN(@Nonnull Block forBlock) {
    return NullHelper.notnull(reverseMapping.get(forBlock), "missing modObject");
  }

  public static @Nullable IModObject.Registerable getModObject(@Nonnull Item forItem) {
    return reverseMapping.get(forItem);
  }

  public static @Nonnull IModObject.Registerable getModObjectNN(@Nonnull Item forItem) {
    return NullHelper.notnull(reverseMapping.get(forItem), "missing modObject");
  }

}
