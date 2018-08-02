package crazypants.enderio.zoo.entity.navigate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import crazypants.enderio.zoo.entity.SpawnUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathHeap;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;

public class FlyingPathFinder extends PathFinder {

  private @Nonnull PathHeap path = new PathHeap();
  private @Nonnull PathPoint[] pathOptions = new PathPoint[32];
  private @Nonnull NodeProcessor nodeProcessor;

  public FlyingPathFinder(@Nonnull NodeProcessor nodeProcessorIn) {
    super(nodeProcessorIn);
    this.nodeProcessor = nodeProcessorIn;
  }

  // createEntityPathTo
  @Override
  public Path findPath(@Nonnull IBlockAccess blockaccess, @Nonnull EntityLiving entityFrom, @Nonnull Entity entityTo, float dist) {
    return createEntityPathTo(blockaccess, entityFrom, entityTo.posX, entityTo.getEntityBoundingBox().minY, entityTo.posZ, dist);
  }

  // createEntityPathTo
  @Override
  public Path findPath(@Nonnull IBlockAccess blockaccess, @Nonnull EntityLiving entityIn, @Nonnull BlockPos targetPos, float dist) {
    return createEntityPathTo(blockaccess, entityIn, targetPos.getX() + 0.5F, targetPos.getY() + 0.5F, targetPos.getZ() + 0.5F, dist);
  }

  private Path createEntityPathTo(@Nonnull IBlockAccess blockaccess, @Nonnull Entity ent, double x, double y, double z, float distance) {

    path.clearPath();

    if (!(ent instanceof EntityLiving)) {
      return null;
    }
    EntityLiving entityIn = (EntityLiving) ent;
    nodeProcessor.init(blockaccess, entityIn);

    PathPoint startPoint = nodeProcessor.getStart();
    PathPoint endPoint = nodeProcessor.getPathPointToCoords(x, y, z);

    Vec3d targ = new Vec3d(x, y, z);
    Vec3d ePos = entityIn.getPositionVector();
    double yDelta = targ.y - ePos.y;

    double horizDist = new Vec3d(x, 0, z).distanceTo(new Vec3d(ePos.x, 0, ePos.z));

    int climbY = 0;
    if (horizDist > 4 && entityIn.onGround) {
      climbY = 1 * MathHelper.clamp((int) (horizDist / 8), 1, 3);
      if (yDelta >= 1) {
        climbY += yDelta;
      } else {
        climbY++;
      }
    }

    if (climbY == 0) {
      return createDefault(blockaccess, entityIn, distance, x, y, z);
    }

    List<PathPoint> resPoints = new ArrayList<PathPoint>();
    // climb, then descend
    double climbDistance = Math.min(horizDist / 2.0, climbY);

    Vec3d horizDirVec = new Vec3d(targ.x, 0, targ.z);
    horizDirVec = horizDirVec.subtract(new Vec3d(ePos.x, 0, ePos.z));
    horizDirVec = horizDirVec.normalize();
    Vec3d offset = new Vec3d(horizDirVec.x * climbDistance, climbY, horizDirVec.z * climbDistance);

    PathPoint climbPoint = new PathPoint(rnd(startPoint.x + offset.x), rnd(startPoint.y + offset.y), rnd(startPoint.z + offset.z));
    if (!SpawnUtil.isSpaceAvailableForSpawn(entityIn.world, entityIn, false)) {
      return createDefault(blockaccess, entityIn, distance, x, y, z);
    }

    PathPoint[] points = addToPath(entityIn, startPoint, climbPoint, distance);
    nodeProcessor.postProcess();

    if (points == null) { // failed to climb so go default
      return createDefault(blockaccess, entityIn, distance, x, y, z);
    }
    resPoints.addAll(Arrays.asList(points));

    // then path from the climb point to destination
    path.clearPath();
    nodeProcessor.init(blockaccess, entityIn);
    // climbPoint.index = -1;
    climbPoint = new PathPoint(climbPoint.x, climbPoint.y, climbPoint.z);
    points = addToPath(entityIn, climbPoint, endPoint, distance);
    nodeProcessor.postProcess();

    if (points == null) {
      return createDefault(blockaccess, entityIn, distance, x, y, z);
    }
    resPoints.addAll(Arrays.asList(points));
    if (resPoints.isEmpty()) {
      return null;
    }
    return new Path(resPoints.toArray(new PathPoint[resPoints.size()]));

  }

  private PathPoint[] addToPath(@Nonnull Entity entityIn, @Nonnull PathPoint pathpointStart, @Nonnull PathPoint pathpointEnd, float maxDistance) {
    // set start point values

    // pathpointStart.totalPathDistance = 0.0F;
    // pathpointStart.distanceToNext = pathpointStart.distanceToSquared(pathpointEnd);
    // pathpointStart.distanceToTarget = pathpointStart.distanceToNext;
    // pathpointStart.index = -1;
    PPUtil.setTotalPathDistance(pathpointStart, 0f);
    float dist = pathpointStart.distanceToSquared(pathpointEnd);
    PPUtil.setDistanceToNext(pathpointStart, dist);
    PPUtil.setDistanceToTarget(pathpointStart, dist);
    PPUtil.setIndex(pathpointStart, -1);

    // clear and add out start point to the path
    path.clearPath();
    path.addPoint(pathpointStart);
    PathPoint curPoint = pathpointStart;

    // while still points in the path
    while (!path.isPathEmpty()) {

      PathPoint dequeued = path.dequeue();

      // we are at the end
      if (dequeued.equals(pathpointEnd)) {
        return createEntityPath(pathpointStart, pathpointEnd);
      }

      // if the dequed point is closer to the ned that our current one, make it
      // the current point
      if (dequeued.distanceToSquared(pathpointEnd) < curPoint.distanceToSquared(pathpointEnd)) {
        curPoint = dequeued;
      }
      dequeued.visited = true;

      // find options for the next point in the path

      int numPathOptions = nodeProcessor.findPathOptions(pathOptions, dequeued, pathpointEnd, maxDistance);
      // int numPathOptions = nodeProcessor.findPathOptions(pathOptions, entityIn, dequeued, pathpointEnd, maxDistance);

      for (int j = 0; j < numPathOptions; ++j) {
        PathPoint cadidatePoint = pathOptions[j];
        if (cadidatePoint != null) {
          float newTotalDistance = PPUtil.getTotalPathDistance(dequeued) + dequeued.distanceToSquared(cadidatePoint);
          if (newTotalDistance < maxDistance * 2.0F && (!cadidatePoint.isAssigned() || newTotalDistance < PPUtil.getTotalPathDistance(cadidatePoint))) {
            // cadidatePoint.previous = dequeued;
            PPUtil.setPrevious(cadidatePoint, dequeued);
            // cadidatePoint.totalPathDistance = newTotalDistance;
            PPUtil.setTotalPathDistance(cadidatePoint, newTotalDistance);
            // cadidatePoint.distanceToNext = cadidatePoint.distanceToSquared(pathpointEnd);
            PPUtil.setDistanceToNext(cadidatePoint, cadidatePoint.distanceToSquared(pathpointEnd));
            if (cadidatePoint.isAssigned()) {
              // path.changeDistance(cadidatePoint, cadidatePoint.totalPathDistance + cadidatePoint.distanceToNext);
              path.changeDistance(cadidatePoint, PPUtil.getTotalPathDistance(cadidatePoint) + PPUtil.getDistanceToNext(cadidatePoint));
            } else {
              PPUtil.setDistanceToTarget(cadidatePoint, PPUtil.getTotalPathDistance(cadidatePoint) + PPUtil.getDistanceToNext(cadidatePoint));
              path.addPoint(cadidatePoint);
            }
          }
        }
      }

      // PathPoint cadidatePoint = new PathPoint(pathpointEnd.xCoord, pathpointEnd.yCoord, pathpointEnd.zCoord);
      // float newTotalDistance = dequeued.totalPathDistance + dequeued.distanceToSquared(cadidatePoint);
      // cadidatePoint.previous = dequeued;
      // cadidatePoint.totalPathDistance = newTotalDistance;
      // cadidatePoint.distanceToNext = cadidatePoint.distanceToSquared(pathpointEnd);
      // if (cadidatePoint.isAssigned()) {
      // path.changeDistance(cadidatePoint, cadidatePoint.totalPathDistance + cadidatePoint.distanceToNext);
      // } else {
      // cadidatePoint.distanceToTarget = cadidatePoint.totalPathDistance + cadidatePoint.distanceToNext;
      // path.addPoint(cadidatePoint);
      // }
    }

    if (curPoint == pathpointStart) {
      return null;
    } else {
      return createEntityPath(pathpointStart, curPoint);
    }
  }

  private int rnd(double d) {
    return (int) Math.round(d);
  }

  private Path createDefault(@Nonnull IBlockAccess blockaccess, @Nonnull EntityLiving entityIn, float distance, double x, double y, double z) {
    this.path.clearPath();
    this.nodeProcessor.init(blockaccess, entityIn);

    // PathPoint pathpoint1 =nodeProcessor.getPathPointToCoords(entityIn, x, y, z);
    PathPoint pathpoint = nodeProcessor.getStart();
    PathPoint pathpoint1 = nodeProcessor.getPathPointToCoords(x, y, z);

    PathPoint[] p = addToPath(entityIn, pathpoint, pathpoint1, distance);
    Path res;
    if (p == null) {
      res = null;
    } else {
      res = new Path(p);
    }
    this.nodeProcessor.postProcess();
    return res;
  }

  private static PathPoint[] createEntityPath(PathPoint start, PathPoint end) {
    int i = 1;

    for (PathPoint pathpoint = end; pathpoint != null && PPUtil.getPrevious(pathpoint) != null; pathpoint = PPUtil.getPrevious(pathpoint)) {
      ++i;
    }

    PathPoint[] apathpoint = new PathPoint[i];
    PathPoint pathpoint1 = end;
    --i;
    for (apathpoint[i] = end; pathpoint1 != null && PPUtil.getPrevious(pathpoint1) != null; apathpoint[i] = pathpoint1) {
      pathpoint1 = PPUtil.getPrevious(pathpoint1);
      --i;
    }

    return apathpoint;
  }
}
