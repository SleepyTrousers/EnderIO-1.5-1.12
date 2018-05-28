package crazypants.enderio.zoo.entity.ai;

import javax.annotation.Nonnull;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.Vec3d;

public class EntityAIFlyingShortWander extends EntityAIBase {

  private @Nonnull EntityCreature entity;
  protected double speed;
  private double randPosX;
  private double randPosY;
  private double randPosZ;
  private int executionChance;

  public EntityAIFlyingShortWander(@Nonnull EntityCreature creature, double speedIn, int executionChanceIn) {
    entity = creature;
    speed = speedIn;
    executionChance = executionChanceIn;
    setMutexBits(1);
  }

  @Override
  public boolean shouldExecute() {
    int chance = executionChance;
    if (isOnLeaves()) {
      chance *= 2;
    }
    if (entity.getRNG().nextInt(chance) != 0) {
      return false;
    }

    Vec3d vec3 = RandomPositionGenerator.findRandomTarget(entity, 4, 2);
    if (vec3 == null || entity.posY - vec3.y < -2) {
      return false;
    }
    randPosX = vec3.x;
    randPosY = vec3.y;
    randPosZ = vec3.z;
    return true;
  }

  @Override
  public void startExecuting() {
    entity.getNavigator().tryMoveToXYZ(randPosX, randPosY, randPosZ, speed);
  }

  @Override
  public boolean shouldContinueExecuting() {
    return !entity.getNavigator().noPath();
  }

  private boolean isOnLeaves() {
    IBlockState bs = entity.getEntityWorld().getBlockState(entity.getPosition().down());
    return bs.getMaterial() == Material.LEAVES;
  }
}
