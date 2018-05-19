package crazypants.enderio.zoo.entity.navigate;

import crazypants.enderio.zoo.entity.IFlyingMob;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.util.math.MathHelper;

public class FlyingMoveHelper extends EntityMoveHelper {

  // private EntityOwl owl;
  private EntityLiving entity;
  private IFlyingMob mob;

  private double maxDescentSpeed = 0.1;

  public FlyingMoveHelper(IFlyingMob owl) {
    super(owl.asEntityCreature());
    this.mob = owl;
    this.entity = owl.asEntityCreature();
  }

  @Override
  public void onUpdateMoveHelper() {

    if (!entity.getNavigator().noPath()) {
      double xDelta = posX - entity.posX;
      double yDelta = posY - entity.posY;
      double zDelta = posZ - entity.posZ;

      float moveFactor = 1;
      float moveSpeed = (float) (speed * entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
      entity.setAIMoveSpeed(entity.getAIMoveSpeed() + (moveSpeed - entity.getAIMoveSpeed()) * moveFactor);

      double distSq = xDelta * xDelta + yDelta * yDelta + zDelta * zDelta;
      double dist = MathHelper.sqrt(distSq);
      yDelta = yDelta / dist;
      if (yDelta > 0) {
        // Ensure enough lift to get up to the target
        yDelta = Math.max(0.1, yDelta);
      }
      double yMove = entity.getAIMoveSpeed() * yDelta * mob.getMaxClimbRate();
      entity.motionY += yMove;
      // Limit max downward speed
      if (!entity.isDead && !entity.onGround && entity.motionY < -maxDescentSpeed) {
        entity.motionY = -maxDescentSpeed;
      }

      // Limit crazy spinning when going straight down
      float tr = mob.getMaxTurnRate();
      if (yMove < -0.12) {
        tr = 10;
      }
      float yawAngle = (float) (MathHelper.atan2(zDelta, xDelta) * 180.0D / Math.PI) - 90.0F;
      entity.rotationYaw = limitAngle(entity.rotationYaw, yawAngle, tr);
      entity.renderYawOffset = entity.rotationYaw;

      // Look
      double d7 = entity.posX + (xDelta / dist * 2.0D);
      double d8 = entity.getEyeHeight() + entity.posY + (yDelta / dist * 1.0D);
      double d9 = entity.posZ + (zDelta / dist * 2.0D);

      EntityLookHelper entitylookhelper = entity.getLookHelper();
      double lookX = entitylookhelper.getLookPosX();
      double lookY = entitylookhelper.getLookPosY();
      double lookZ = entitylookhelper.getLookPosZ();

      if (!entitylookhelper.getIsLooking()) {
        lookX = d7;
        lookY = d8;
        lookZ = d9;
      }
      entity.getLookHelper().setLookPosition(lookX + (d7 - lookX) * 0.125D, lookY + (d8 - lookY) * 0.125D, lookZ + (d9 - lookZ) * 0.125D, 10.0F, 40.0F);
    } else {
      entity.setAIMoveSpeed(0.0F);
    }
  }

}