package crazypants.enderio.integration.tic.recipes;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

import com.enderio.core.common.util.NullHelper;

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

  private static void registerAlloyRecipe(Pair<ItemStack, ItemStack[]> alloy) {
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
    TinkerRegistry.registerAlloy(fluidResult, fluids);
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
      if (basin.getFluid() != null) {
        TinkerRegistry.registerBasinCasting(basin.getOutput(), basin.getCast(), basin.getFluid(), basin.getAmount());
        Log.debug("Tinkers.registerBasinCasting: " + basin.getOutput() + ", " + basin.getCast() + ", " + basin.getFluid().getName() + ", " + basin.getAmount());
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
        Log.warn("Item used in cast recipe '" + cast.getItem() + "' doesn't smelt into a fluid");
      } else {
        TinkerRegistry.registerTableCasting(new CastingRecipe(cast.getResult(), Prep.isValid(cast.getCast()) ? RecipeMatch.ofNBT(cast.getCast()) : null,
            cast.getFluid(), (int) Math.ceil(cast.getAmount()), cast.isConsumeCast(), false));
        Log.debug("Tinkers.registerTableCasting: " + cast.getResult() + ", " + cast.getCast() + ", " + cast.getFluid().getName() + ", " + cast.getAmount());
      }
    }
    TiCQueues.getCastQueue().clear();
  }

  public static void registerAlloys() {
    for (Pair<ItemStack, ItemStack[]> alloy : TiCQueues.getAlloyQueue()) {
      registerAlloyRecipe(alloy);
    }
    TiCQueues.getAlloyQueue().clear();
  }

  public static void registerSmeltings() {
    for (SmeltQueue smelt : TiCQueues.getSmeltQueue()) { // 1st because it may provide fluids for later
      if (smelt.getFluidOutput() == null) {
        FluidStack fluid = getFluidForItems(smelt.getOutput());
        if (fluid == null) {
          Log.warn("Item used in Smeltery recipe '" + smelt.getOutput() + "' doesn't smelt into a fluid");
        } else {
          smelt.setFluidOutput(fluid.getFluid());
          smelt.setAmount(smelt.getAmount() * fluid.amount);
        }
      }
      if (smelt.getFluidOutput() != null) {
        TinkerRegistry.registerMelting(smelt.getInput(), smelt.getFluidOutput(), (int) Math.max(1, Math.floor(smelt.getAmount())));
        Log.debug("Tinkers.registerMelting: " + smelt.getInput() + ", " + smelt.getFluidOutput().getName() + ", " + smelt.getAmount());
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
      Log.info("Failed to get Tinker's Construct melting recipe for " + itemStack);
      return null;
    }
    FluidStack result = melting.getResult();
    if (result != null) {
      result.amount *= input.getCount();
      return result;
    } else {
      Log.info("Failed to get Tinker's Construct melting recipe result for " + itemStack + " -> " + result);
    }
    return null;
  }

}
