package crazypants.enderio.machines.machine.obelisk.attractor.handlers;

import crazypants.enderio.machines.machine.obelisk.attractor.TileAttractor;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySpider;

public class TargetAttractionHandler implements IMobAttractionHandler {

  @Override
  public boolean canAttract(TileAttractor attractor, EntityLiving entity) {
    return entity instanceof EntityPigZombie || entity instanceof EntitySpider;
  }

  @Override
  public void startAttracting(TileAttractor attractor, EntityLiving entity) {
    ((EntityMob) entity).setAttackTarget(attractor.getTarget());
  }

  @Override
  public void tick(TileAttractor attractor, EntityLiving entity) {
    double x = (attractor.getPos().getX() + 0.5D - entity.posX);
    double y = (attractor.getPos().getY() + 1D - entity.posY);
    double z = (attractor.getPos().getZ() + 0.5D - entity.posZ);
    double distance = Math.sqrt(x * x + y * y + z * z);
    if (distance > 2) {
      EntityMob mob = (EntityMob) entity;
      mob.faceEntity(attractor.getTarget(), 180, 0);
      // TODO are these values right?
      mob.moveRelative(0, 0, 1, 0);
      if (mob.posY < attractor.getPos().getY()) {
        mob.setJumping(true);
      } else {
        mob.setJumping(false);
      }
    }
  }

  @Override
  public void release(TileAttractor attractor, EntityLiving entity) {
  }

}
