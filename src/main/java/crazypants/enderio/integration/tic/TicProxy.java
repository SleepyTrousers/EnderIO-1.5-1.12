package crazypants.enderio.integration.tic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import crazypants.enderio.EnderIO;
import crazypants.enderio.Log;
import crazypants.enderio.fluid.BlockFluidEio;
import crazypants.enderio.material.Alloy;
import crazypants.util.Prep;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class TicProxy {

  private static final ResourceLocation TEX_STILL = new ResourceLocation("tconstruct:blocks/fluids/molten_metal_flow");
  private static final ResourceLocation TEX_FLOWING = new ResourceLocation("tconstruct:blocks/fluids/molten_metal");
  private static boolean isLoaded = false;
  private static Class<Object> TinkerRegistry;
  private static Method getMelting;
  private static Method registerAlloy;
  private static Method registerTableCasting;
  private static Method registerMelting;
  private static Class<Object> MeltingRecipe;
  private static Method getResult;

  public static void init(FMLPreInitializationEvent event) {
    if (Loader.isModLoaded("tconstruct")) {
      try {
        TinkerRegistry = ReflectionHelper.getClass(TicProxy.class.getClassLoader(), "slimeknights.tconstruct.library.TinkerRegistry");
        getMelting = ReflectionHelper.findMethod(TinkerRegistry, null, new String[] { "getMelting" }, ItemStack.class); // MeltingRecipe
        registerAlloy = ReflectionHelper.findMethod(TinkerRegistry, null, new String[] { "registerAlloy" }, FluidStack.class, FluidStack[].class); // void
        registerTableCasting = ReflectionHelper.findMethod(TinkerRegistry, null, new String[] { "registerTableCasting" }, ItemStack.class, ItemStack.class,
            Fluid.class, int.class); // void
        registerMelting = ReflectionHelper.findMethod(TinkerRegistry, null, new String[] { "registerMelting" }, ItemStack.class, Fluid.class, int.class); // void

        MeltingRecipe = ReflectionHelper.getClass(TicProxy.class.getClassLoader(), "slimeknights.tconstruct.library.smeltery.MeltingRecipe");
        getResult = ReflectionHelper.findMethod(MeltingRecipe, null, new String[] { "getResult" }); // FluidStack

        isLoaded = true;
      } catch (RuntimeException e) {
        Log.error("Failed to load Tinker's Construct integration. Reason:");
        e.printStackTrace();
      }
    }
  }

  public static boolean isLoaded() {
    return isLoaded;
  }

  private static List<Pair<ItemStack, ItemStack[]>> alloyQueue;

  public static void registerAlloyRecipe(ItemStack result, ItemStack... input) {
    if (!isLoaded || Prep.isInvalid(result) || input.length < 2) {
      return;
    }
    for (ItemStack itemStack : input) {
      if (Prep.isInvalid(itemStack)) {
        return;
      }
    }

    if (alloyQueue == null) {
      alloyQueue = new ArrayList<Pair<ItemStack, ItemStack[]>>();
    }
    alloyQueue.add(Pair.of(result, input));

    FluidStack fluidResult = getFluidForItems(result);
    if (fluidResult == null) {
      return;
    }

    FluidStack[] fluids = new FluidStack[input.length];
    for (int i = 0; i < input.length; i++) {
      if ((fluids[i] = getFluidForItems(input[i])) == null) {
        return;
      }
    }

    Log.debug("Registered alloy recipe with TiC: " + result + " (" + fluidResult + ") from " + input + " (" + fluids + ")");
  }

  private static void registerAlloyRecipe(Pair<ItemStack, ItemStack[]> alloy)
      throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    FluidStack fluidResult = getFluidForItems(alloy.getLeft());
    if (fluidResult == null) {
      return;
    }

    FluidStack[] fluids = new FluidStack[alloy.getRight().length];
    for (int i = 0; i < alloy.getRight().length; i++) {
      if ((fluids[i] = getFluidForItems(alloy.getRight()[i])) == null) {
        return;
      }
    }

    gcd(fluidResult, fluids);
    registerAlloy.invoke(null, fluidResult, fluids);
    Log.debug("Tinkers.registerAlloy: " + fluidResult + ", " + fluids);
  }

  public static void init(FMLPostInitializationEvent event) {
    try {
      if (alloyQueue != null) {
        for (Pair<ItemStack, ItemStack[]> alloy : alloyQueue) {
          registerAlloyRecipe(alloy);
        }
        alloyQueue = null;
      }
      if (castQueue != null) {
        for (CastQueue cast : castQueue) {
          FluidStack fluid = getFluidForItems(cast.item);
          if (fluid == null) {
            Log.warn("Item used in cast recipe '" + cast.item + "' doesn't smelt into a fluid");
          } else {
            fluid.amount = (int) Math.ceil(fluid.amount * cast.amount);
            registerTableCasting.invoke(null, cast.result, cast.cast, fluid.getFluid(), fluid.amount);
            Log.debug("Tinkers.registerTableCasting: " + cast.result + ", " + cast.cast + ", " + fluid.getFluid().getName() + ", " + fluid.amount);
          }
        }
        castQueue = null;
      }
      if (smeltQueue != null) {
        for (SmeltQueue smelt : smeltQueue) {
          FluidStack fluid = getFluidForItems(smelt.output);
          if (fluid == null) {
            Log.warn("Item used in Smeltery recipe '" + smelt.output + "' doesn't smelt into a fluid");
          } else {
            registerMelting.invoke(null, smelt.input, fluid.getFluid(), (int) (fluid.amount * smelt.amount));
            Log.debug("Tinkers.registerMelting: " + smelt.input + ", " + fluid.getFluid().getName() + ", " + (int) (fluid.amount * smelt.amount));
          }
        }
        smeltQueue = null;
      }
    } catch (IllegalAccessException e) {
      Log.error("Failed to access Tinker's Construct integration. Reason:");
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      Log.error("Failed to access Tinker's Construct integration. Reason:");
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      Log.error("Failed to access Tinker's Construct integration. Reason:");
      e.printStackTrace();
    }
  }

  private static int gcd(int a, int b) {
    while (b > 0) {
      int temp = b;
      b = a % b;
      a = temp;
    }
    return a;
  }

  private static void gcd(FluidStack input, FluidStack... inputs) {
    int result = input.amount;
    for (FluidStack stack : inputs) {
      result = gcd(result, stack.amount);
    }
    if (result > 1) {
      input.amount /= result;
      for (FluidStack stack : inputs) {
        stack.amount /= result;
      }
    }
  }

  public static FluidStack getFluidForItems(ItemStack input) {
    if (!isLoaded) {
      return null;
    }
    try {
      ItemStack itemStack = input.copy();
      itemStack.stackSize = 1;
      Object melting = getMelting.invoke(null, itemStack);
      if (melting == null) {
        return null;
      }
      Object meltingResult = getResult.invoke(melting);
      if (meltingResult instanceof FluidStack) {
        FluidStack result = (FluidStack) meltingResult;
        result.amount *= input.stackSize;
        return result;
      }
    } catch (IllegalAccessException e) {
      Log.error("Failed to access Tinker's Construct integration. Reason:");
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      Log.error("Failed to access Tinker's Construct integration. Reason:");
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      Log.error("Failed to access Tinker's Construct integration. Reason:");
      e.printStackTrace();
    }
    return null;
  }

  // to be called and executed during preinit
  public static void registerMetal(final Alloy alloy) {
    if (!isLoaded) {
      return;
    }

    Fluid f = new Fluid(alloy.getBaseName().toLowerCase(Locale.ENGLISH), TEX_FLOWING, TEX_STILL) {
      @Override
      public int getColor() {
        return 0xFF000000 | alloy.getColor();
      }
    };
    f.setDensity(9000);
    f.setLuminosity(4);
    f.setTemperature(alloy.getMeltingPoint() + 273);
    f.setViscosity(3000);
    FluidRegistry.registerFluid(f);
    BlockFluidEio.createMetal(f, Material.LAVA, alloy.getColor());
    if (!EnderIO.proxy.isDedicatedServer()) {
      EnderIO.fluids.registerFluidBlockRendering(f, f.getName());
    }
    if (FluidRegistry.isUniversalBucketEnabled()) {
      FluidRegistry.addBucketForFluid(f);
    }

    NBTTagCompound tag = new NBTTagCompound();
    tag.setString("fluid", f.getName());
    tag.setString("ore", StringUtils.capitalize(alloy.getBaseName()));
    tag.setBoolean("toolforge", true);
    FMLInterModComms.sendMessage("tconstruct", "integrateSmeltery", tag);
  }

  private static class CastQueue {
    ItemStack result;
    ItemStack cast;
    ItemStack item;
    float amount;

    CastQueue(ItemStack result, ItemStack cast, ItemStack item, float amount) {
      super();
      this.result = result;
      this.cast = cast;
      this.item = item;
      this.amount = amount;
    }
  }

  private static List<CastQueue> castQueue;

  public static String registerTableCast(ItemStack result, ItemStack cast, ItemStack item, float amount, boolean simulate) {
    if (!isLoaded) {
      return null;
    }

    if (Prep.isInvalid(result)) {
      return "Result item not found";
    }

    if (Prep.isInvalid(cast)) {
      return "Cast item not found";
    }

    if (Prep.isInvalid(item)) {
      return "Fluid item not found";
    }

    if (!simulate) {
      item = item.copy();
      item.stackSize = 1;

      if (castQueue == null) {
        castQueue = new ArrayList<CastQueue>();
      }
      castQueue.add(new CastQueue(result, cast, item, amount));
    }

    return null;
  }

  private static class SmeltQueue {
    ItemStack input;
    ItemStack output;
    float amount;

    public SmeltQueue(ItemStack input, ItemStack output, float amount) {
      super();
      this.input = input;
      this.output = output;
      this.amount = amount;
    }
  }

  private static List<SmeltQueue> smeltQueue;

  public static void registerSmelterySmelting(ItemStack input, ItemStack output, float amount) {
    if (!isLoaded || Prep.isInvalid(input) || Prep.isInvalid(output)) {
      return;
    }
    if (smeltQueue == null) {
      smeltQueue = new ArrayList<SmeltQueue>();
    }
    smeltQueue.add(new SmeltQueue(input, output, amount));
  }

}
