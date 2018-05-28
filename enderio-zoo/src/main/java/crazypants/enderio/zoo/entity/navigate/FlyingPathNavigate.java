package crazypants.enderio.zoo.entity.navigate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FlyingPathNavigate extends PathNavigateGround {

  private int totalTicks;
  private int ticksAtLastPos;
  private @Nonnull Vec3d lastPosCheck = new Vec3d(0.0D, 0.0D, 0.0D);

  private boolean forceFlying = false;

  public FlyingPathNavigate(EntityLiving entitylivingIn, World worldIn) {
    super(entitylivingIn, worldIn);
  }

  public boolean isForceFlying() {
    return forceFlying && !noPath();
  }

  public void setForceFlying(boolean forceFlying) {
    this.forceFlying = forceFlying;
  }

  @Override
  protected @Nonnull PathFinder getPathFinder() {
    nodeProcessor = new FlyNodeProcessor();
    return new FlyingPathFinder(nodeProcessor);
  }

  @Override
  protected boolean canNavigate() {
    return true;
  }

  @Override
  protected @Nonnull Vec3d getEntityPosition() {
    int y = (int) (entity.getEntityBoundingBox().minY + 0.5D);
    return new Vec3d(entity.posX, y, entity.posZ);
  }

  public boolean tryFlyToXYZ(double x, double y, double z, double speedIn) {
    Path pathentity = getPathToPos(new BlockPos((double) MathHelper.floor(x), (double) ((int) y), (double) MathHelper.floor(z)));
    return setPath(pathentity, speedIn, true);
  }

  public boolean tryFlyToPos(double x, double y, double z, double speedIn) {
    Path pathentity = getPathToXYZ(x, y, z);
    return setPath(pathentity, speedIn, true);
  }

  public boolean tryFlyToEntityLiving(@Nonnull Entity entityIn, double speedIn) {
    Path pathentity = NullHelper.untrust(getPathToEntityLiving(entityIn));
    return pathentity != null ? setPath(pathentity, speedIn, true) : false;
  }

  public boolean setPath(@Nullable Path path, double speed, boolean forceFlying) {
    if (super.setPath(path, speed)) {
      // String str = "FlyingPathNavigate.setPath:";
      // for (int i = 0; i < path.getCurrentPathLength(); i++) {
      // PathPoint pp = path.getPathPointFromIndex(i);
      // str += " [" + pp + "]";
      // }
      // Log.info(str);
      ticksAtLastPos = totalTicks;
      lastPosCheck = getEntityPosition();
      this.forceFlying = forceFlying;
      return true;
    }
    return false;
  }

  @Override
  public boolean setPath(@Nullable Path path, double speed) {
    return setPath(path, speed, false);
  }

  @Override
  public void onUpdateNavigation() {
    ++totalTicks;
    if (!noPath()) { // if we have a path
      // theEntity.onGround = false;
      // theEntity.isAirBorne = true;
      pathFollow(); // follow it
      if (!noPath()) { // if we haven't finished, then set the new move point
        Vec3d targetPos = currentPath.getPosition(entity);
        double y = targetPos.y;
        if (forceFlying) {
          double aboveBlock = y - (int) y;
          if (aboveBlock < 0.10) {
            y = (int) y + 0.10;
          }
        }
        entity.getMoveHelper().setMoveTo(targetPos.x, y, targetPos.z, speed);
      }
    }

  }

  @Override
  protected void pathFollow() {

    Vec3d entPos = getEntityPosition();
    float entWidthSq = entity.width * entity.width;
    if (currentPath.getCurrentPathIndex() == currentPath.getCurrentPathLength() - 1 && entity.onGround) {
      entWidthSq = 0.01f; // we need to be right on top of the last point if on
                          // the ground so we don't hang on ledges
    }

    Vec3d targetPos = currentPath.getVectorFromIndex(entity, currentPath.getCurrentPathIndex());

    double distToCurrTargSq = entPos.squareDistanceTo(targetPos);
    if (distToCurrTargSq < entWidthSq) {
      currentPath.incrementPathIndex();
    }
    // starting six points ahead (or the end point) see if we can go directly
    // there
    int i = 6;
    for (int j = Math.min(currentPath.getCurrentPathIndex() + i, currentPath.getCurrentPathLength() - 1); j > currentPath.getCurrentPathIndex(); --j) {
      targetPos = currentPath.getVectorFromIndex(entity, j);
      if (targetPos.squareDistanceTo(entPos) <= 36.0D && isDirectPathBetweenPoints(entPos, targetPos, 0, 0, 0)) {
        currentPath.setCurrentPathIndex(j);
        break;
      }
    }
    checkForStuck(entPos);
  }

  @Override
  protected boolean isDirectPathBetweenPoints(@Nonnull Vec3d startPos, @Nonnull Vec3d endPos, int sizeX, int sizeY, int sizeZ) {

    Vec3d target = new Vec3d(endPos.x, endPos.y + entity.height * 0.5D, endPos.z);
    if (!isClear(startPos, target)) {
      return false;
    }
    AxisAlignedBB bb = entity.getEntityBoundingBox();
    startPos = new Vec3d(bb.maxX, bb.maxY, bb.maxZ);
    if (!isClear(startPos, target)) {
      return false;
    }
    return true;

  }

  private boolean isClear(@Nonnull Vec3d startPos, @Nonnull Vec3d target) {
    RayTraceResult hit = world.rayTraceBlocks(startPos, target, true, true, false);
    return hit == null || hit.typeOfHit == RayTraceResult.Type.MISS;
  }

  @Override
  protected void checkForStuck(@Nonnull Vec3d positionVec3) {

    if (totalTicks - ticksAtLastPos > 10 && positionVec3.squareDistanceTo(lastPosCheck) < 0.0625) {
      clearPath();
      ticksAtLastPos = totalTicks;
      lastPosCheck = positionVec3;
      return;
    }

    if (totalTicks - ticksAtLastPos > 50) {
      if (positionVec3.squareDistanceTo(lastPosCheck) < 2.25D) {
        clearPath();
      }

      ticksAtLastPos = totalTicks;
      lastPosCheck = positionVec3;
    }
  }

}
