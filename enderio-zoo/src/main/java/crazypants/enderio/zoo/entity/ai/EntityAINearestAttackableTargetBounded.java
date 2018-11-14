package crazypants.enderio.zoo.entity.ai;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import info.loenwind.autoconfig.factory.IValue;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;

public class EntityAINearestAttackableTargetBounded<T extends EntityLivingBase> extends EntityAINearestAttackableTarget<T> {

  private IValue<Double> distanceOverride = null;
  private final int targetChance;
  private IValue<Double> vertDistOverride = null;

  public EntityAINearestAttackableTargetBounded(EntityCreature creature, Class<T> classTarget, boolean checkSight) {
    this(creature, classTarget, checkSight, false);
  }

  public EntityAINearestAttackableTargetBounded(EntityCreature creature, Class<T> classTarget, boolean checkSight, boolean onlyNearby) {
    this(creature, classTarget, 10, checkSight, onlyNearby, (Predicate<? super T>) null);
  }

  public EntityAINearestAttackableTargetBounded(EntityCreature creature, Class<T> classTarget, int chance, boolean checkSight, boolean onlyNearby,
      final Predicate<? super T> targetSelector) {
    super(creature, classTarget, chance, checkSight, onlyNearby, targetSelector);
    targetChance = chance;
  }

  public @Nonnull EntityAINearestAttackableTargetBounded<T> setMaxDistanceToTarget(@Nonnull IValue<Double> distance) {
    this.distanceOverride = distance;
    return this;
  }

  public @Nonnull EntityAINearestAttackableTargetBounded<T> setMaxVerticalDistanceToTarget(@Nonnull IValue<Double> vertDist) {
    vertDistOverride = vertDist;
    return this;
  }

  @Override
  protected double getTargetDistance() {
    if (distanceOverride != null) {
      return distanceOverride.get();
    }
    return super.getTargetDistance();
  }

  @Override
  public boolean shouldExecute() {
    if (getTargetDistance() > 0 && (targetChance <= 0 || taskOwner.getRNG().nextInt(targetChance) == 0)) {
      double horizDist = getTargetDistance();
      double vertDist = getVerticalDistance();

      AxisAlignedBB bb = taskOwner.getEntityBoundingBox().expand(horizDist, vertDist, horizDist);
      List<T> list = taskOwner.getEntityWorld().<T> getEntitiesWithinAABB(targetClass, bb,
          Predicates.<T> and(targetEntitySelector, EntitySelectors.NOT_SPECTATING));
      Collections.sort(list, sorter);

      if (!list.isEmpty()) {
        final T t = list.get(0);
        if (t != null) {
          this.targetEntity = t;
          return true;
        }
      }
    }
    return false;
  }

  private double getVerticalDistance() {
    if (vertDistOverride != null) {
      return vertDistOverride.get();
    }
    return 4;
  }

}
