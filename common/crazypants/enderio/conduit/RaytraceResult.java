package crazypants.enderio.conduit;

import java.util.Collection;

import crazypants.enderio.conduit.geom.CollidableComponent;


import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class RaytraceResult {

  public static RaytraceResult getClosestHit(Vec3 origin, Collection<RaytraceResult> candidates) {
    double minLengthSquared = Double.POSITIVE_INFINITY;
    RaytraceResult closest = null;

    for (RaytraceResult candidate : candidates) {
      MovingObjectPosition hit = candidate.movingObjectPosition;
      if (hit != null) {
        double lengthSquared = hit.hitVec.squareDistanceTo(origin);
        if (lengthSquared < minLengthSquared) {
          minLengthSquared = lengthSquared;
          closest = candidate;
        }
      }
    }
    return closest;
  }

  public final CollidableComponent component;
  public final MovingObjectPosition movingObjectPosition;

  public RaytraceResult(CollidableComponent component, MovingObjectPosition movingObjectPosition) {
    this.component = component;
    this.movingObjectPosition = movingObjectPosition;
  }

}
