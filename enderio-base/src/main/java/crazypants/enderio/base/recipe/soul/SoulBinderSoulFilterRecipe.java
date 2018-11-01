package crazypants.enderio.base.recipe.soul;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.EntityUtil;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.filter.FilterRegistry;
import crazypants.enderio.base.filter.item.SoulFilter;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.init.ModObjectRegistry;
import crazypants.enderio.util.CapturedMob;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class SoulBinderSoulFilterRecipe extends AbstractSoulBinderRecipe {

  public static final @Nonnull SoulBinderSoulFilterRecipe instance1 = new SoulBinderSoulFilterRecipe("soulMusicRecipe", ModObject.itemBasicItemFilter,
      ModObject.itemSoulFilterNormal);
  public static final @Nonnull SoulBinderSoulFilterRecipe instance2 = new SoulBinderSoulFilterRecipe("thiefOfTimeRecipe", ModObject.itemBigItemFilter,
      ModObject.itemSoulFilterBig);

  private final @Nonnull ModObject in, out;

  public SoulBinderSoulFilterRecipe(@Nonnull String uid, @Nonnull ModObject in, @Nonnull ModObject out) {
    super(Config.soulBinderSoulFilterRF, Config.soulBinderSoulFilterLevels, "SoulFuser:" + uid);
    this.in = in;
    this.out = out;
  }

  @Override
  protected @Nonnull ItemStack getOutputStack(@Nonnull ItemStack input, @Nonnull CapturedMob mobType) {
    final ItemStack result = new ItemStack(out.getItemNN());
    SoulFilter filter = (SoulFilter) FilterRegistry.getFilterForUpgrade(result);
    if (filter != null) {
      filter.getSouls().add(mobType);
      FilterRegistry.writeFilterToStack(filter, result);
    }
    return result;
  }

  @Override
  protected boolean isValidInputItem(@Nonnull ItemStack item) {
    return ModObjectRegistry.getModObject(item.getItem()) == in;
  }

  @Override
  public @Nonnull ItemStack getInputStack() {
    return new ItemStack(in.getItemNN());
  }

  @Override
  public @Nonnull ItemStack getOutputStack() {
    return new ItemStack(out.getItemNN());
  }

  @Override
  public @Nonnull NNList<ResourceLocation> getSupportedSouls() {
    return EntityUtil.getAllRegisteredMobNames();
  }

}
