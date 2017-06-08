package crazypants.enderio.recipe.soul;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.EntityUtil;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.block.painted.EnumPressurePlateType;
import crazypants.enderio.config.Config;
import crazypants.util.CapturedMob;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import static crazypants.enderio.init.ModObject.blockPaintedPressurePlate;

public class SoulBinderTunedPressurePlateRecipe extends AbstractSoulBinderRecipe {

  public static final @Nonnull SoulBinderTunedPressurePlateRecipe instance1 = new SoulBinderTunedPressurePlateRecipe(false, "iTunesRecipe");
  public static final @Nonnull SoulBinderTunedPressurePlateRecipe instance2 = new SoulBinderTunedPressurePlateRecipe(true, "winampRecipe");

  private final boolean silent;

  public SoulBinderTunedPressurePlateRecipe(boolean silent, String uid) {
    super(Config.soulBinderTunedPressurePlateRF, Config.soulBinderTunedPressurePlateLevels, "SoulFuser:" + uid);
    this.silent = silent;
  }

  @Override
  protected @Nonnull ItemStack getOutputStack(@Nonnull ItemStack input, @Nonnull CapturedMob mobType) {
    ItemStack result = input.copy();
    result.setItemDamage(EnumPressurePlateType.getMetaFromType(EnumPressurePlateType.TUNED, silent));
    result.setTagCompound(mobType.toNbt(result.getTagCompound()));
    result.setCount(1);
    return result;
  }

  @Override
  protected boolean isValidInputItem(@Nonnull ItemStack item) {
    if (Block.getBlockFromItem(item.getItem()) == blockPaintedPressurePlate.getBlock()) {
      EnumPressurePlateType type = EnumPressurePlateType.getTypeFromMeta(item.getMetadata());
      boolean silentFromMeta = EnumPressurePlateType.getSilentFromMeta(item.getMetadata());
      return (type == EnumPressurePlateType.SOULARIUM || type == EnumPressurePlateType.TUNED) && silentFromMeta == silent;
    }
    return false;
  }

  @Override
  public @Nonnull ItemStack getInputStack() {
    return new ItemStack(blockPaintedPressurePlate.getBlockNN(), 1, EnumPressurePlateType.SOULARIUM.getMetaFromType(silent));
  }

  @Override
  public @Nonnull ItemStack getOutputStack() {
    return new ItemStack(blockPaintedPressurePlate.getBlockNN(), 1, EnumPressurePlateType.TUNED.getMetaFromType(silent));
  }

  @Override
  public @Nonnull NNList<ResourceLocation> getSupportedSouls() {
    return EntityUtil.getAllRegisteredMobNames();
  }

}
