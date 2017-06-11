package crazypants.enderio.init;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import com.enderio.core.common.BlockEnder;

import crazypants.enderio.BlockEio;
import crazypants.enderio.Log;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class ModObjectRegistry {

  private ModObjectRegistry() {
  }

  public static void init(@Nonnull FMLInitializationEvent event) {
    for (ModObject elem : ModObject.values()) {
      elem.initElem(event);
    }
  }

  public static void init(@Nonnull FMLPreInitializationEvent event) {
    for (ModObject elem : ModObject.values()) {
      elem.preInitElem(event);
    }
    ModObjectRegistry.registerTeClasses();
  }

  static void registerTeClasses() {
    Map<Class<? extends TileEntity>, List<String>> clazzes = new HashMap<Class<? extends TileEntity>, List<String>>();
  
    for (ModObject elem : ModObject.values()) {
      Class<? extends TileEntity> teClazz = elem.teClazz;
      if (teClazz == null) {
        if (elem.getBlock() instanceof BlockEnder) {
          teClazz = ((BlockEnder<?>) elem.getBlockNN()).getTeClass();
        }
      }
      if (teClazz != null) {
        if (!clazzes.containsKey(teClazz)) {
          clazzes.put(teClazz, new ArrayList<String>());
        }
        clazzes.get(teClazz).add(elem.unlocalisedName + "_tileentity");
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

  static void preInit(@Nonnull IModObject.Registerable mo, @Nonnull FMLPreInitializationEvent event) {
    final Class<?> clazz2 = mo.getClazz(); // because final fields may unexpectedly become null, according to our compiler warnings
    if (clazz2 == null) {
      Log.debug(mo + ".preInitElem() missing");
      return;
    }
    Object obj = null;
    try {
      obj = clazz2.getDeclaredMethod(mo.getMethodName(), new Class<?>[] { IModObject.class }).invoke(null, new Object[] { mo });
    } catch (Throwable e) {
      String str = "ModObject:create: Could not create instance for " + mo.getClazz() + " using method " + mo.getMethodName();
      Log.error(str + " Exception: " + e);
      throw new RuntimeException(str, e);
    }
    if (obj instanceof Item) {
      mo.setItem((Item) obj);
    } else if (obj instanceof Block) {
      mo.setBlock((Block) obj);
      final Item itemFromBlock = Item.getItemFromBlock((Block) obj);
      if (itemFromBlock != Items.AIR) {
        mo.setItem(itemFromBlock);
      }
      if (obj instanceof BlockEio<?>) {
        ((BlockEio<?>) obj).preInit(event);
      }
    }
  }

  static void initElem(@Nonnull IModObject.Registerable mo, @Nonnull FMLInitializationEvent event) {
    final Block block = mo.getBlock();
    if (block instanceof BlockEio<?>) {
      ((BlockEio<?>) block).init(event);
    }
  }

  static @Nonnull String sanitizeName(@Nonnull String name) {
    return name.replaceAll("([A-Z])", "_$0").toLowerCase(Locale.ENGLISH);
  }

}
