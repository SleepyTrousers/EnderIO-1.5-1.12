package crazypants.enderio.base.recipe.soul;

import javax.annotation.Nonnull;

import crazypants.enderio.util.CapturedMob;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class BasicSoulBinderRecipe extends AbstractSoulBinderRecipe {

  private @Nonnull ItemStack inputStack;
  private @Nonnull ItemStack outputStack;

  public BasicSoulBinderRecipe(@Nonnull ItemStack inputStack, @Nonnull ItemStack outputStack, int energyRequired, int xpRequired, @Nonnull String uid,
      @Nonnull ResourceLocation... entityNames) {
    super(energyRequired, xpRequired, uid, entityNames);
    this.inputStack = inputStack.copy();
    this.outputStack = outputStack.copy();
  }

  @Override
  public @Nonnull ItemStack getInputStack() {
    return inputStack.copy();
  }

  @Override
  public @Nonnull ItemStack getOutputStack() {
    return outputStack.copy();
  }

  @Override
  protected @Nonnull ItemStack getOutputStack(@Nonnull ItemStack input, @Nonnull CapturedMob mobType) {
    return getOutputStack();
  }

}
