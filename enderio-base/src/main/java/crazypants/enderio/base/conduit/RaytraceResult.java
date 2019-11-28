package crazypants.enderio.base.conduit;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.conduit.geom.CollidableComponent;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class RaytraceResult {

  public static @Nullable RaytraceResult getClosestHit(@Nonnull Vec3d origin, @Nonnull Collection<RaytraceResult> candidates) {
    double minLengthSquared = Double.POSITIVE_INFINITY;
    RaytraceResult closest = null;

    for (RaytraceResult candidate : candidates) {
      RayTraceResult hit = candidate.movingObjectPosition;
      double lengthSquared = hit.hitVec.squareDistanceTo(origin);
      if (lengthSquared < minLengthSquared) {
        minLengthSquared = lengthSquared;
        closest = candidate;
      }
    }
    return closest;
  }

  public static @Nonnull List<RaytraceResult> sort(final @Nonnull Vec3d origin, @Nonnull List<RaytraceResult> toSort) {
    Collections.sort(toSort, new Comparator<RaytraceResult>() {
      @Override
      public int compare(RaytraceResult o1, RaytraceResult o2) {
        return Double.compare(o1.getDistanceTo(origin), o2.getDistanceTo(origin));
      }
    });
    return toSort;
  }

  public final @Nonnull CollidableComponent component;
  public final @Nonnull RayTraceResult movingObjectPosition;

  public RaytraceResult(@Nonnull CollidableComponent component, @Nonnull RayTraceResult movingObjectPosition) {
    this.component = component;
    this.movingObjectPosition = movingObjectPosition;
  }

  public double getDistanceTo(@Nonnull Vec3d origin) {
    return movingObjectPosition.hitVec.squareDistanceTo(origin);
  }

}
