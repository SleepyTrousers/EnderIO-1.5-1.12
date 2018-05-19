package crazypants.enderio.zoo.entity.ai;

import java.util.List;

import crazypants.enderio.zoo.entity.EntityUtil;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;

public class EntityAIFlyingFindPerch extends EntityAIBase {
  private EntityCreature entity;
  private double xPosition;
  private double yPosition;
  private double zPosition;
  private double speed;
  private int executionChance;
  private int searchRange = 6;
  private int searchAttempts = 30;

  public EntityAIFlyingFindPerch(EntityCreature creatureIn, double speedIn) {
    this(creatureIn, speedIn, 120);
  }

  public EntityAIFlyingFindPerch(EntityCreature creatureIn, double speedIn, int chance) {
    entity = creatureIn;
    speed = speedIn;
    executionChance = chance;
    setMutexBits(1);
  }

  @Override
  public boolean shouldExecute() {
    int chance = executionChance;
    if (isOnLeaves()) { // if we are already on leaves, don't look for a new perch so often
      chance *= 10;
    } 
//    chance = 5;
    if (entity.getRNG().nextInt(chance) != 0) {
      return false;
    }

    BlockPos targetPos = EntityUtil.findRandomLandingSurface(entity, searchRange, entity.getPosition().getY() + 1, 250, searchAttempts);
    if (targetPos != null) {
      List<EntityCreature> others = entity.getEntityWorld().getEntitiesWithinAABB(entity.getClass(), EntityUtil.getBoundsAround(targetPos, 4));
      if (others != null && others.size() > 1) {
        return false;
      }
      xPosition = targetPos.getX() + 0.5;
      yPosition = targetPos.getY();
      zPosition = targetPos.getZ() + 0.5;
      return true;
    }
    return false;

  }

  private boolean isOnLeaves() {
    IBlockState bs = entity.getEntityWorld().getBlockState(entity.getPosition().down());
    return bs.getMaterial() == Material.LEAVES;
  }

  @Override
  public boolean shouldContinueExecuting() {
    return !entity.getNavigator().noPath();
  }

  @Override
  public void startExecuting() {
    if (!entity.getNavigator().tryMoveToXYZ(xPosition, yPosition, zPosition, speed)) {
//      System.out.println("EntityAIFlyingFindPerch.startExecuting: No path");
    }
  }

  public void setExecutionChance(int newchance) {
    executionChance = newchance;
  }
}