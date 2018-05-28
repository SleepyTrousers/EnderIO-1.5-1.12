package crazypants.enderio.zoo.entity.navigate;

import javax.annotation.Nonnull;

import net.minecraft.pathfinding.PathPoint;

public class PPUtil {

  public static PathPoint getPrevious(@Nonnull PathPoint pp) {
    return pp.previous;
  }

  public static void setPrevious(@Nonnull PathPoint pp, @Nonnull PathPoint prev) {
    pp.previous = prev;
  }

  public static float getTotalPathDistance(@Nonnull PathPoint pp) {
    return pp.totalPathDistance;
  }

  public static void setTotalPathDistance(@Nonnull PathPoint pp, float dist) {
    pp.totalPathDistance = dist;
  }

  public static float getDistanceToNext(@Nonnull PathPoint pp) {
    return pp.distanceToNext;
  }

  public static void setDistanceToNext(@Nonnull PathPoint pp, float dist) {
    pp.distanceToNext = dist;
  }

  public static void setDistanceToTarget(@Nonnull PathPoint pp, float dist) {
    pp.distanceToTarget = dist;
  }

  public static void setIndex(@Nonnull PathPoint pp, int i) {
    pp.index = i;
  }

}
