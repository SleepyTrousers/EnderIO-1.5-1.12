package crazypants.enderio.conduit.geom;

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.common.ForgeDirection;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.liquid.ILiquidConduit;
import crazypants.enderio.conduit.me.IMeConduit;
import crazypants.enderio.conduit.power.IPowerConduit;
import crazypants.enderio.conduit.redstone.IRedstoneConduit;

public class Offsets {

  private static Map<OffsetKey, Offset> OFFSETS = new HashMap<OffsetKey, Offset>();

  static {
    OFFSETS.put(key(IRedstoneConduit.class, Axis.NONE), Offset.UP);
    OFFSETS.put(key(IRedstoneConduit.class, Axis.X), Offset.UP);
    OFFSETS.put(key(IRedstoneConduit.class, Axis.Y), Offset.NORTH);
    OFFSETS.put(key(IRedstoneConduit.class, Axis.Z), Offset.UP);

    OFFSETS.put(key(IPowerConduit.class, Axis.NONE), Offset.DOWN);
    OFFSETS.put(key(IPowerConduit.class, Axis.X), Offset.DOWN);
    OFFSETS.put(key(IPowerConduit.class, Axis.Y), Offset.SOUTH);
    OFFSETS.put(key(IPowerConduit.class, Axis.Z), Offset.DOWN);

    OFFSETS.put(key(ILiquidConduit.class, Axis.NONE), Offset.WEST);
    OFFSETS.put(key(ILiquidConduit.class, Axis.X), Offset.NORTH);
    OFFSETS.put(key(ILiquidConduit.class, Axis.Y), Offset.WEST);
    OFFSETS.put(key(ILiquidConduit.class, Axis.Z), Offset.WEST);

    OFFSETS.put(key(IItemConduit.class, Axis.NONE), Offset.EAST);
    OFFSETS.put(key(IItemConduit.class, Axis.X), Offset.SOUTH);
    OFFSETS.put(key(IItemConduit.class, Axis.Y), Offset.EAST);
    OFFSETS.put(key(IItemConduit.class, Axis.Z), Offset.EAST);

    OFFSETS.put(key(IMeConduit.class, Axis.NONE), Offset.NONE);
    OFFSETS.put(key(IMeConduit.class, Axis.X), Offset.NONE);
    OFFSETS.put(key(IMeConduit.class, Axis.Y), Offset.NONE);
    OFFSETS.put(key(IMeConduit.class, Axis.Z), Offset.NONE);
  }

  public static Offset get(Class<? extends IConduit> type, ForgeDirection dir) {
    Offset res = OFFSETS.get(key(type, getAxisForDir(dir)));
    if(res == null) {
      res = Offset.NONE;
    }
    return res;
  }

  public static OffsetKey key(Class<? extends IConduit> type, Axis axis) {
    return new OffsetKey(type, axis);
  }

  public static Axis getAxisForDir(ForgeDirection dir) {
    if(dir == ForgeDirection.UNKNOWN) {
      return Axis.NONE;
    }
    if(dir == ForgeDirection.EAST || dir == ForgeDirection.WEST) {
      return Axis.X;
    }
    if(dir == ForgeDirection.UP || dir == ForgeDirection.DOWN) {
      return Axis.Y;
    }
    if(dir == ForgeDirection.NORTH || dir == ForgeDirection.SOUTH) {
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
      if(this == obj) {
        return true;
      }
      if(obj == null) {
        return false;
      }
      if(getClass() != obj.getClass()) {
        return false;
      }
      OffsetKey other = (OffsetKey) obj;
      if(axis != other.axis) {
        return false;
      }
      if(typeName == null) {
        if(other.typeName != null) {
          return false;
        }
      } else if(!typeName.equals(other.typeName)) {
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
