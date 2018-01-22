package crazypants.enderio.base.integration.tic;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

public interface ITicHandler {

  void registerTableCast(@Nonnull ItemStack result, @Nonnull ItemStack cast, Fluid fluid, float amount);

  String registerTableCast(@Nonnull ItemStack result, @Nonnull ItemStack cast, @Nonnull ItemStack item, float amount, boolean simulate);

  void registerBasinCasting(@Nonnull ItemStack output, @Nonnull ItemStack cast, Fluid fluid, int amount);

  void registerBasinCasting(@Nonnull ItemStack output, @Nonnull ItemStack cast, @Nonnull ItemStack fluid, int amount);

  void registerSmelterySmelting(@Nonnull ItemStack input, Fluid output, float amount);

  void registerSmelterySmelting(@Nonnull ItemStack input, @Nonnull ItemStack output, float amount);

  void registerAlloyRecipe(@Nonnull ItemStack result, ItemStack... input);

}