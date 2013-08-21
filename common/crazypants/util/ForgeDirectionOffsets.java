package crazypants.util;

import net.minecraftforge.common.ForgeDirection;
import crazypants.vecmath.Vector3d;

public final class ForgeDirectionOffsets {

  public static final Vector3d[] OFFSETS = new Vector3d[ForgeDirection.values().length];

  static {
    for (ForgeDirection dir : ForgeDirection.values()) {
      OFFSETS[dir.ordinal()] = new Vector3d(dir.offsetX, dir.offsetY, dir.offsetZ);
    }
  }

  public static Vector3d forDir(ForgeDirection dir) {
    return OFFSETS[dir.ordinal()];
  }

  public static Vector3d forDirCopy(ForgeDirection dir) {
    return new Vector3d(OFFSETS[dir.ordinal()]);
  }

  public static Vector3d offsetScaled(ForgeDirection dir, double scale) {
    Vector3d res = forDirCopy(dir);
    res.scale(scale);
    return res;
  }

  private ForgeDirectionOffsets() {
  }

  public static boolean isPositiveOffset(ForgeDirection dir) {
    return dir == ForgeDirection.SOUTH || dir == ForgeDirection.EAST || dir == ForgeDirection.UP;
  }

}
