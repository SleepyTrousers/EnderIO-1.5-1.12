package crazypants.enderio.base.material.alloy;

import java.util.Locale;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.init.ModObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

public enum Alloy implements IStringSerializable {

  ELECTRICAL_STEEL("electricalSteel", 6.0f, 0xa9a9a9, 1202),
  ENERGETIC_ALLOY("energeticAlloy", 7.0f, 0xd9934c, 855),
  VIBRANT_ALLOY("vibrantAlloy", 4.0f, 0xb6c870, 640),
  REDSTONE_ALLOY("redstoneAlloy", 1.0f, 0xb12727, 1084),
  CONDUCTIVE_IRON("conductiveIron", 5.2f, 0xab5d5f, 1127),
  PULSATING_IRON("pulsatingIron", 7.0f, 0x2c9044, 1132),
  DARK_STEEL("darkSteel", 10.0f, 0x6c6c6c, 1540),
  SOULARIUM("soularium", 10.0f, 0x695b4d, 363);

  public final @Nonnull String baseName; // model names for sub-items
  private final @Nonnull String oreName;
  private final float hardness;
  private final int color;
  private final int meltingPoint; // in °C

  private Alloy(@Nonnull String baseName, float hardness, int color, int meltingPoint) {
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

  public @Nonnull ItemStack getStackNugget() {
    return getStackNugget(1);
  }

  public @Nonnull ItemStack getStackNugget(int size) {
    return new ItemStack(ModObject.itemAlloyNugget.getItemNN(), size, ordinal());
  }

  public @Nonnull ItemStack getStackIngot() {
    return getStackIngot(1);
  }

  public @Nonnull ItemStack getStackIngot(int size) {
    return new ItemStack(ModObject.itemAlloyIngot.getItemNN(), size, ordinal());
  }

  public @Nonnull ItemStack getStackBall() {
    return getStackBall(1);
  }

  public @Nonnull ItemStack getStackBall(int size) {
    return new ItemStack(ModObject.itemAlloyBall.getItemNN(), size, ordinal());
  }

  public @Nonnull ItemStack getStackBlock() {
    return getStackBlock(1);
  }

  public @Nonnull ItemStack getStackBlock(int size) {
    return new ItemStack(ModObject.blockAlloy.getBlockNN(), size, ordinal());
  }

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

  public static @Nonnull Alloy getTypeFromMeta(int meta) {
    return NullHelper.notnullJ(values()[meta >= 0 && meta < values().length ? meta : 0], "Enum.values()");
  }

  public static int getMetaFromType(@Nonnull Alloy value) {
    return value.ordinal();
  }

}
