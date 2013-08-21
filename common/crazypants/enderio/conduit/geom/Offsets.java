package crazypants.enderio.conduit.geom;

import java.util.HashMap;
import java.util.Map;

import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.liquid.ILiquidConduit;
import crazypants.enderio.conduit.power.IPowerConduit;
import crazypants.enderio.conduit.redstone.IRedstoneConduit;

public class Offsets {

  private static Map<OffsetKey, Offset> OFFSETS = new HashMap<OffsetKey, Offset>();

  static {
    OFFSETS.put(key(IRedstoneConduit.class, true, false), Offset.BOTTOM);
    OFFSETS.put(key(IPowerConduit.class, true, false), Offset.TOP);
    OFFSETS.put(key(ILiquidConduit.class, true, false), Offset.NONE);

    OFFSETS.put(key(IRedstoneConduit.class, false, true), Offset.LEFT);
    OFFSETS.put(key(IPowerConduit.class, false, true), Offset.RIGHT);
    OFFSETS.put(key(ILiquidConduit.class, false, true), Offset.NONE);

    OFFSETS.put(key(IRedstoneConduit.class, true, true), Offset.BL);
    OFFSETS.put(key(IPowerConduit.class, true, true), Offset.TR);
    OFFSETS.put(key(ILiquidConduit.class, true, true), Offset.NONE);
  }

  public static Offset get(Class<? extends IConduit> type, boolean horizontal, boolean vertical) {
    return OFFSETS.get(key(type, horizontal, vertical));
  }

  public static OffsetKey key(Class<? extends IConduit> type, boolean horizontal, boolean vertical) {
    return new OffsetKey(type, horizontal, vertical);
  }

  public static class OffsetKey {

    String typeName;
    boolean horizontal;
    boolean vertical;

    private OffsetKey(Class<? extends IConduit> type, boolean horizontal, boolean vertical) {
      this.typeName = type.getCanonicalName();
      this.horizontal = horizontal;
      this.vertical = vertical;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (horizontal ? 1231 : 1237);
      result = prime * result + ((typeName == null) ? 0 : typeName.hashCode());
      result = prime * result + (vertical ? 1231 : 1237);
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
      OffsetKey other = (OffsetKey) obj;
      if (horizontal != other.horizontal)
        return false;
      if (typeName == null) {
        if (other.typeName != null)
          return false;
      } else if (!typeName.equals(other.typeName))
        return false;
      if (vertical != other.vertical)
        return false;
      return true;
    }

  }

}
