package crazypants.enderio.render;

import java.util.Locale;

import net.minecraft.util.IStringSerializable;

public enum EnumRenderMode implements IStringSerializable {
  DEFAULTS,
  AUTO;

  @Override
  public String getName() {
    return name().toLowerCase(Locale.ENGLISH);
  }
}
