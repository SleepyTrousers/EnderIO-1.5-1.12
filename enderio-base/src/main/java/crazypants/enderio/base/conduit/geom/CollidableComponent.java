package crazypants.enderio.base.conduit.geom;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.enderio.core.client.render.BoundingBox;

import crazypants.enderio.base.conduit.IConduit;
import net.minecraft.util.EnumFacing;

@ParametersAreNonnullByDefault
public class CollidableComponent {

  public final @Nullable Class<? extends IConduit> conduitType;
  public final BoundingBox bound;
  public final EnumFacing dir;
  public final @Nullable Object data;

  public CollidableComponent(@Nullable Class<? extends IConduit> conduitType, BoundingBox bound, EnumFacing id, @Nullable Object data) {
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
  public boolean equals(@Nullable Object obj) {
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
    result = prime * result + bound.hashCode();
    final Class<?> cls = conduitType;
    result = prime * result + (cls == null ? 0 : cls.getName().hashCode());
    result = prime * result + dir.hashCode();
    return result;
  }
}
