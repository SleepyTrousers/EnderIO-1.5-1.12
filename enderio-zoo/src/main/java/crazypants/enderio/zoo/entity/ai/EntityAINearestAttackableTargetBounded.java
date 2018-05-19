package crazypants.enderio.zoo.entity.ai;

import java.util.Collections;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;

public class EntityAINearestAttackableTargetBounded<T extends EntityLivingBase> extends EntityAINearestAttackableTarget<T> {

  private double distanceOverride = -1;
  private final int targetChance;
  private double vertDistOverride = -1;

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

  public double getMaxDistanceToTarget() {
    return distanceOverride;
  }

  public void setMaxDistanceToTarget(double distance) {
    this.distanceOverride = distance;
  }
  
  public double getMaxVerticalDistanceToTarget() {
    return distanceOverride;
  }

  public void setMaxVerticalDistanceToTarget(double vertDist) {
    vertDistOverride = vertDist; 
  }

  @Override
  protected double getTargetDistance() {
    if (distanceOverride > 0) {
      return distanceOverride;
    }
    return super.getTargetDistance();
  }

  @Override
  public boolean shouldExecute() {
    if (targetChance > 0 && taskOwner.getRNG().nextInt(targetChance) != 0) {
      return false;
    } else {
      double horizDist = getTargetDistance();
      double vertDist = getVerticalDistance();
      
      AxisAlignedBB bb = taskOwner.getEntityBoundingBox().expand(horizDist, vertDist, horizDist);
      List<T> list = taskOwner.getEntityWorld().<T> getEntitiesWithinAABB(targetClass, bb,
          Predicates.<T> and(targetEntitySelector, EntitySelectors.NOT_SPECTATING));
      Collections.sort(list, sorter);

      if (list.isEmpty()) {
        return false;
      } else {
        this.targetEntity = list.get(0);
        return true;
      }
    }
  }

  private double getVerticalDistance() {
    if(vertDistOverride > 0) {
      return vertDistOverride;
    }
    return 4;
  }

}
