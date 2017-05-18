package crazypants.enderio.item.skull;

import java.util.Locale;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import net.minecraft.util.IStringSerializable;

public enum SkullType implements IStringSerializable {

  BASE("base", false),
  REANIMATED("reanimated", true),
  TORMENTED("tormented", false),
  REANIMATED_TORMENTED("reanimated_tormented", true);

  final @Nonnull String name;
  final boolean showEyes;

  SkullType(@Nonnull String name, boolean showEyes) {
    this.name = name;
    this.showEyes = showEyes;
  }

  @Override
  public @Nonnull String getName() {
    return NullHelper.notnullJ(name.toLowerCase(Locale.ENGLISH), "String.toLowerCase()");
  }

  public static @Nonnull SkullType getTypeFromMeta(int meta) {
    return NullHelper.notnullJ(values()[meta >= 0 && meta < values().length ? meta : 0], "Enum.values()");
  }
}