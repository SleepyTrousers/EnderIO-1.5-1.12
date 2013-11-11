package crazypants.enderio.conduit;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import crazypants.enderio.conduit.geom.CollidableComponent;

public class RaytraceResult {

  public static RaytraceResult getClosestHit(Vec3 origin, Collection<RaytraceResult> candidates) {
    double minLengthSquared = Double.POSITIVE_INFINITY;
    RaytraceResult closest = null;

    for (RaytraceResult candidate : candidates) {
      MovingObjectPosition hit = candidate.movingObjectPosition;
      if(hit != null) {
        double lengthSquared = hit.hitVec.squareDistanceTo(origin);
        if(lengthSquared < minLengthSquared) {
          minLengthSquared = lengthSquared;
          closest = candidate;
        }
      }
    }
    return closest;
  }

  public static void sort(final Vec3 origin, List<RaytraceResult> toSort) {
    if(origin == null || toSort == null) {
      Collections.sort(toSort, new Comparator<RaytraceResult>() {

        @Override
        public int compare(RaytraceResult o1, RaytraceResult o2) {
          return Double.compare(o1.getDistanceTo(origin), o1.getDistanceTo(origin));
        }
      });
    }
  }

  public final CollidableComponent component;
  public final MovingObjectPosition movingObjectPosition;

  public RaytraceResult(CollidableComponent component, MovingObjectPosition movingObjectPosition) {
    this.component = component;
    this.movingObjectPosition = movingObjectPosition;
  }

  public double getDistanceTo(Vec3 origin) {
    if(movingObjectPosition == null || origin == null) {
      return Double.MAX_VALUE;
    }
    return movingObjectPosition.hitVec.squareDistanceTo(origin);
  }

}
