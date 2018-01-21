package crazypants.enderio.base.integration.tic;

import javax.annotation.Nonnull;

import crazypants.enderio.base.Log;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

public class TicProxy {

  public static final String GLOWSTONE_FLUID_NAME = "glowstone";
  public static final String REDSTONE_FLUID_NAME = "redstone";
  public static final String ENDER_FLUID_NAME = "ender";

  private static ITicHandler handler;

  public static boolean isLoaded() {
    if (handler == null) {
      Log.warn("Early access!");// FIXME deleme
      new RuntimeException().printStackTrace();// FIXME deleme
    }
    return handler != null;
  }

  public static void registerTableCast(@Nonnull ItemStack result, @Nonnull ItemStack cast, Fluid fluid, float amount) {
    if (isLoaded()) {
      handler.registerTableCast(result, cast, fluid, amount);
    }
  }

  public static String registerTableCast(@Nonnull ItemStack result, @Nonnull ItemStack cast, @Nonnull ItemStack item, float amount, boolean simulate) {
    if (isLoaded()) {
      return handler.registerTableCast(result, cast, item, amount, simulate);
    } else {
      return null;
    }
  }

  public static void registerBasinCasting(@Nonnull ItemStack output, @Nonnull ItemStack cast, Fluid fluid, int amount) {
    if (isLoaded()) {
      handler.registerBasinCasting(output, cast, fluid, amount);
    }
  }

  public static void registerBasinCasting(@Nonnull ItemStack output, @Nonnull ItemStack cast, @Nonnull ItemStack fluid, int amount) {
    if (isLoaded()) {
      handler.registerBasinCasting(output, cast, fluid, amount);
    }
  }

  public static void registerSmelterySmelting(@Nonnull ItemStack input, Fluid output, float amount) {
    if (isLoaded()) {
      handler.registerSmelterySmelting(input, output, amount);
    }
  }

  public static void registerSmelterySmelting(@Nonnull ItemStack input, @Nonnull ItemStack output, float amount) {
    if (isLoaded()) {
      handler.registerSmelterySmelting(input, output, amount);
    }
  }

  public static void registerAlloyRecipe(@Nonnull ItemStack result, ItemStack... input) {
    if (isLoaded()) {
      handler.registerAlloyRecipe(result, input);
    }
  }

  public static void register(@Nonnull ITicHandler instance) {
    handler = instance;
  }

}
