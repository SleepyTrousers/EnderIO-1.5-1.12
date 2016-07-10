package crazypants.enderio.conduit;

import java.util.Locale;

import javax.annotation.Nonnull;

import crazypants.util.NullHelper;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;

public enum EnumFacadeState implements IStringSerializable {

  TRANSPARENT,
  SOLID,
  SLAB;

  public static final PropertyEnum<EnumFacadeState> STATE = PropertyEnum.<EnumFacadeState> create("state", EnumFacadeState.class);

  @Override
  public @Nonnull String getName() {
    return NullHelper.notnullJ(name().toLowerCase(Locale.ENGLISH), "String.toLowerCase()");
  }

  public static @Nonnull EnumFacadeState getTypeFromMeta(int meta) {
    return NullHelper.notnullJ(values()[meta >= 0 && meta < values().length ? meta : 0], "Enum.values()");
  }

  public static int getMetaFromType(EnumFacadeState value) {
    return value.ordinal();
  }

}
