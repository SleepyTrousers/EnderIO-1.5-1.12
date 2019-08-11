package crazypants.enderio.base.integration.bigreactors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.config.IntegrationConfig;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

@EventBusSubscriber(modid = EnderIO.MODID)
public class BRProxy {

  private static boolean isLoaded = false;

  private static Class<Object> ReactorInterior;

  private static Class<Object> TurbineCoil;

  private static Method registerBlock;

  private static Method registerFluid;

  private static Method registerTurbineBlock;

  private static Method getBlockData;

  private static Method getFluidData;

  private static Method getTurbineBlockData;

  @SuppressWarnings("null")
  @SubscribeEvent
  public static void init(@Nonnull EnderIOLifecycleEvent.Init.Normal event) {
    if (Loader.isModLoaded("bigreactors") && IntegrationConfig.enableBigReactors.get()) {
      try {
        ReactorInterior = ReflectionHelper.getClass(BRProxy.class.getClassLoader(), "erogenousbeef.bigreactors.api.registry.ReactorInterior");

        // public static ReactorInteriorData getBlockData(String oreDictName) {

        getBlockData = ReflectionHelper.findMethod(ReactorInterior, "getBlockData", null, String.class);

        // public static void registerBlock(String oreDictName, float absorption, float heatEfficiency, float moderation, float heatConductivity)

        registerBlock = ReflectionHelper.findMethod(ReactorInterior, "registerBlock", null, String.class, float.class, float.class, float.class, float.class);

        // public static ReactorInteriorData getFluidData(String fluidName) {

        getFluidData = ReflectionHelper.findMethod(ReactorInterior, "getFluidData", null, String.class);

        // public static void registerFluid(String fluidName, float absorption, float heatEfficiency, float moderation, float heatConductivity)

        registerFluid = ReflectionHelper.findMethod(ReactorInterior, "registerFluid", null, String.class, float.class, float.class, float.class, float.class);

        TurbineCoil = ReflectionHelper.getClass(BRProxy.class.getClassLoader(), "erogenousbeef.bigreactors.api.registry.TurbineCoil");

        // public static CoilPartData getBlockData(String oreDictName)

        getTurbineBlockData = ReflectionHelper.findMethod(TurbineCoil, "getBlockData", null, String.class);

        // public static void registerBlock(String oreDictName, float efficiency, float bonus, float extractionRate)

        registerTurbineBlock = ReflectionHelper.findMethod(TurbineCoil, "registerBlock", null, String.class, float.class, float.class, float.class);

        isLoaded = true;

        BRRegistrations.init(event.getEvent());

      } catch (RuntimeException e) {
        Log.error("Failed to load Extreme Reactors integration. Reason:");
        e.printStackTrace();
      }
    }
  }

  public static boolean isBlockRegistered(String oreDictName) {
    if (isLoaded) {
      try {
        return getBlockData.invoke(null, oreDictName) != null;
      } catch (IllegalAccessException e) {
        Log.error("Failed to access Extreme Reactors integration. Reason:");
        e.printStackTrace();
      } catch (IllegalArgumentException e) {
        Log.error("Failed to access Extreme Reactors integration. Reason:");
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        Log.error("Failed to access Extreme Reactors integration. Reason:");
        e.printStackTrace();
      }
    }
    return true;
  }

  public static void registerBlock(String oreDictName, float absorption, float heatEfficiency, float moderation, float heatConductivity) {
    if (isLoaded && !isBlockRegistered(oreDictName)) {
      try {
        registerBlock.invoke(null, oreDictName, absorption, heatEfficiency, moderation, heatConductivity);
      } catch (IllegalAccessException e) {
        Log.error("Failed to access Extreme Reactors integration. Reason:");
        e.printStackTrace();
      } catch (IllegalArgumentException e) {
        Log.error("Failed to access Extreme Reactors integration. Reason:");
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        Log.error("Failed to access Extreme Reactors integration. Reason:");
        e.printStackTrace();
      }
    }
  }

  public static boolean isFluidRegistered(String fluidName) {
    if (isLoaded) {
      try {
        return getFluidData.invoke(null, fluidName) != null;
      } catch (IllegalAccessException e) {
        Log.error("Failed to access Extreme Reactors integration. Reason:");
        e.printStackTrace();
      } catch (IllegalArgumentException e) {
        Log.error("Failed to access Extreme Reactors integration. Reason:");
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        Log.error("Failed to access Extreme Reactors integration. Reason:");
        e.printStackTrace();
      }
    }
    return true;
  }

  public static void registerFluid(String fluidName, float absorption, float heatEfficiency, float moderation, float heatConductivity) {
    if (isLoaded && !isFluidRegistered(fluidName)) {
      try {
        registerFluid.invoke(null, fluidName, absorption, heatEfficiency, moderation, heatConductivity);
      } catch (IllegalAccessException e) {
        Log.error("Failed to access Extreme Reactors integration. Reason:");
        e.printStackTrace();
      } catch (IllegalArgumentException e) {
        Log.error("Failed to access Extreme Reactors integration. Reason:");
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        Log.error("Failed to access Extreme Reactors integration. Reason:");
        e.printStackTrace();
      }
    }
  }

  public static boolean isTurbineBlockRegistered(String oreDictName) {
    if (isLoaded) {
      try {
        return getTurbineBlockData.invoke(null, oreDictName) != null;
      } catch (IllegalAccessException e) {
        Log.error("Failed to access Extreme Reactors integration. Reason:");
        e.printStackTrace();
      } catch (IllegalArgumentException e) {
        Log.error("Failed to access Extreme Reactors integration. Reason:");
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        Log.error("Failed to access Extreme Reactors integration. Reason:");
        e.printStackTrace();
      }
    }
    return true;
  }

  public static void registerTurbineBlock(String oreDictName, float efficiency, float bonus, float extractionRate) {
    if (isLoaded && !isTurbineBlockRegistered(oreDictName)) {
      try {
        registerTurbineBlock.invoke(null, oreDictName, efficiency, bonus, extractionRate);
      } catch (IllegalAccessException e) {
        Log.error("Failed to access Extreme Reactors integration. Reason:");
        e.printStackTrace();
      } catch (IllegalArgumentException e) {
        Log.error("Failed to access Extreme Reactors integration. Reason:");
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        Log.error("Failed to access Extreme Reactors integration. Reason:");
        e.printStackTrace();
      }
    }
  }

}
