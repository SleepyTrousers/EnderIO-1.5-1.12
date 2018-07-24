package crazypants.enderio.endergy.alloy;

import java.util.Locale;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.endergy.init.EndergyObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

public enum EndergyAlloy implements IStringSerializable {

  COMBUSTIVE_METAL("combustiveMetal", 6.0f, 0x998f89, 640),
  CRUDE_STEEL("crudeSteel", 7.0f, 0x998f89, 200),
  CRYSTALLINE_ALLOY("crystallineAlloy", 4.0f, 0xa7ecec, 1230),
  MELODIC_ALLOY("melodicAlloy", 1.0f, 0x956f95, 1354),
  STELLAR_ALLOY("stellarAlloy", 5.2f, 0xe7eaea, 1008)

  ;

  public final @Nonnull String baseName; // model names for sub-items
  private final @Nonnull String oreName;
  private final float hardness;
  private final int color;
  private final int meltingPoint; // in °C

  private EndergyAlloy(@Nonnull String baseName, float hardness, int color, int meltingPoint) {
    this.baseName = baseName.replaceAll("([A-Z])", "_$0").toLowerCase(Locale.ENGLISH);
    this.oreName = StringUtils.capitalize(baseName);
    this.hardness = hardness;
    this.color = color;
    this.meltingPoint = meltingPoint;
  }

  public @Nonnull String getBaseName() {
    return baseName;
  }

  public @Nonnull String getFluidName() {
    return baseName;
  }

  public float getHardness() {
    return hardness;
  }

  public int getColor() {
    return color;
  }

  public int getMeltingPoint() {
    return meltingPoint;
  }

//  public @Nonnull ItemStack getStackNugget() {
//    return getStackNugget(1);
//  }
//
//  public @Nonnull ItemStack getStackNugget(int size) {
//    return new ItemStack(ModObject.itemAlloyNugget.getItemNN(), size, ordinal());
//  }

  public @Nonnull ItemStack getStackIngot() {
    return getStackIngot(1);
  }

  public @Nonnull ItemStack getStackIngot(int size) {
    return new ItemStack(EndergyObject.itemEndergyAlloy.getItemNN(), size, ordinal());
  }

//  public @Nonnull ItemStack getStackBall() {
//    return getStackBall(1);
//  }
//
//  public @Nonnull ItemStack getStackBall(int size) {
//    return new ItemStack(ModObject.itemAlloyBall.getItemNN(), size, ordinal());
//  }
//
//  public @Nonnull ItemStack getStackBlock() {
//    return getStackBlock(1);
//  }
//
//  public @Nonnull ItemStack getStackBlock(int size) {
//    return new ItemStack(ModObject.blockAlloy.getBlockNN(), size, ordinal());
//  }

  public @Nonnull String getOreName() {
    return oreName;
  }

  public @Nonnull String getOreNugget() {
    return "nugget" + oreName;
  }

  public @Nonnull String getOreIngot() {
    return "ingot" + oreName;
  }

  public @Nonnull String getOreBall() {
    return "ball" + oreName;
  }

  public @Nonnull String getOreBlock() {
    return "block" + oreName;
  }

  @Override
  public @Nonnull String getName() {
    return baseName;
  }

  public static @Nonnull
  EndergyAlloy getTypeFromMeta(int meta) {
    return NullHelper.notnullJ(values()[meta >= 0 && meta < values().length ? meta : 0], "Enum.values()");
  }

  public static int getMetaFromType(@Nonnull EndergyAlloy value) {
    return value.ordinal();
  }

}
