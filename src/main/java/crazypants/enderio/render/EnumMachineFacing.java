package crazypants.enderio.render;

import java.util.Locale;

import net.minecraft.util.IStringSerializable;

public enum EnumMachineFacing implements IStringSerializable {
  NORTH,
  EAST,
  SOUTH,
  WEST;

  @Override
  public String getName() {
    return name().toLowerCase(Locale.ENGLISH);
  }
}
