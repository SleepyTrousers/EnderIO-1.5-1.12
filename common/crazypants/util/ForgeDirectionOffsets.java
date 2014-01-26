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

  public ForgeDirection closest(float x, float y, float z) {
    float ax = Math.abs(x);
    float ay = Math.abs(y);
    float az = Math.abs(z);

    if(ax >= ay && ax >= az) {
      return x > 0 ? ForgeDirection.EAST : ForgeDirection.WEST;
    }
    if(ay >= ax && ay >= az) {
      return y > 0 ? ForgeDirection.UP : ForgeDirection.DOWN;
    }
    return z > 0 ? ForgeDirection.SOUTH : ForgeDirection.NORTH;
  }

  private ForgeDirectionOffsets() {
  }

  public static boolean isPositiveOffset(ForgeDirection dir) {
    return dir == ForgeDirection.SOUTH || dir == ForgeDirection.EAST || dir == ForgeDirection.UP;
  }

}
