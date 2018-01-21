package crazypants.enderio.machines.machine.obelisk.attractor.handlers;

import crazypants.enderio.machines.machine.obelisk.attractor.TileAttractor;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityEnderman;

public class EndermanAttractionHandler extends AIAttractionHandler {

  @Override
  public boolean canAttract(TileAttractor attractor, EntityLiving entity) {
    return entity instanceof EntityEnderman;
  }

  @Override
  public void startAttracting(TileAttractor attractor, EntityLiving entity) {
    super.startAttracting(attractor, entity);
    entity.getEntityData().setBoolean("EIO:tracked", true);
  }

  @Override
  public void tick(TileAttractor attractor, EntityLiving entity) {
    super.tick(attractor, entity);
    ((EntityEnderman) entity).setAttackTarget(attractor.getTarget());
  }

  @Override
  public void release(TileAttractor attractor, EntityLiving entity) {
    super.release(attractor, entity);
    entity.getEntityData().setBoolean("EIO:tracked", false);
  }

}
