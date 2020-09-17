package crazypants.enderio.machines.machine.obelisk.attractor.handlers;

import javax.annotation.Nonnull;

import crazypants.enderio.machines.machine.obelisk.attractor.TileAttractor;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntitySlime;

public class SlimeAttractionHandler extends AIAttractionHandler {

  @Override
  public @Nonnull State canAttract(TileAttractor attractor, EntityLiving entity) {
    if (entity instanceof EntitySlime) {
      return super.canAttract(attractor, entity);
    }
    return State.CANNOT_ATTRACT;
  }

  @Override
  protected @Nonnull AttractTask makeAITask(TileAttractor attractor, @Nonnull EntityLiving entity) {
    return new SlimeAttractTask((EntitySlime) entity, attractor.getTarget(), attractor.getPos());
  }

}
