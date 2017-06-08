package crazypants.enderio.recipe.soul;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.EntityUtil;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.config.Config;
import crazypants.enderio.item.spawner.BrokenSpawnerHandler;
import crazypants.util.CapturedMob;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import static crazypants.enderio.init.ModObject.itemBrokenSpawner;

public class SoulBinderSpawnerRecipe extends AbstractSoulBinderRecipe {

  public static final @Nonnull SoulBinderSpawnerRecipe instance = new SoulBinderSpawnerRecipe();

  public SoulBinderSpawnerRecipe() {
    super(Config.soulBinderBrokenSpawnerRF, Config.soulBinderBrokenSpawnerLevels, "SoulFuserSpawnerRecipe");
  }

  @Override
  protected @Nonnull ItemStack getOutputStack(@Nonnull ItemStack input, @Nonnull CapturedMob mobType) {
    return mobType.toStack(itemBrokenSpawner.getItemNN(), input.getMetadata(), 1);
  }

  @Override
  protected boolean isValidInputSoul(@Nonnull CapturedMob mobType) {
    return getSupportedSouls().contains(mobType.getEntityName()) && !BrokenSpawnerHandler.isBlackListed(mobType.getEntityName());
  }

  @Override
  public @Nonnull ItemStack getInputStack() {
    return new ItemStack(itemBrokenSpawner.getItemNN());
  }

  @Override
  public @Nonnull ItemStack getOutputStack() {
    return new ItemStack(itemBrokenSpawner.getItemNN());
  }

  @Override
  public @Nonnull NNList<ResourceLocation> getSupportedSouls() {
    return EntityUtil.getAllRegisteredMobNames();
  }

}
