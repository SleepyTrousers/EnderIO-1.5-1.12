package crazypants.enderio.integration.tic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

import com.enderio.core.common.fluid.BlockFluidEnder;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.EnderIO;
import crazypants.enderio.Log;
import crazypants.enderio.fluid.Fluids;
import crazypants.enderio.material.alloy.Alloy;
import crazypants.util.Prep;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
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

  static final ResourceLocation TEX_STILL = new ResourceLocation("tconstruct:blocks/fluids/molten_metal_flow");
  static final ResourceLocation TEX_FLOWING = new ResourceLocation("tconstruct:blocks/fluids/molten_metal");
  private static boolean isLoaded = false;
  private static Method getMelting;
  private static Method registerAlloy;
  private static Method registerTableCasting;
  private static Method registerMelting;
  private static Method registerBasinCasting;
  private static Method getResult;

  public static void init(FMLPreInitializationEvent event) {
    if (Loader.isModLoaded("tconstruct")) {
      try {
        Class<Object> TinkerRegistry = ReflectionHelper.getClass(TicProxy.class.getClassLoader(), "slimeknights.tconstruct.library.TinkerRegistry");
        getMelting = ReflectionHelper.findMethod(TinkerRegistry, "getMelting", (String) null, ItemStack.class); // MeltingRecipe
        registerAlloy = ReflectionHelper.findMethod(TinkerRegistry, "registerAlloy", (String) null, FluidStack.class, FluidStack[].class); // void
        registerTableCasting = ReflectionHelper.findMethod(TinkerRegistry, "registerTableCasting", (String) null, ItemStack.class, ItemStack.class,
            Fluid.class, int.class); // void
        registerMelting = ReflectionHelper.findMethod(TinkerRegistry, "registerMelting", (String) null, ItemStack.class, Fluid.class, int.class); // void
        registerBasinCasting = ReflectionHelper.findMethod(TinkerRegistry, "registerBasinCasting", (String) null, ItemStack.class, ItemStack.class,
            Fluid.class, int.class); // void

        Class<Object> MeltingRecipe = ReflectionHelper.getClass(TicProxy.class.getClassLoader(), "slimeknights.tconstruct.library.smeltery.MeltingRecipe");
        getResult = ReflectionHelper.findMethod(MeltingRecipe, "getResult", (String) null); // FluidStack

        isLoaded = true;

        AdditionalFluid.init(event);
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

  public static void registerAlloyRecipe(@Nonnull ItemStack result, ItemStack... input) {
    if (!isLoaded || Prep.isInvalid(result) || input.length < 2) {
      return;
    }
    for (ItemStack itemStack : input) {
      if (itemStack == null || Prep.isInvalid(itemStack)) {
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
      if ((fluids[i] = getFluidForItems(NullHelper.notnull(input[i], "missing input item stack in alloy recipe"))) == null) {
        return;
      }
    }

    Log.debug("Registered alloy recipe with TiC: " + result + " (" + fluidResult + ") from " + input + " (" + fluids + ")");
  }

  private static void registerAlloyRecipe(Pair<ItemStack, ItemStack[]> alloy)
      throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    final ItemStack result = NullHelper.notnull(alloy.getLeft(), "missing result item stack in alloy recipe");
    final ItemStack[] input = alloy.getRight();
    FluidStack fluidResult = getFluidForItems(result);
    if (fluidResult == null) {
      tryBasinAloying(result, input);
      return;
    }

    FluidStack[] fluids = new FluidStack[input.length];
    for (int i = 0; i < input.length; i++) {
      if ((fluids[i] = getFluidForItems(NullHelper.notnull(input[i], "missing input item stack in alloy recipe"))) == null) {
        return;
      }
    }

    gcd(fluidResult, fluids);
    registerAlloy.invoke(null, fluidResult, fluids);
    Log.debug("Tinkers.registerAlloy: " + fluidResult + ", " + fluids);
  }

  private static void tryBasinAloying(@Nonnull ItemStack result, ItemStack... inputs) {
    if (Prep.isInvalid(result) || result.getCount() != 1 || !(result.getItem() instanceof ItemBlock) || inputs.length != 2) {
      return;
    }
    final ItemStack input0 = inputs[0];
    final ItemStack input1 = inputs[1];
    if (input0 == null || Prep.isInvalid(input0) || input1 == null || Prep.isInvalid(input1)) {
      return;
    }
    FluidStack a = getFluidForItems(input0);
    FluidStack b = getFluidForItems(input1);
    if ((a == null) == (b == null)) {
      return;
    }
    if (a == null && !(input0.getCount() == 1 && input0.getItem() instanceof ItemBlock)) {
      return;
    }
    if (b == null && !(input1.getCount() == 1 && input1.getItem() instanceof ItemBlock)) {
      return;
    }

    if (a != null) {
      registerBasinCasting(result, input1, a.getFluid(), a.amount);
    } else if (b != null) {
      registerBasinCasting(result, input0, b.getFluid(), b.amount);
    }
  }

  public static void init(@Nonnull FMLPostInitializationEvent event) {
    if (!isLoaded) {
      return;
    }
    AdditionalFluid.init(event);
    try {
      if (smeltQueue != null) { // 1st because it may provide fluids for later
        for (SmeltQueue smelt : smeltQueue) {
          if (smelt.fluidOutput == null) {
            FluidStack fluid = getFluidForItems(smelt.output);
            if (fluid == null) {
              Log.warn("Item used in Smeltery recipe '" + smelt.output + "' doesn't smelt into a fluid");
            } else {
              smelt.fluidOutput = fluid.getFluid();
              smelt.amount *= fluid.amount;
            }
          }
          if (smelt.fluidOutput != null) {
            registerMelting.invoke(null, smelt.input, smelt.fluidOutput, (int) Math.max(1, Math.floor(smelt.amount)));
            Log.debug("Tinkers.registerMelting: " + smelt.input + ", " + smelt.fluidOutput.getName() + ", " + smelt.amount);
          }
        }
        smeltQueue = null;
      }
      if (alloyQueue != null) {
        for (Pair<ItemStack, ItemStack[]> alloy : alloyQueue) {
          registerAlloyRecipe(alloy);
        }
        alloyQueue = null;
      }
      if (castQueue != null) {
        for (CastQueue cast : castQueue) {
          if (cast.fluid == null) {
            FluidStack fluid = getFluidForItems(cast.item);
            cast.fluid = fluid.getFluid();
            cast.amount *= fluid.amount;
          }
          if (cast.fluid == null) {
            Log.warn("Item used in cast recipe '" + cast.item + "' doesn't smelt into a fluid");
          } else {
            registerTableCasting.invoke(null, cast.result, cast.cast, cast.fluid, (int) Math.ceil(cast.amount));
            Log.debug("Tinkers.registerTableCasting: " + cast.result + ", " + cast.cast + ", " + cast.fluid.getName() + ", " + cast.amount);
          }
        }
        castQueue = null;
      }
      if (basinQueue != null) {
        for (BasinQueue basin : basinQueue) {
          if (basin.fluid == null) {
            FluidStack fluid = getFluidForItems(basin.fluidItem);
            if (fluid != null) {
              basin.fluid = fluid.getFluid();
              basin.amount *= fluid.amount;
            }
          }
          if (basin.fluid != null) {
            registerBasinCasting.invoke(null, basin.output, basin.cast, basin.fluid, basin.amount);
            Log.debug("Tinkers.registerBasinCasting: " + basin.output + ", " + basin.cast + ", " + basin.fluid.getName() + ", " + basin.amount);
          }
        }
        basinQueue = null;
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

  public static FluidStack getFluidForItems(@Nonnull ItemStack input) {
    if (!isLoaded) {
      return null;
    }
    try {
      ItemStack itemStack = input.copy();
      itemStack.setCount(1);
      Object melting = getMelting.invoke(null, itemStack);
      if (melting == null) {
        // For some reason this recipe isn't yet available in postInit...
        if (itemStack.getItem() == Item.getItemFromBlock(Blocks.OBSIDIAN)) {
          Fluid fluid = FluidRegistry.getFluid("obsidian");
          if (fluid != null) {
            return new FluidStack(fluid, 288 * input.getCount());
          }
        }
        return null;
      }
      Object meltingResult = getResult.invoke(melting);
      if (meltingResult instanceof FluidStack) {
        FluidStack result = (FluidStack) meltingResult;
        result.amount *= input.getCount();
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
  public static void registerMetal(final @Nonnull Alloy alloy) {
    if (!isLoaded) {
      return;
    }

    Fluid f = new Fluid(alloy.getFluidName(), TEX_FLOWING, TEX_STILL) {
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
    BlockFluidEnder.MoltenMetal.create(f, Material.LAVA, alloy.getColor());
    if (!EnderIO.proxy.isDedicatedServer()) {
      Fluids.registerFluidBlockRendering(f);
    }
    FluidRegistry.addBucketForFluid(f);

    NBTTagCompound tag = new NBTTagCompound();
    tag.setString("fluid", f.getName());
    tag.setString("ore", alloy.getOreOre());
    tag.setBoolean("toolforge", true);
    FMLInterModComms.sendMessage("tconstruct", "integrateSmeltery", tag);
  }

  private static class CastQueue {
    @Nonnull
    ItemStack result;
    @Nonnull
    ItemStack cast;
    @Nonnull
    ItemStack item;
    Fluid fluid;
    float amount;

    CastQueue(@Nonnull ItemStack result, @Nonnull ItemStack cast, @Nonnull ItemStack item, float amount) {
      this.result = result;
      this.cast = cast;
      this.item = item;
      this.fluid = null;
      this.amount = amount;
    }

    CastQueue(@Nonnull ItemStack result, @Nonnull ItemStack cast, @Nonnull Fluid fluid, float amount) {
      this.result = result;
      this.cast = cast;
      this.item = Prep.getEmpty();
      this.fluid = fluid;
      this.amount = amount;
    }
  }

  private static List<CastQueue> castQueue;

  public static String registerTableCast(@Nonnull ItemStack result, @Nonnull ItemStack cast, @Nonnull ItemStack item, float amount, boolean simulate) {
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
      item.setCount(1);

      if (castQueue == null) {
        castQueue = new ArrayList<CastQueue>();
      }
      castQueue.add(new CastQueue(result, cast, item, amount));
    }

    return null;
  }

  public static void registerTableCast(@Nonnull ItemStack result, @Nonnull ItemStack cast, Fluid fluid, float amount) {
    if (!isLoaded || Prep.isInvalid(result) || Prep.isInvalid(cast) || fluid == null) {
      return;
    }

    if (castQueue == null) {
      castQueue = new ArrayList<CastQueue>();
    }
    castQueue.add(new CastQueue(result, cast, fluid, amount));
  }

  private static class SmeltQueue {
    @Nonnull
    ItemStack input;
    @Nonnull
    ItemStack output;
    Fluid fluidOutput;
    float amount;

    public SmeltQueue(@Nonnull ItemStack input, @Nonnull ItemStack output, float amount) {
      super();
      this.input = input;
      this.output = output;
      this.fluidOutput = null;
      this.amount = amount;
    }

    public SmeltQueue(@Nonnull ItemStack input, Fluid fluidOutput, float amount) {
      super();
      this.input = input;
      this.output = Prep.getEmpty();
      this.fluidOutput = fluidOutput;
      this.amount = amount;
    }
  }

  private static List<SmeltQueue> smeltQueue;

  public static void registerSmelterySmelting(@Nonnull ItemStack input, @Nonnull ItemStack output, float amount) {
    if (!isLoaded || Prep.isInvalid(input) || Prep.isInvalid(output)) {
      return;
    }
    if (smeltQueue == null) {
      smeltQueue = new ArrayList<SmeltQueue>();
    }
    smeltQueue.add(new SmeltQueue(input, output, amount));
  }

  public static void registerSmelterySmelting(@Nonnull ItemStack input, Fluid output, float amount) {
    if (!isLoaded || Prep.isInvalid(input) || output == null) {
      return;
    }
    if (smeltQueue == null) {
      smeltQueue = new ArrayList<SmeltQueue>();
    }
    smeltQueue.add(new SmeltQueue(input, output, amount));
  }

  private static class BasinQueue {
    @Nonnull
    ItemStack output;
    @Nonnull
    ItemStack cast;
    Fluid fluid;
    @Nonnull
    ItemStack fluidItem;
    int amount;

    public BasinQueue(@Nonnull ItemStack output, @Nonnull ItemStack cast, @Nonnull ItemStack fluidItem, int amount) {
      this.output = output;
      this.cast = cast;
      this.fluid = null;
      this.fluidItem = fluidItem;
      this.amount = amount;
    }

    public BasinQueue(@Nonnull ItemStack output, @Nonnull ItemStack cast, Fluid fluid, int amount) {
      this.output = output;
      this.cast = cast;
      this.fluid = fluid;
      this.fluidItem = Prep.getEmpty();
      this.amount = amount;
    }
  }

  private static List<BasinQueue> basinQueue;

  public static void registerBasinCasting(@Nonnull ItemStack output, @Nonnull ItemStack cast, @Nonnull ItemStack fluid, int amount) {
    if (!isLoaded || Prep.isInvalid(output) || Prep.isInvalid(fluid)) {
      return;
    }
    if (basinQueue == null) {
      basinQueue = new ArrayList<BasinQueue>();
    }
    basinQueue.add(new BasinQueue(output, cast, fluid, amount));
  }

  public static void registerBasinCasting(@Nonnull ItemStack output, @Nonnull ItemStack cast, Fluid fluid, int amount) {
    if (!isLoaded || Prep.isInvalid(output) || fluid == null) {
      return;
    }
    if (basinQueue == null) {
      basinQueue = new ArrayList<BasinQueue>();
    }
    basinQueue.add(new BasinQueue(output, cast, fluid, amount));
  }

}
