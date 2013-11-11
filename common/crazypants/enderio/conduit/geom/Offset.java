package crazypants.enderio.conduit.geom;

import net.minecraftforge.common.ForgeDirection;

/**
 * Offset vectors are based on Y up.
 * 
 * @author brad
 */
public enum Offset {

  NONE(0, 0, 0),
  UP(ForgeDirection.UP),
  DOWN(ForgeDirection.DOWN),
  EAST(ForgeDirection.EAST),
  WEST(ForgeDirection.WEST),
  SOUTH(ForgeDirection.SOUTH),
  NORTH(ForgeDirection.NORTH),
  EAST_UP(ForgeDirection.EAST, ForgeDirection.UP),
  WEST_UP(ForgeDirection.WEST, ForgeDirection.UP),
  SOUTH_UP(ForgeDirection.SOUTH, ForgeDirection.UP),
  NORTH_UP(ForgeDirection.NORTH, ForgeDirection.UP),
  EAST_DOWN(ForgeDirection.EAST, ForgeDirection.DOWN),
  WEST_DOWN(ForgeDirection.WEST, ForgeDirection.DOWN),
  SOUTH_DOWN(ForgeDirection.SOUTH, ForgeDirection.DOWN),
  NORTH_DOWN(ForgeDirection.NORTH, ForgeDirection.DOWN);

  public final int xOffset;
  public final int yOffset;
  public final int zOffset;

  private Offset(ForgeDirection dir) {
    xOffset = dir.offsetX;
    yOffset = dir.offsetY;
    zOffset = dir.offsetZ;
  }

  private Offset(ForgeDirection dir, ForgeDirection yDir) {
    xOffset = dir.offsetX;
    yOffset = yDir.offsetY;
    zOffset = dir.offsetZ;
  }

  private Offset(int xOffset, int yOffset, int zOffset) {
    this.xOffset = xOffset;
    this.yOffset = yOffset;
    this.zOffset = zOffset;
  }

}
