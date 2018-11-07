package crazypants.enderio.base.recipe.soul;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.util.CapturedMob;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class BasicSoulBinderRecipe extends AbstractSoulBinderRecipe {

  private @Nonnull ItemStack inputStack;
  private @Nonnull ItemStack outputStack;
  private @Nonnull OutputFilter filter;

  public BasicSoulBinderRecipe(@Nonnull ItemStack inputStack, @Nonnull ItemStack outputStack, int energyRequired, int xpRequired, @Nonnull String uid,
      @Nonnull ResourceLocation... entityNames) {
    super(energyRequired, xpRequired, uid, entityNames);
    this.inputStack = inputStack.copy();
    this.outputStack = outputStack.copy();
    this.filter = new OutputFilter() {
    };
  }

  public BasicSoulBinderRecipe(@Nonnull ItemStack inputStack, @Nonnull ItemStack outputStack, int energyRequired, int xpRequired, @Nonnull String uid,
      @Nonnull NNList<ResourceLocation> entityNames, @Nonnull OutputFilter filter) {
    super(energyRequired, xpRequired, uid, entityNames);
    this.inputStack = inputStack.copy();
    this.outputStack = outputStack.copy();
    this.filter = filter;
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
  @Nonnull
  public ItemStack getOutputStack(@Nonnull ItemStack input, @Nonnull CapturedMob mobType) {
    return filter.apply(getOutputStack(), mobType);
  }

  public interface OutputFilter {

    default @Nonnull ItemStack apply(@Nonnull ItemStack output, @Nonnull CapturedMob mobType) {
      return output;
    }
  }

}
