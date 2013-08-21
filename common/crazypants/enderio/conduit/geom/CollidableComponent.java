package crazypants.enderio.conduit.geom;

import net.minecraftforge.common.ForgeDirection;
import crazypants.enderio.conduit.IConduit;
import crazypants.render.BoundingBox;

public class CollidableComponent {

  public final Class<? extends IConduit> conduitType;
  public final BoundingBox bound;
  public final ForgeDirection dir;
  public final Object data;

  public CollidableComponent(Class<? extends IConduit> conduitType, BoundingBox bound, ForgeDirection id, Object data) {
    this.conduitType = conduitType;
    this.bound = bound;
    this.dir = id;
    this.data = data;
  }

  @Override
  public String toString() {
    return "CollidableComponent [conduitType=" + conduitType + ", bound=" + bound + ", id=" + dir + "]";
  }

}
