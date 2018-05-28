package crazypants.enderio.zoo.entity.ai;

import javax.annotation.Nonnull;

import crazypants.enderio.zoo.entity.IOwnable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIFollowOwner extends EntityAIBase {

  /** The child that is following its parent. */
  @Nonnull
  IOwnable<? extends EntityCreature, ? extends EntityLivingBase> owned;
  double followSpeed;
  private int pathingTimer;

  private double minDistanceSq;
  private double maxDistanceSq;

  public EntityAIFollowOwner(@Nonnull IOwnable<? extends EntityCreature, ? extends EntityLivingBase> owned, double minDist, double maxDist,
      double followSpeed) {
    this.owned = owned;
    minDistanceSq = minDist * minDist;
    maxDistanceSq = maxDist * maxDist;
    this.followSpeed = followSpeed;
  }

  @Override
  public boolean shouldExecute() {
    if (owned.getOwner() == null) {
      return false;
    }
    return getDistanceSqFromOwner() > maxDistanceSq;
  }

  @Override
  public boolean shouldContinueExecuting() {
    EntityLivingBase owner = owned.getOwner();
    if (owner == null || !owner.isEntityAlive()) {
      return false;
    }
    return !owned.asEntity().getNavigator().noPath();
  }

  public boolean isWithinTargetDistanceFromOwner() {
    if (owned.getOwner() == null) {
      return true;
    }
    double distance = getDistanceSqFromOwner();
    return distance >= minDistanceSq && distance <= maxDistanceSq;
  }

  private double getDistanceSqFromOwner() {
    final EntityLivingBase owner = owned.getOwner();
    return owner == null ? 0 : owned.asEntity().getDistanceSq(owner);
  }

  @Override
  public void startExecuting() {
    pathingTimer = 0;
  }

  @Override
  public void resetTask() {
  }

  @Override
  public void updateTask() {
    EntityLivingBase owner = owned.getOwner();
    if (owner == null) {
      return;
    }
    double distance = getDistanceSqFromOwner();
    if (distance < minDistanceSq) {
      owned.asEntity().getNavigator().clearPath();
    }
    if (--pathingTimer <= 0) {
      pathingTimer = 10;
      owned.asEntity().getNavigator().tryMoveToEntityLiving(owner, followSpeed);
    }
  }
}