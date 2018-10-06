package crazypants.enderio.base.material.alloy.endergy;

import java.util.Locale;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.material.alloy.IAlloy;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

public enum AlloyEndergy implements IStringSerializable, IAlloy {

  // endergy
  CRUDE_STEEL("crudeSteel", 2.0f, 0xc3b7b1, 140),
  CRYSTALLINE_ALLOY("crystallineAlloy", 6.0f, 0x86d9d9, 1152),
  MELODIC_ALLOY("melodicAlloy", 5.0f, 0x845e84, 940),
  STELLAR_ALLOY("stellarAlloy", 10.0f, 0xe7eaea, 1202),
  CRYSTAlLINE_PINK_SLIME("crystallinePinkSlime", 7.0f, 0x35a8db, 1174),
  ENERGETIC_SILVER("energeticSilver", 7.0f, 0x79a5c5, 855),
  VIVID_ALLOY("vividAlloy", 4.0f, 0x4bbad6, 650),

  ;

  public final @Nonnull String baseName; // model names for sub-items
  private final @Nonnull String oreName;
  private final float hardness;
  private final int color;
  private final int meltingPoint; // in °C

  private AlloyEndergy(@Nonnull String baseName, float hardness, int color, int meltingPoint) {
    this.baseName = baseName.replaceAll("([A-Z])", "_$0").toLowerCase(Locale.ENGLISH);
    this.oreName = StringUtils.capitalize(baseName);
    this.hardness = hardness;
    this.color = color;
    this.meltingPoint = meltingPoint;
  }

  @Override
  public @Nonnull String getBaseName() {
    return baseName;
  }

  @Override
  public @Nonnull String getFluidName() {
    return baseName;
  }

  @Override
  public float getHardness() {
    return hardness;
  }

  @Override
  public int getColor() {
    return color;
  }

  @Override
  public int getMeltingPoint() {
    return meltingPoint;
  }

  @Override
  public @Nonnull ItemStack getStackNugget() {
    return getStackNugget(1);
  }

  @Override
  public @Nonnull ItemStack getStackNugget(int size) {
    return new ItemStack(ModObject.itemAlloyEndergyNugget.getItemNN(), size, ordinal());
  }

  @Override
  public @Nonnull ItemStack getStackIngot() {
    return getStackIngot(1);
  }

  @Override
  public @Nonnull ItemStack getStackIngot(int size) {
    return new ItemStack(ModObject.itemAlloyEndergyIngot.getItemNN(), size, ordinal());
  }

  @Override
  public @Nonnull ItemStack getStackBall() {
    return getStackBall(1);
  }

  @Override
  public @Nonnull ItemStack getStackBall(int size) {
    return new ItemStack(ModObject.itemAlloyEndergyNugget.getItemNN(), size, ordinal());
  }

  @Override
  public @Nonnull ItemStack getStackBlock() {
    return getStackBlock(1);
  }

  @Override
  public @Nonnull ItemStack getStackBlock(int size) {
    return new ItemStack(ModObject.blockAlloyEndergy.getBlockNN(), size, ordinal());
  }

  @Override
  public @Nonnull String getOreName() {
    return oreName;
  }

  @Override
  public @Nonnull String getOreNugget() {
    return "nugget" + oreName;
  }

  @Override
  public @Nonnull String getOreIngot() {
    return "ingot" + oreName;
  }

  @Override
  public @Nonnull String getOreBall() {
    return "ball" + oreName;
  }

  @Override
  public @Nonnull String getOreBlock() {
    return "block" + oreName;
  }

  @Override
  public @Nonnull String getName() {
    return baseName;
  }

  public static @Nonnull
  AlloyEndergy getTypeFromMeta(int meta) {
    return NullHelper.notnullJ(values()[meta >= 0 && meta < values().length ? meta : 0], "Enum.values()");
  }

  public static int getMetaFromType(@Nonnull AlloyEndergy value) {
    return value.ordinal();
  }

}
