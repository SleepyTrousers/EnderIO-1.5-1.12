package crazypants.enderio.base.conduit.geom;

import javax.annotation.Nonnull;

import crazypants.enderio.base.conduit.IConduit;
import net.minecraft.util.EnumFacing;

public final class GeometryKey {

  public final EnumFacing dir;
  public final @Nonnull Offset offset;
  public final String className;

  public GeometryKey(EnumFacing dir, @Nonnull Offset offset, Class<? extends IConduit> type) {
    this.dir = dir;
    this.offset = offset;
    className = type != null ? type.getName() : null;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((className == null) ? 0 : className.hashCode());
    result = prime * result + ((dir == null) ? 0 : dir.hashCode());
    result = prime * result + offset.hashCode();
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
    GeometryKey other = (GeometryKey) obj;
    if (className == null) {
      if (other.className != null) {
        return false;
      }
    } else if (!className.equals(other.className)) {
      return false;
    }
    if (dir != other.dir) {
      return false;
    }
    if (offset != other.offset) {
      return false;
    }
    return true;
  }

}
