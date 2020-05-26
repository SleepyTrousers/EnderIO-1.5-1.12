package crazypants.enderio.machines.machine.obelisk.attractor.handlers;

import javax.annotation.Nonnull;

import crazypants.enderio.machines.machine.obelisk.attractor.TileAttractor;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntitySlime;

public class SlimeAttractionHandler implements IMobAttractionHandler {

  @Override
  public @Nonnull State canAttract(TileAttractor attractor, EntityLiving entity) {
    if (entity instanceof EntitySlime) {
      return State.CAN_ATTRACT;
    }
    return State.CANNOT_ATTRACT;
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
