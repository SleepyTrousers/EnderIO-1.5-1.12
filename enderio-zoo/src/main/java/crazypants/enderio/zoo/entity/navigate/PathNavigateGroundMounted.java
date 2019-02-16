package crazypants.enderio.zoo.entity.navigate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PathNavigateGroundMounted extends PathNavigateGround {

  public PathNavigateGroundMounted(EntityLiving entitylivingIn, World worldIn) {
    super(entitylivingIn, worldIn);
  }

  /**
   * Based off of PathNavigateGround.pathFollow() with 1 small modification mentioned below
   */
  @Override
  protected void pathFollow() {
    Vec3d vec3d = this.getEntityPosition();
    int i = this.currentPath.getCurrentPathLength();

    for (int j = this.currentPath.getCurrentPathIndex(); j < this.currentPath.getCurrentPathLength(); ++j) {
      if ((double) this.currentPath.getPathPointFromIndex(j).y != Math.floor(vec3d.y)) {
        i = j;
        break;
      }
    }

    this.maxDistanceToWaypoint = this.entity.width > 0.75F ? entity.width / 2.0F : 0.75F - entity.width / 2.0F;
    Vec3d vec3d1 = this.currentPath.getCurrentPos();

    /**
     * Adjust the maximum height difference to allow the mob riding the other mob to step down a block correctly
     * - only when detected it is riding the mob
     * - only for pathing downwards as it can interfere with pathing upwards
     */
    double maxHeightDifference = 1D;
    if (this.entity.isRiding() && this.entity.posY - vec3d1.y > 0) {
      maxHeightDifference = 2D;
    }
    if (MathHelper.abs((float) (this.entity.posX - (vec3d1.x + 0.5D))) < this.maxDistanceToWaypoint && MathHelper.abs((float) (this.entity.posZ - (vec3d1.z + 0.5D))) < this.maxDistanceToWaypoint && Math.abs(this.entity.posY - vec3d1.y) < maxHeightDifference) {
      this.currentPath.setCurrentPathIndex(this.currentPath.getCurrentPathIndex() + 1);
    }

    int k = MathHelper.ceil(this.entity.width);
    int l = MathHelper.ceil(this.entity.height);
    int i1 = k;

    for (int j1 = i - 1; j1 >= this.currentPath.getCurrentPathIndex(); --j1) {
      if (this.isDirectPathBetweenPoints(vec3d, this.currentPath.getVectorFromIndex(this.entity, j1), k, l, i1)) {
        this.currentPath.setCurrentPathIndex(j1);
        break;
      }
    }

    this.checkForStuck(vec3d);
  }
}
