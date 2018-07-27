package crazypants.enderio.base.integration.tic;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.stackable.Things;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

public class TicProxy {

  public static final String GLOWSTONE_FLUID_NAME = "glowstone";
  public static final String REDSTONE_FLUID_NAME = "redstone";
  public static final String ENDER_FLUID_NAME = "ender";

  private static ITicRecipeHandler handler;
  private static ITicModifierHandler mhandler;

  public static boolean isLoaded() {
    return handler != null && mhandler != null;
  }

  // Recipes

  public static void registerTableCast(@Nonnull Things result, @Nonnull Things cast, Fluid fluid, float amount, boolean consumeCast) {
    if (isLoaded()) {
      handler.registerTableCast(result, cast, fluid, amount, consumeCast);
    }
  }

  public static void registerTableCast(@Nonnull Things result, @Nonnull Things cast, @Nonnull Things item, float amount, boolean consumeCast) {
    if (isLoaded()) {
      handler.registerTableCast(result, cast, item, amount, consumeCast);
    }
  }

  public static void registerBasinCasting(@Nonnull Things output, @Nonnull Things cast, Fluid fluid, int amount) {
    if (isLoaded()) {
      handler.registerBasinCasting(output, cast, fluid, amount);
    }
  }

  public static void registerBasinCasting(@Nonnull Things output, @Nonnull Things cast, @Nonnull Things fluid, float amount) {
    if (isLoaded()) {
      handler.registerBasinCasting(output, cast, fluid, amount);
    }
  }

  public static void registerSmelterySmelting(@Nonnull Things input, Fluid output, float amount) {
    if (isLoaded()) {
      handler.registerSmelterySmelting(input, output, amount);
    }
  }

  public static void registerSmelterySmelting(@Nonnull Things input, @Nonnull Things output, float amount) {
    if (isLoaded()) {
      handler.registerSmelterySmelting(input, output, amount);
    }
  }

  public static void registerAlloyRecipe(@Nonnull Things result, NNList<Things> input) {
    if (isLoaded()) {
      handler.registerAlloyRecipe(result, input);
    }
  }

  // Modifiers

  public static boolean isBroken(ItemStack itemStack) {
    if (isLoaded()) {
      return mhandler.isBroken(itemStack);
    }
    return false;
  }

  public static boolean isTinkerItem(@Nonnull ItemStack itemStack) {
    if (isLoaded()) {
      return mhandler.isTinkerItem(itemStack);
    }
    return false;
  }

  public static int getBehadingLevel(@Nonnull ItemStack itemStack) {
    if (isLoaded()) {
      return mhandler.getBehadingLevel(itemStack);
    }
    return 0;
  }

  // Registration

  public static void register(@Nonnull ITicRecipeHandler recipeInstance, @Nonnull ITicModifierHandler modifierInstance) {
    handler = recipeInstance;
    mhandler = modifierInstance;
  }

}
