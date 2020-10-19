package crazypants.enderio.base.recipe.soul;

import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.EntityUtil;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.recipe.RecipeLevel;
import crazypants.enderio.util.CapturedMob;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class DynamicSoulBinderRecipe extends BasicSoulBinderRecipe {

  private final @Nonnull Predicate<ResourceLocation> entityFilter;

  public DynamicSoulBinderRecipe(@Nonnull ItemStack inputStack, @Nonnull ItemStack outputStack, int energyRequired, int xpRequired, @Nonnull String uid,
      @Nonnull RecipeLevel recipeLevel, @Nonnull Predicate<ResourceLocation> entityFilter, @Nonnull OutputFilter filter) {
    super(inputStack, outputStack, energyRequired, xpRequired, uid, recipeLevel, EntityUtil.getAllRegisteredMobNames(), filter);
    this.entityFilter = entityFilter;
  }

  @SuppressWarnings("null")
  @Override
  public @Nonnull NNList<ResourceLocation> getSupportedSouls() {
    // this is only called by the JEI plugin, so we can get away with filtering the list on each call
    return super.getSupportedSouls().stream().filter(entityFilter).collect(Collectors.toCollection(() -> new NNList<ResourceLocation>()));
  }

  @Override
  protected boolean isValidInputSoul(@Nonnull CapturedMob mobType) {
    return super.getSupportedSouls().contains(mobType.getEntityName()) && entityFilter.test(mobType.getEntityName());
  }

}
