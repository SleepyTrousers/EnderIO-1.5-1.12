package crazypants.enderio.base.conduit.geom;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import crazypants.enderio.base.conduit.IConduit;
import net.minecraft.util.EnumFacing;

public class Offsets {

  /**
   * Registers a set of offsets for a new conduit. (API method)
   * 
   * @param type
   *          The class of the conduit
   * @param none
   *          The offset for the node
   * @param x
   *          The offset for conduit arms on the X axis
   * @param y
   *          The offset for conduit arms on the Y axis
   * @param z
   *          The offset for conduit arms on the Z axis
   * @return true if the offset was registered. false if the conduit already is registered of if one of the axis are already in use.
   */
  public static boolean registerOffsets(Class<? extends IConduit> type, Offset none, Offset x, Offset y, Offset z) {
    OffsetKey keyNone = key(type, Axis.NONE);
    OffsetKey keyX = key(type, Axis.X);
    OffsetKey keyY = key(type, Axis.Y);
    OffsetKey keyZ = key(type, Axis.Z);
    if (OFFSETS.containsKey(keyNone) || OFFSETS.containsKey(keyX) || OFFSETS.containsKey(keyY) || OFFSETS.containsKey(keyZ)) {
      return false;
    }
    for (Entry<OffsetKey, Offset> elem : OFFSETS.entrySet()) {
      if (elem.getKey().axis == Axis.NONE && elem.getValue() == none) {
        return false;
      }
      if (elem.getKey().axis == Axis.X && elem.getValue() == x) {
        return false;
      }
      if (elem.getKey().axis == Axis.Y && elem.getValue() == y) {
        return false;
      }
      if (elem.getKey().axis == Axis.Z && elem.getValue() == z) {
        return false;
      }
    }
    OFFSETS.put(keyNone, none);
    OFFSETS.put(keyX, x);
    OFFSETS.put(keyY, y);
    OFFSETS.put(keyZ, z);
    return true;
  }

  private static Map<OffsetKey, Offset> OFFSETS = new HashMap<OffsetKey, Offset>();

  // new ConduitRegistry.ConduitInfo(getBaseConduitType(), Offset.NORTH_UP, Offset.NORTH_UP, Offset.NORTH_WEST, Offset.WEST_UP);

  // new ConduitRegistry.ConduitInfo(getBaseConduitType(), Offset.SOUTH_DOWN, Offset.SOUTH_DOWN, Offset.SOUTH_EAST, Offset.EAST_DOWN);

  public static Offset get(Class<? extends IConduit> type, EnumFacing dir) {
    Offset res = OFFSETS.get(key(type, getAxisForDir(dir)));
    if (res == null) {
      res = Offset.NONE;
    }
    return res;
  }

  public static OffsetKey key(Class<? extends IConduit> type, Axis axis) {
    return new OffsetKey(type, axis);
  }

  public static Axis getAxisForDir(EnumFacing dir) {
    if (dir == null) {
      return Axis.NONE;
    }
    if (dir == EnumFacing.EAST || dir == EnumFacing.WEST) {
      return Axis.X;
    }
    if (dir == EnumFacing.UP || dir == EnumFacing.DOWN) {
      return Axis.Y;
    }
    if (dir == EnumFacing.NORTH || dir == EnumFacing.SOUTH) {
      return Axis.Z;
    }
    return Axis.NONE;
  }

  public static enum Axis {
    NONE,
    X,
    Y,
    Z
  }

  public static class OffsetKey {

    String typeName;
    Axis axis;

    private OffsetKey(Class<? extends IConduit> type, Axis axis) {
      this.typeName = type.getCanonicalName();
      this.axis = axis;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((axis == null) ? 0 : axis.hashCode());
      result = prime * result + ((typeName == null) ? 0 : typeName.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      OffsetKey other = (OffsetKey) obj;
      if (axis != other.axis) {
        return false;
      }
      if (typeName == null) {
        if (other.typeName != null) {
          return false;
        }
      } else if (!typeName.equals(other.typeName)) {
        return false;
      }
      return true;
    }

    @Override
    public String toString() {
      return "OffsetKey [typeName=" + typeName + ", axis=" + axis + "]";
    }

  }

}
