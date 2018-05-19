package crazypants.enderio.zoo.entity.navigate;

import java.lang.reflect.Field;

import net.minecraft.pathfinding.PathPoint;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class PPUtil {

  private static final Field PREV;
  private static final Field TOT_DIST;
  private static final Field DIST_TO_NEXT;
  private static final Field DIST_TO_TARG;
  private static final Field INDEX;

  static {
    PREV = ReflectionHelper.findField(PathPoint.class, "previous", "field_75841_h");
    TOT_DIST = ReflectionHelper.findField(PathPoint.class, "totalPathDistance", "field_75836_e");
    DIST_TO_NEXT = ReflectionHelper.findField(PathPoint.class, "distanceToNext", "field_75833_f");
    DIST_TO_TARG = ReflectionHelper.findField(PathPoint.class, "distanceToTarget", "field_75834_g");
    INDEX = ReflectionHelper.findField(PathPoint.class, "index", "field_75835_d");
  }

  public static PathPoint getPrevious(PathPoint pp) {
    try {
      return (PathPoint) PREV.get(pp);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  public static void setPrevious(PathPoint pp, PathPoint prev) {
    try {
      PREV.set(pp, prev);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static float getTotalPathDistance(PathPoint pp) {
    try {
      return TOT_DIST.getFloat(pp);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  public static void setTotalPathDistance(PathPoint pp, float dist) {
    try {
      TOT_DIST.setFloat(pp, dist);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  public static float getDistanceToNext(PathPoint pp) {
    try {
      return DIST_TO_NEXT.getFloat(pp);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void setDistanceToNext(PathPoint pp, float dist) {
    try {
      DIST_TO_NEXT.setFloat(pp, dist);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void setDistanceToTarget(PathPoint pp, float dist) {
    try {
      DIST_TO_TARG.setFloat(pp, dist);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void setIndex(PathPoint pp, int i) {
    try {
      INDEX.setInt(pp, i);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
