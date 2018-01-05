package crazypants.enderio.base.conduit.geom;

import com.enderio.core.client.render.BoundingBox;

import crazypants.enderio.base.conduit.IConduit;
import net.minecraft.util.EnumFacing;

public class CollidableComponent {

  public final Class<? extends IConduit> conduitType;
  public final BoundingBox bound;
  public final EnumFacing dir;
  public final Object data;

  public CollidableComponent(Class<? extends IConduit> conduitType, BoundingBox bound, EnumFacing id, Object data) {
    this.conduitType = conduitType;
    this.bound = bound;
    this.dir = id;
    this.data = data;
  }

  @Override
  public String toString() {
    return "CollidableComponent [conduitType=" + conduitType + ", bound=" + bound + ", id=" + dir + "]";
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof CollidableComponent) {
      CollidableComponent other = (CollidableComponent) obj;
      return conduitType == other.conduitType && bound.equals(((CollidableComponent) obj).bound) && dir == other.dir;
    }
    return false;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((bound == null) ? 0 : bound.hashCode());
    result = prime * result + ((conduitType == null) ? 0 : conduitType.getName().hashCode());
    result = prime * result + ((dir == null) ? 0 : dir.hashCode());
    return result;
  }
}
