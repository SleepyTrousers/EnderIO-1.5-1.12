package crazypants.enderio.machine.invpanel.remote;

import net.minecraft.item.ItemStack;

public enum ItemRemoteInvAccessType {
  BASIC("basic", 160, false, 1, true),
  ADVANCED("advanced", -1, false, 2, true),
  ENDER("ender", -1, true, 4, false);

  private final String nameSuffix;
  private final int range;
  private final boolean interdimensional;
  private final int capacity;
  private final boolean visible;

  private ItemRemoteInvAccessType(String nameSuffix, int range, boolean interdimensional, int capacity, boolean visible) {
    this.nameSuffix = nameSuffix;
    this.range = range;
    this.interdimensional = interdimensional;
    this.capacity = capacity;
    this.visible = visible;
  }

  public int toMetadata() {
    return ordinal();
  }

  public static ItemRemoteInvAccessType fromMetadata(int meta) {
    return values()[meta >= 0 && meta < values().length ? meta : 0];
  }

  public static ItemRemoteInvAccessType fromStack(ItemStack stack) {
    return fromMetadata(stack != null ? stack.getMetadata() : 0);
  }

  public boolean inRange(int dim0, int x0, int y0, int z0, int dim1, int x1, int y1, int z1) {
    if (!interdimensional && dim0 != dim1) {
      return false;
    }
    if (range < 0) {
      return true;
    }
    if (Math.abs(x0 - x1) > range || Math.abs(y0 - y1) > range || Math.abs(z0 - z1) > range) {
      return false;
    }
    return true;
  }

  public String getNameSuffix() {
    return nameSuffix;
  }

  public String getUnlocalizedName(String basename) {
    return basename + getNameSuffix();
  }

  public int getRange() {
    return range;
  }

  public boolean isInterdimensional() {
    return interdimensional;
  }

  public int getCapacity() {
    return capacity;
  }

  public boolean isVisible() {
    return visible;
  }

}