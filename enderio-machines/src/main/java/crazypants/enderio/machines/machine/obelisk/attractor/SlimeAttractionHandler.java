package crazypants.enderio.machines.machine.obelisk.attractor;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntitySlime;

public class SlimeAttractionHandler implements IMobAttractionHandler {

  @Override
  public boolean canAttract(TileAttractor attractor, EntityLiving entity) {
    return entity instanceof EntitySlime;
  }

  @Override
  public void startAttracting(TileAttractor attractor, EntityLiving entity) {
    tick(attractor, entity);
  }

  @Override
  public void tick(TileAttractor attractor, EntityLiving entity) {
    entity.faceEntity(attractor.getTarget(), 10.0F, 20.0F);
  }

  @Override
  public void release(TileAttractor attractor, EntityLiving entity) {
  }

}
