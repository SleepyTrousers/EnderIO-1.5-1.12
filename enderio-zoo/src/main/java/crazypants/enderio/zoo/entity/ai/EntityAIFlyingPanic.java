package crazypants.enderio.zoo.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.Vec3d;

public class EntityAIFlyingPanic extends EntityAIBase {

  private EntityCreature theEntityCreature;
  protected double speed;
  private double randPosX;
  private double randPosY;
  private double randPosZ;

  public EntityAIFlyingPanic(EntityCreature creature, double speedIn) {
    theEntityCreature = creature;
    speed = speedIn;
    setMutexBits(1);
  }

  @Override
  public boolean shouldExecute() {
    if (theEntityCreature.getRevengeTarget() == null && !theEntityCreature.isBurning()) {
      return false;
    }
    Vec3d vec3 = RandomPositionGenerator.findRandomTarget(theEntityCreature, 5, 4);
    if (vec3 == null) {
      return false;
    }
    double yOffset = 1 + theEntityCreature.getEntityWorld().rand.nextInt(3);
    //double yOffset = 0;
    randPosX = vec3.x;
    randPosY = vec3.y + yOffset;
    randPosZ = vec3.z;
    return true;
  }

  @Override
  public void startExecuting() {
    theEntityCreature.getNavigator().tryMoveToXYZ(randPosX, randPosY, randPosZ, speed);
  }

  @Override
  public boolean shouldContinueExecuting() {
    return !theEntityCreature.getNavigator().noPath();
  }

}
