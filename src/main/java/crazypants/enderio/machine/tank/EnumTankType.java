package crazypants.enderio.machine.tank;

import java.util.Locale;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;

public enum EnumTankType implements IStringSerializable {

  NORMAL,
  ADVANCED;

  public static final PropertyEnum<EnumTankType> KIND = PropertyEnum.<EnumTankType> create("kind", EnumTankType.class);

  @Override
  public @Nonnull String getName() {
    return NullHelper.notnullJ(name().toLowerCase(Locale.ENGLISH), "String.toLowerCase()");
  }

  public static @Nonnull EnumTankType getTypeFromMeta(int meta) {
    return NullHelper.notnullJ(values()[meta >= 0 && meta < values().length ? meta : 0], "Enum.values()");
  }

  public static int getMetaFromType(EnumTankType value) {
    return value.ordinal();
  }

}
