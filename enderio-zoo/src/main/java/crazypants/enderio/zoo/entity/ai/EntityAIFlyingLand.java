package crazypants.enderio.zoo.entity.ai;

import crazypants.enderio.zoo.entity.EntityUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;

public class EntityAIFlyingLand extends EntityAIBase {

  private EntityCreature entity;
  protected double speed;
  private double targetX;
  private double targetY;
  private double targetZ;

  private int onGroundCount = 0;
  private int defSearchRange = 3;
  private int maxSearchRange = 16;
  private int searchRange = 4;
  private int searchAttempts = 10;

  public EntityAIFlyingLand(EntityCreature creature, double speedIn) {
    entity = creature;
    speed = speedIn;
    setMutexBits(1);
  }

  @Override
  public boolean shouldExecute() {

    if (entity.onGround || !entity.getNavigator().noPath()) {
      return false;
    }

    BlockPos target = null;

    BlockPos ep = entity.getPosition();
    // Land just bellow us if we can
    BlockPos blockLocationResult = EntityUtil.findClearLandingSurface(entity, ep.getX(), ep.getZ(), 1, ep.getY());
    if (blockLocationResult != null) {
      int distFromGround = ep.getY() - blockLocationResult.getY();
      if (distFromGround < 2) {
        target = blockLocationResult;
      }
    }
    // otherwise randomly search for somewhere to land
    if (target == null) {
      target = EntityUtil.findRandomLandingSurface(entity, searchRange, 1, ep.getY() + 1, searchAttempts);
    }

    if (target != null) {
      int distFromGround = ep.getY() - target.getY();
      if (distFromGround > 12) {        
        target = EntityUtil.findRandomClearArea(entity, searchRange, ep.getY() - 10, ep.getY() - 5, searchAttempts);
      }
    }

    if (target == null) {
      // failed so increase the seach range for next time
      searchRange = Math.min(searchRange + 1, maxSearchRange);
      return false;
    }

    searchRange = defSearchRange;
    targetX = target.getX() + 0.5;
    targetY = target.getY();
    targetZ = target.getZ() + 0.5;

    return true;
  }

  @Override
  public void startExecuting() {
    onGroundCount = 0;
    if (!entity.getNavigator().tryMoveToXYZ(targetX, targetY, targetZ, speed)) {
//      System.out.println("EntityAIFlyingLand.startExecuting: No path to target");
    }
  }

  @Override
  public boolean shouldContinueExecuting() {

    if (entity.onGround) {
      onGroundCount++;
      if (onGroundCount >= 40) {
        // If we have been on the ground for a couple of seconds
        // time to chill no matter what
        entity.getNavigator().clearPathEntity();
        return false;
      }

      // Stop if we are on the ground in the middle of a block
      double fx = entity.posX - Math.floor(entity.posX);
      double fz = entity.posX - Math.floor(entity.posX);
      if (fx > 0.4 && fx < 0.6 && fz > 0.4 && fz < 0.6) {
        BlockPos bellow = entity.getPosition().down();
        IBlockState bs = entity.getEntityWorld().getBlockState(bellow);
        if (!bs.getBlock().isAir(bs, entity.getEntityWorld(), bellow)) {
          entity.getNavigator().clearPathEntity();
          return false;
        }
      }
    }

    boolean isStillNavigating = !entity.getNavigator().noPath();
    if (!isStillNavigating) {
      entity.onGround = EntityUtil.isOnGround(entity);
      entity.isAirBorne = !entity.onGround;
      if (!entity.onGround) { // gravity
        entity.setPosition(entity.posX, entity.posY - 0.01, entity.posZ);
      }
    }
    return isStillNavigating;
  }

}
