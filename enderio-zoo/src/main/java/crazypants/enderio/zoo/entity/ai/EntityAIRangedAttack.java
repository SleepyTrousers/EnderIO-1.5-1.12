package crazypants.enderio.zoo.entity.ai;

import com.enderio.core.common.util.NullHelper;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.math.MathHelper;

public class EntityAIRangedAttack extends EntityAIBase {

  private final EntityLiving entityHost;
  private final IRangedAttackMob rangedAttackEntityHost;
  private EntityLivingBase attackTarget;

  private int timeUntilNextAttack;
  private double entityMoveSpeed;

  private int timeTargetVisible;
  private int timeTargetHidden;
  private int minRangedAttackTime;
  private int maxRangedAttackTime;

  private float attackRange;
  private float attackRangeSq;

  public EntityAIRangedAttack(IRangedAttackMob host, double moveSpeed, int timeBetweenAttacks, float attackRange) {
    this(host, moveSpeed, timeBetweenAttacks, timeBetweenAttacks, attackRange);
  }

  public EntityAIRangedAttack(IRangedAttackMob host, double moveSpeed, int minTimeBetweenAttacks, int maxTimeBetweenAttacks, float range) {
    timeUntilNextAttack = -1;

    rangedAttackEntityHost = host;
    entityHost = (EntityLiving) host;
    entityMoveSpeed = moveSpeed;
    minRangedAttackTime = minTimeBetweenAttacks;
    maxRangedAttackTime = maxTimeBetweenAttacks;
    attackRange = range;
    attackRangeSq = attackRange * attackRange;
    setMutexBits(3);
  }

  @Override
  public boolean shouldExecute() {
    EntityLivingBase target = entityHost.getAttackTarget();
    if (target == null) {
      return false;
    }
    attackTarget = target;
    return true;
  }

  public EntityLivingBase getAttackTarget() {
    return attackTarget;
  }

  @Override
  public boolean shouldContinueExecuting() {
    return shouldExecute() || !entityHost.getNavigator().noPath();
  }

  protected double getTargetDistance() {
    IAttributeInstance iattributeinstance = entityHost.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
    return NullHelper.untrust(iattributeinstance) == null ? 16.0D : iattributeinstance.getAttributeValue();
  }

  @Override
  public void resetTask() {
    attackTarget = null;
    timeTargetVisible = 0;
    timeUntilNextAttack = -1;
  }

  @Override
  public void updateTask() {
    final EntityLivingBase attackTarget2 = attackTarget;
    if (attackTarget2 == null) {
      return;
    }
    double distToTargetSq = entityHost.getDistanceSq(attackTarget.posX, attackTarget2.getEntityBoundingBox().minY, attackTarget.posZ);
    boolean canSee = entityHost.getEntitySenses().canSee(attackTarget2);

    if (canSee) {
      ++timeTargetVisible;
    } else {
      timeTargetVisible = 0;
    }

    if (distToTargetSq <= attackRangeSq && timeTargetVisible >= 20) {
      entityHost.getNavigator().clearPath();
    } else if (timeTargetHidden < 100) {
      entityHost.getNavigator().tryMoveToEntityLiving(attackTarget2, entityMoveSpeed);
    }
    entityHost.getLookHelper().setLookPositionWithEntity(attackTarget2, 30.0F, 30.0F);

    if (--timeUntilNextAttack <= 0) {
      if (distToTargetSq > attackRangeSq || !canSee) {
        return;
      }
      float rangeRatio = MathHelper.sqrt(distToTargetSq) / attackRange;
      if (rangeRatio < 0.1F) {
        rangeRatio = 0.1F;
      } else if (rangeRatio > 1.0F) {
        rangeRatio = 1.0F;
      }
      rangedAttackEntityHost.attackEntityWithRangedAttack(attackTarget2, rangeRatio);
      timeUntilNextAttack = MathHelper.floor(rangeRatio * (maxRangedAttackTime - minRangedAttackTime) + minRangedAttackTime);

    } else if (timeUntilNextAttack < 0) {
      entityHost.setAttackTarget(attackTarget2);
      float rangeRatio = MathHelper.sqrt(distToTargetSq) / attackRange;
      timeUntilNextAttack = MathHelper.floor(rangeRatio * (maxRangedAttackTime - minRangedAttackTime) + minRangedAttackTime);
    }
  }
}