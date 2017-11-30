package crazypants.enderio.machines.machine.tank;

import java.util.Locale;

import javax.annotation.Nonnull;

import com.enderio.core.common.fluid.SmartTank;
import com.enderio.core.common.util.NullHelper;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

public enum EnumTankType implements IStringSerializable {

  NORMAL(16000, false, ""),
  ADVANCED(32000, true, ".advanced");

  public static final @Nonnull PropertyEnum<EnumTankType> KIND = PropertyEnum.<EnumTankType> create("kind", EnumTankType.class);

  private final int size;
  private final boolean explosionResistant;
  private final @Nonnull String suffix;

  private EnumTankType(int size, boolean explosionResistant, @Nonnull String suffix) {
    this.size = size;
    this.explosionResistant = explosionResistant;
    this.suffix = suffix;
  }

  @Override
  public @Nonnull String getName() {
    return NullHelper.notnullJ(name().toLowerCase(Locale.ENGLISH), "String.toLowerCase()");
  }

  public static @Nonnull EnumTankType getType(int meta) {
    return NullHelper.notnullJ(values()[meta >= 0 && meta < values().length ? meta : 0], "Enum.values()");
  }

  public static @Nonnull EnumTankType getType(@Nonnull ItemStack stack) {
    return getType(stack.getMetadata());
  }

  public static int getMeta(@Nonnull EnumTankType value) {
    return value.ordinal();
  }

  public @Nonnull SmartTank getTank() {
    return new SmartTank(size);
  }

  public boolean isExplosionResistant() {
    return explosionResistant;
  }

  public @Nonnull String getSuffix() {
    return suffix;
  }

}
