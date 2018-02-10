package crazypants.enderio.integration.tic.recipes;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;
import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.Log;
import crazypants.enderio.integration.tic.queues.BasinQueue;
import crazypants.enderio.integration.tic.queues.CastQueue;
import crazypants.enderio.integration.tic.queues.SmeltQueue;
import crazypants.enderio.integration.tic.queues.TiCQueues;
import crazypants.enderio.integration.tic.queues.TicHandler;
import crazypants.enderio.util.Prep;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;

public class TicRegistration {

  private static void registerAlloyRecipe(Pair<Things, NNList<Things>> alloy) {
    final Things result = NullHelper.notnull(alloy.getLeft(), "missing result item stack in alloy recipe");
    final NNList<Things> input = alloy.getRight();
    FluidStack fluidResult = getFluidForItems(result);
    if (fluidResult == null) {
      tryBasinAloying(result, input);
      return;
    }

    FluidStack[] fluids = new FluidStack[input.size()];
    List<String> debug = new ArrayList<>();
    for (int i = 0; i < input.size(); i++) {
      if ((fluids[i] = getFluidForItems(NullHelper.notnull(input.get(i), "missing input item stack in alloy recipe"))) == null) {
        return;
      }
      debug.add(toString(fluids[i]));
    }

    gcd(fluidResult, fluids);
    TinkerRegistry.registerAlloy(fluidResult, fluids);
    Log.debug("Tinkers.registerAlloy: " + toString(fluidResult) + ", " + debug);
  }

  private static void tryBasinAloying(@Nonnull Things result, NNList<Things> inputs) {
    if (!result.isValid() || result.getItemStack().getCount() != 1 || !(result.getItemStack().getItem() instanceof ItemBlock) || inputs.size() != 2) {
      return;
    }
    final Things input0 = inputs.get(0);
    final Things input1 = inputs.get(1);
    if (!input0.isValid() || !input1.isValid()) {
      return;
    }
    FluidStack a = getFluidForItems(input0);
    FluidStack b = getFluidForItems(input1);
    if ((a == null) == (b == null)) {
      return;
    }
    if (a == null && !(input0.getItemStack().getCount() == 1 && input0.getItemStack().getItem() instanceof ItemBlock)) {
      return;
    }
    if (b == null && !(input1.getItemStack().getCount() == 1 && input1.getItemStack().getItem() instanceof ItemBlock)) {
      return;
    }

    if (a != null) {
      TicHandler.instance.registerBasinCasting(result, input1, a.getFluid(), a.amount);
    } else if (b != null) {
      TicHandler.instance.registerBasinCasting(result, input0, b.getFluid(), b.amount);
    }
  }

  public static void registerBasinCasting() {
    for (BasinQueue basin : TiCQueues.getBasinQueue()) {
      if (basin.getFluid() == null) {
        FluidStack fluid = getFluidForItems(basin.getFluidItem());
        if (fluid != null) {
          basin.setFluid(fluid.getFluid());
          basin.setAmount(basin.getAmount() * fluid.amount);
        }
      }
      if (basin.getFluid() == null) {
        Log.warn("Item used in basin cast recipe '" + toString(basin.getFluidItem()) + "' doesn't smelt into a fluid");
      }
      if (!basin.getOutput().isValid()) {
        Log.warn("Item used in basin cast recipe '" + toString(basin.getOutput()) + "' doesn't exist");
      }
      if (!basin.getCast().isEmpty()) {
        for (NNIterator<ItemStack> itr = basin.getCast().getItemStacks().fastIterator(); itr.hasNext();) {
          ItemStack castStack = itr.next();
          TinkerRegistry.registerBasinCasting(basin.getOutput().getItemStack(), castStack, basin.getFluid(), (int) Math.ceil(basin.getAmount()));
          Log.debug("Tinkers.registerBasinCasting: " + toString(basin.getOutput()) + ", " + toString(castStack) + ", " + basin.getFluid().getName() + ", "
              + basin.getAmount());
        }
      } else {
        TinkerRegistry.registerBasinCasting(basin.getOutput().getItemStack(), Prep.getEmpty(), basin.getFluid(), (int) Math.ceil(basin.getAmount()));
        Log.debug("Tinkers.registerBasinCasting: " + toString(basin.getOutput()) + ", (empty), " + basin.getFluid().getName() + ", " + basin.getAmount());
      }
    }
    TiCQueues.getBasinQueue().clear();
  }

  public static void registerTableCasting() {
    for (CastQueue cast : TiCQueues.getCastQueue()) {
      if (cast.getFluid() == null) {
        FluidStack fluid = getFluidForItems(cast.getItem());
        if (fluid != null) {
          cast.setFluid(fluid.getFluid());
          cast.setAmount(cast.getAmount() * fluid.amount);
        }
      }
      if (cast.getFluid() == null) {
        Log.warn("Item used in cast recipe '" + toString(cast.getItem()) + "' doesn't smelt into a fluid");
      } else if (!cast.getResult().isValid()) {
        Log.warn("Item used in cast recipe '" + toString(cast.getResult()) + "' doesn't exist");
      } else {
        if (!cast.getCast().isEmpty()) {
          for (NNIterator<ItemStack> itr = cast.getCast().getItemStacks().fastIterator(); itr.hasNext();) {
            ItemStack castStack = itr.next();
            TinkerRegistry.registerTableCasting(new CastingRecipe(cast.getResult().getItemStack(), RecipeMatch.ofNBT(castStack), cast.getFluid(),
                (int) Math.ceil(cast.getAmount()), cast.isConsumeCast(), false));
            Log.debug("Tinkers.registerTableCasting: " + toString(cast.getResult()) + ", " + toString(castStack) + ", " + cast.getFluid().getName() + ", "
                + cast.getAmount());
          }
        } else {
          TinkerRegistry.registerTableCasting(
              new CastingRecipe(cast.getResult().getItemStack(), null, cast.getFluid(), (int) Math.ceil(cast.getAmount()), cast.isConsumeCast(), false));
          Log.debug("Tinkers.registerTableCasting: " + toString(cast.getResult()) + ", (no cast), " + cast.getFluid().getName() + ", " + cast.getAmount());
        }
      }
    }
    TiCQueues.getCastQueue().clear();
  }

  public static void registerAlloys() {
    for (Pair<Things, NNList<Things>> alloy : TiCQueues.getAlloyQueue()) {
      registerAlloyRecipe(alloy);
    }
    TiCQueues.getAlloyQueue().clear();
  }

  public static void registerSmeltings() {
    for (SmeltQueue smelt : TiCQueues.getSmeltQueue()) { // 1st because it may provide fluids for later
      if (smelt.getFluidOutput() == null) {
        FluidStack fluid = getFluidForItems(smelt.getOutput());
        if (fluid == null) {
          Log.warn("Item used in Smeltery recipe '" + toString(smelt.getOutput()) + "' doesn't smelt into a fluid");
        } else {
          smelt.setFluidOutput(fluid.getFluid());
          smelt.setAmount(smelt.getAmount() * fluid.amount);
        }
      }
      if (smelt.getFluidOutput() != null) {
        for (ItemStack in : smelt.getInput().getItemStacks()) {
          TinkerRegistry.registerMelting(in, smelt.getFluidOutput(), (int) Math.max(1, Math.floor(smelt.getAmount())));
          Log.debug("Tinkers.registerMelting: " + toString(in) + ", " + smelt.getFluidOutput().getName() + ", " + smelt.getAmount());
        }
      }
    }
    TiCQueues.getSmeltQueue().clear();
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

  private static FluidStack getFluidForItems(@Nonnull ItemStack input) {
    ItemStack itemStack = input.copy();
    itemStack.setCount(1);
    MeltingRecipe melting = TinkerRegistry.getMelting(itemStack);
    if (melting == null) {
      // For some reason this recipe isn't yet available in postInit...
      if (itemStack.getItem() == Item.getItemFromBlock(Blocks.OBSIDIAN)) {
        Fluid fluid = FluidRegistry.getFluid("obsidian");
        if (fluid != null) {
          return new FluidStack(fluid, 288 * input.getCount());
        }
      }
      Log.debug("Failed to get Tinker's Construct melting recipe for " + toString(itemStack));
      return null;
    }
    FluidStack result = melting.getResult();
    if (result != null) {
      result.amount *= input.getCount();
      return result;
    } else {
      Log.info("Failed to get Tinker's Construct melting recipe result for " + toString(itemStack) + " -> " + toString(result));
      return null;
    }
  }

  private static FluidStack getFluidForItems(Things item) {
    if (item != null) {
      NNList<ItemStack> itemStacks = item.getItemStacks();
      for (NNIterator<ItemStack> itr = itemStacks.fastIterator(); itr.hasNext();) {
        FluidStack fluidStack = getFluidForItems(itr.next());
        if (fluidStack != null) {
          return fluidStack;
        }
      }
    }
    return null;
  }

  private static @Nonnull String toString(Things o) {
    return (o == null || o.isEmpty()) ? "(empty)" : (o + " (" + toString(o.getItemStacks()) + ")");
  }

  private static @Nonnull String toString(@Nonnull List<ItemStack> o) {
    List<String> result = new NNList<>();
    for (ItemStack itemStack : o) {
      result.add(toString(itemStack));
    }
    return "" + result.toString();
  }

  private static @Nonnull String toString(ItemStack o) {
    return (o == null || Prep.isInvalid(o)) ? "(empty)" : (o + " (" + o.getDisplayName() + ")");
  }

  private static @Nonnull String toString(FluidStack o) {
    return o == null ? "(null)" : (o + " (" + o.getLocalizedName() + ")");
  }

}
