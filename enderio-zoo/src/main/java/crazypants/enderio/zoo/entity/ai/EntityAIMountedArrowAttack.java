package crazypants.enderio.zoo.entity.ai;

import javax.annotation.Nonnull;

import info.loenwind.autoconfig.factory.IValue;
import crazypants.enderio.zoo.entity.EntityUtil;
import crazypants.enderio.zoo.entity.SpawnUtil;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityAIMountedArrowAttack extends EntityAIBase {

  private final @Nonnull EntityLiving entityHost;

  private final @Nonnull IRangedAttackMob rangedAttackEntityHost;
  private EntityLivingBase attackTarget;
  private IValue<Double> entityMoveSpeed;
  private IValue<Double> mountedEntityMoveSpeed;

  private int timeUntilNextAttack;
  private int timeTargetVisible;

  private IValue<Integer> minRangedAttackTime;
  private IValue<Integer> maxRangedAttackTime;

  private IValue<Float> attackRange;

  private int runAwayTimer = 0;

  private PathPoint runningAwayTo;

  private IValue<Boolean> useRunAwayTactic;

  public EntityAIMountedArrowAttack(@Nonnull IRangedAttackMob host, IValue<Double> moveSpeed, IValue<Double> mountedEntityMoveSpeed,
      IValue<Integer> minAttackTime, IValue<Integer> maxAttackTime, IValue<Float> attackRange, IValue<Boolean> useRunAwayTactic) {
    timeUntilNextAttack = -1;
    rangedAttackEntityHost = host;
    entityHost = (EntityLiving) host;
    entityMoveSpeed = moveSpeed;
    this.mountedEntityMoveSpeed = mountedEntityMoveSpeed;
    minRangedAttackTime = minAttackTime;
    maxRangedAttackTime = maxAttackTime;
    this.attackRange = attackRange;
    this.useRunAwayTactic = useRunAwayTactic;
    setMutexBits(3);
  }

  @Override
  public boolean shouldExecute() {
    EntityLivingBase toAttack = entityHost.getAttackTarget();
    if (toAttack == null) {
      return false;
    } else {
      attackTarget = toAttack;
      return true;
    }
  }

  @Override
  public boolean shouldContinueExecuting() {
    return shouldExecute() || !getNavigator().noPath();
  }

  @Override
  public void resetTask() {
    attackTarget = null;
    timeTargetVisible = 0;
    timeUntilNextAttack = -1;
    runAwayTimer = 0;
    runningAwayTo = null;
  }

  /**
   * Updates the task
   */
  @Override
  public void updateTask() {
    final EntityLivingBase attackTarget2 = attackTarget;
    if (attackTarget2 == null) {
      return;
    }
    double distToTargetSq = entityHost.getDistanceSq(attackTarget.posX, attackTarget2.getEntityBoundingBox().minY, attackTarget.posZ);
    boolean canSeeTarget = entityHost.getEntitySenses().canSee(attackTarget2);

    if (canSeeTarget) {
      ++timeTargetVisible;
    } else {
      timeTargetVisible = 0;
    }

    boolean runningAway = isRunningAway();
    if (!runningAway) {
      runAwayTimer--;
    }

    float attackRangeSq = attackRange.get() * attackRange.get();

    if (!runningAway && distToTargetSq <= attackRangeSq && timeTargetVisible >= 20) {
      getNavigator().clearPath();
    } else if (distToTargetSq > (attackRangeSq * 0.9)) {
      getNavigator().tryMoveToEntityLiving(attackTarget2, getMoveSpeed());
    }

    if (canSeeTarget && entityHost.isRiding() && distToTargetSq < 36 && runAwayTimer <= 0 && runAway()) {
      --timeUntilNextAttack;
      return;
    }

    if (runningAway) {
      --timeUntilNextAttack;
      return;
    }

    entityHost.getLookHelper().setLookPositionWithEntity(attackTarget2, 30.0F, 30.0F);

    if (--timeUntilNextAttack == 0) {
      if (distToTargetSq > attackRangeSq || !canSeeTarget) {
        return;
      }
      float rangeRatio = MathHelper.sqrt(distToTargetSq) / attackRange.get();
      rangeRatio = MathHelper.clamp(rangeRatio, 0.1f, 1);
      rangedAttackEntityHost.attackEntityWithRangedAttack(attackTarget2, rangeRatio);
      timeUntilNextAttack = MathHelper.floor(rangeRatio * (maxRangedAttackTime.get() - minRangedAttackTime.get()) + minRangedAttackTime.get());
    } else if (timeUntilNextAttack < 0) {
      float rangeRatio = MathHelper.sqrt(distToTargetSq) / attackRange.get();
      timeUntilNextAttack = MathHelper.floor(rangeRatio * (maxRangedAttackTime.get() - minRangedAttackTime.get()) + minRangedAttackTime.get());
    }
  }

  private boolean isRunningAway() {

    if (runningAwayTo == null) {
      return false;
    }
    if (getNavigator().noPath()) {
      runningAwayTo = null;
      return false;
    }
    final Path path = getNavigator().getPath();
    PathPoint dest = path != null ? path.getFinalPathPoint() : null;
    return dest != null && dest.equals(runningAwayTo);
  }

  private boolean runAway() {
    if (!useRunAwayTactic.get()) {
      return false;
    }

    runAwayTimer = 40;
    Vec3d targetDir = new Vec3d(attackTarget.posX, attackTarget.getEntityBoundingBox().minY, attackTarget.posZ);
    Vec3d entityPos = EntityUtil.getEntityPosition(entityHost);

    targetDir = entityPos.subtract(targetDir).normalize().scale(attackRange.get() * 0.9).add(entityPos);

    BlockPos probePoint = new BlockPos((int) Math.round(targetDir.x), (int) Math.round(entityHost.posY), (int) Math.round(targetDir.z));

    World world = entityHost.getEntityWorld();

    BlockPos clearGround = SpawnUtil.findClearGround(world, probePoint);
    if (clearGround == null) {
      return false;
    }

    boolean res = getNavigator().tryMoveToXYZ(clearGround.getX() + .5, clearGround.getY(), clearGround.getZ() + .5, mountedEntityMoveSpeed.get());
    if (getNavigator().noPath()) {
      runningAwayTo = null;
    } else {
      final Path path = getNavigator().getPath();
      runningAwayTo = path != null ? path.getFinalPathPoint() : null;
    }
    return res;
  }

  private double getMoveSpeed() {
    return (entityHost.isRiding() ? mountedEntityMoveSpeed : entityMoveSpeed).get();
  }

  protected PathNavigate getNavigator() {
    return entityHost.getNavigator();
  }
}
