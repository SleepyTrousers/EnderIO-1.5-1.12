package crazypants.enderio.tool;

import static net.minecraftforge.common.util.ForgeDirection.EAST;
import static net.minecraftforge.common.util.ForgeDirection.SOUTH;
import static net.minecraftforge.common.util.ForgeDirection.WEST;
import static net.minecraftforge.common.util.ForgeDirection.NORTH;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.util.BlockCoord;

public class SpiralPattern {

  private final BlockCoord center;
  private final int rings;

  private BlockCoord current;
  private int ring = 0;
  ForgeDirection dir = EAST;

  public SpiralPattern(int x, int z, int rings) {
    this(new BlockCoord(x, 0, z), rings);
  }

  public SpiralPattern(BlockCoord center, int rings) {
    this.center = center;
    this.rings = rings;
    current = center;
  }

  public BlockCoord next() {
    if (ring > rings) {
      return null;
    }
    switch (dir) {
    case SOUTH:
      if (current.z == center.z + ring) {
        dir = WEST;
      }
      break;
    case WEST:
      if (current.x == center.x - ring) {
        dir = NORTH;
      }
      break;
    case NORTH:
      if (current.z == center.z - ring) {
        dir = EAST;
      }
      break;
    case EAST:
      if (current.x == center.x + ring) {
        if (++ring > rings) {
          return null;
        }
        dir = SOUTH;
        current = current.getLocation(EAST);
        return current;
      }
      break;
    default:
      break;
    }
    current = current.getLocation(dir);
    return current;
  }

}
