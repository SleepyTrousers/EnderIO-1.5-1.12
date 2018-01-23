package crazypants.enderio.base.integration.tic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.Log;
import crazypants.enderio.util.Prep;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class TicProxy {

  private static boolean isLoaded = false;
  private static Method getMelting;
  private static Method registerAlloy;
  private static Method registerTableCasting;
  private static Method registerMelting;
  private static Method registerBasinCasting;
  private static Method getResult;

  static {
    if (Loader.isModLoaded("tconstruct")) {
      try {
        Class<Object> TinkerRegistry = ReflectionHelper.getClass(TicProxy.class.getClassLoader(), "slimeknights.tconstruct.library.TinkerRegistry");
        getMelting = ReflectionHelper.findMethod(TinkerRegistry, "getMelting", (String) null, ItemStack.class); // MeltingRecipe
        registerAlloy = ReflectionHelper.findMethod(TinkerRegistry, "registerAlloy", (String) null, FluidStack.class, FluidStack[].class); // void
        registerTableCasting = ReflectionHelper.findMethod(TinkerRegistry, "registerTableCasting", (String) null, ItemStack.class, ItemStack.class, Fluid.class,
            int.class); // void
        registerMelting = ReflectionHelper.findMethod(TinkerRegistry, "registerMelting", (String) null, ItemStack.class, Fluid.class, int.class); // void
        registerBasinCasting = ReflectionHelper.findMethod(TinkerRegistry, "registerBasinCasting", (String) null, ItemStack.class, ItemStack.class, Fluid.class,
            int.class); // void

        Class<Object> MeltingRecipe = ReflectionHelper.getClass(TicProxy.class.getClassLoader(), "slimeknights.tconstruct.library.smeltery.MeltingRecipe");
        getResult = ReflectionHelper.findMethod(MeltingRecipe, "getResult", (String) null); // FluidStack

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

  public static void registerTableCast(@Nonnull ItemStack result, @Nonnull ItemStack cast, Fluid fluid, float amount, boolean consumeCast) {
    if (isLoaded()) {
      handler.registerTableCast(result, cast, fluid, amount, consumeCast);
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

  public static String registerTableCast(@Nonnull ItemStack result, @Nonnull ItemStack cast, @Nonnull ItemStack item, float amount, boolean consumeCast,
      boolean simulate) {
    if (isLoaded()) {
      return handler.registerTableCast(result, cast, item, amount, consumeCast, simulate);
    } else {
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
