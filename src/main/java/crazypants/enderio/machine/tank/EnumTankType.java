package crazypants.enderio.machine.tank;

import java.util.Locale;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;

public enum EnumTankType implements IStringSerializable {

  NORMAL,
  ADVANCED;

  public static final PropertyEnum<EnumTankType> KIND = PropertyEnum.<EnumTankType> create("kind", EnumTankType.class);

  @Override
  public String getName() {
    return name().toLowerCase(Locale.ENGLISH);
  }

  public static EnumTankType getTypeFromMeta(int meta) {
    return values()[meta >= 0 && meta < values().length ? meta : 0];
  }

  public static int getMetaFromType(EnumTankType value) {
    return value.ordinal();
  }

}
