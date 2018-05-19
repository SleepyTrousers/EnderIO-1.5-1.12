package crazypants.enderio.zoo.entity.ai;

import crazypants.enderio.zoo.entity.IFlyingMob;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class EntityAIFlyingAttackOnCollide extends EntityAIBase {
  
  private EntityCreature attacker;
  private int attackTick;
  private double speedTowardsTarget;
  private boolean longMemory;
  private Path entityPathEntity;
  private Class<? extends Entity> classTarget;
  private int delayCounter;
  private double targetX;
  private double targetY;
  private double targetZ;
  private int failedPathFindingPenalty = 0;
  private boolean canPenalize = false;  
  private IFlyingMob flyingMob;

  public EntityAIFlyingAttackOnCollide(IFlyingMob mob, Class<? extends Entity> targetClass, double speedIn, boolean useLongMemory) {
    this(mob, speedIn, useLongMemory);
    this.classTarget = targetClass;    
  }

  public EntityAIFlyingAttackOnCollide(IFlyingMob mob, double speedIn, boolean useLongMemory) {
    this.flyingMob = mob;
    this.attacker = mob.asEntityCreature();
    this.speedTowardsTarget = speedIn;
    this.longMemory = useLongMemory;    
    this.setMutexBits(3);
  }

  @Override
  public boolean shouldExecute() {
    EntityLivingBase entitylivingbase = attacker.getAttackTarget();
    if (entitylivingbase == null) {      
      return false;
    } else if (!entitylivingbase.isEntityAlive()) {
      return false;
    } else if (classTarget != null && !classTarget.isAssignableFrom(entitylivingbase.getClass())) {
      return false;
    }

    if (canPenalize) {
      if (--delayCounter <= 0) {
        setPathTo(entitylivingbase);
        targetX = 4 + attacker.getRNG().nextInt(7);
        return entityPathEntity != null;
      } else {
        return true;
      }
    }
    setPathTo(entitylivingbase);
    return this.entityPathEntity != null;

  }

  private void setPathTo(EntityLivingBase target) {
    Vec3d targPos = target.getPositionVector();
    AxisAlignedBB targBB = target.getEntityBoundingBox();
    entityPathEntity = attacker.getNavigator().getPathToPos(new BlockPos(targPos.x, targBB.maxY + 1, targPos.z));    
  }

  @Override
  public boolean shouldContinueExecuting() {
    EntityLivingBase target = attacker.getAttackTarget();
    if(target == null || !target.isEntityAlive()) {      
      return false;
    }        
    return !longMemory ? !attacker.getNavigator().noPath() : attacker.isWithinHomeDistanceFromPosition(new BlockPos(target));
  }

  @Override
  public void startExecuting() {
    flyingMob.getFlyingNavigator().setPath(entityPathEntity, speedTowardsTarget, true);
    delayCounter = 0;
  }

  @Override
  public void resetTask() {
    attacker.getNavigator().clearPathEntity();
  }

  @Override
  public void updateTask() {
    EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
    attacker.getLookHelper().setLookPositionWithEntity(entitylivingbase, 30.0F, 30.0F);
    double distToTargSq = attacker.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY, entitylivingbase.posZ);
    double attackRangSq = getAttackRangeSq(entitylivingbase);
    --delayCounter;

    if ((longMemory || attacker.getEntitySenses().canSee(entitylivingbase)) && delayCounter <= 0
        && (targetX == 0.0D && targetY == 0.0D && targetZ == 0.0D
            || entitylivingbase.getDistanceSq(targetX, targetY, targetZ) >= 1.0D || attacker.getRNG().nextFloat() < 0.05F)) {
      targetX = entitylivingbase.posX;
      targetY = entitylivingbase.getEntityBoundingBox().minY;
      targetZ = entitylivingbase.posZ;
      delayCounter = 4 + attacker.getRNG().nextInt(7);

      if (canPenalize) {
        targetX += failedPathFindingPenalty;
        if (attacker.getNavigator().getPath() != null) {
          PathPoint finalPathPoint = attacker.getNavigator().getPath().getFinalPathPoint();
          if (finalPathPoint != null && entitylivingbase.getDistanceSq(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 1)
            failedPathFindingPenalty = 0;
          else
            failedPathFindingPenalty += 10;
        } else {
          failedPathFindingPenalty += 10;
        }
      }

      if (distToTargSq > 1024) {
        delayCounter += 10;
      } else if (distToTargSq > 256) {
        delayCounter += 5;
      }

      if (!flyToAttacker(entitylivingbase)) {
        delayCounter += 15;
      }
    }

    attackTick = Math.max(attackTick - 1, 0);

    if (distToTargSq <= attackRangSq && attackTick <= 0) {
      attackTick = 20;
      if (attacker.getHeldItem(EnumHand.MAIN_HAND) != null) {
        attacker.swingArm(EnumHand.MAIN_HAND);
      }
      attacker.attackEntityAsMob(entitylivingbase);
    }
  }

  private boolean flyToAttacker(EntityLivingBase targetEnt) {        
    AxisAlignedBB targBB = targetEnt.getEntityBoundingBox();    
    return flyingMob.getFlyingNavigator().tryFlyToPos(targetEnt.posX, targBB.maxY + 0.5, targetEnt.posZ, speedTowardsTarget);
  }

  protected double getAttackRangeSq(EntityLivingBase attackTarget) {
    return (attacker.width * 2.0F * attacker.width * 2.0F + attackTarget.width);
  }

}
