package crazypants.enderio.conduit.redstone;

import crazypants.enderio.conduit.redstone.IRedstoneConduit.SignalColor;
import crazypants.util.BlockCoord;

public class Signal {

  public final int x;
  public final int y;
  public final int z;
  public final int strength;
  public final SignalColor color;

  public Signal(int x, int y, int z, int strength, SignalColor color) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.strength = strength;
    this.color = color;
  }

  public boolean isLocationEquals(BlockCoord loc) {
    return loc.x == x && loc.y == y && loc.z == z;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((color == null) ? 0 : color.hashCode());
    result = prime * result + x;
    result = prime * result + y;
    result = prime * result + z;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Signal other = (Signal) obj;
    if (color != other.color)
      return false;
    if (x != other.x)
      return false;
    if (y != other.y)
      return false;
    if (z != other.z)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Signal [x=" + x + " y=" + y + " z=" + z + " strength=" + strength + " color=" + color + "]";
  }

}
