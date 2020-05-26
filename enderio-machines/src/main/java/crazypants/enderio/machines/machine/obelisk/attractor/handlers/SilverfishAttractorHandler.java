package crazypants.enderio.machines.machine.obelisk.attractor.handlers;

import javax.annotation.Nonnull;

import crazypants.enderio.machines.machine.obelisk.attractor.TileAttractor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class SilverfishAttractorHandler implements IMobAttractionHandler {

  @Override
  public @Nonnull State canAttract(TileAttractor attractor, EntityLiving entity) {
    if (entity instanceof EntitySilverfish) {
      if (((EntityMob) entity).getAttackTarget() == attractor.getTarget()) {
        return State.ALREADY_ATTRACTING;
      }
      return State.CAN_ATTRACT;
    }
    return State.CANNOT_ATTRACT;
  }

  @Override
  public void startAttracting(TileAttractor attractor, EntityLiving entity) {
    ((EntityMob) entity).setAttackTarget(attractor.getTarget());
    tick(attractor, entity);
  }

  @Override
  public void tick(TileAttractor attractor, EntityLiving entity) {
    EntitySilverfish sf = (EntitySilverfish) entity;
    Path pathentity = getPathEntityToEntity(entity, attractor.getTarget(), attractor.getRange() * 2);
    sf.getNavigator().setPath(pathentity, sf.getAIMoveSpeed());
  }

  @Override
  public void release(TileAttractor attractor, EntityLiving entity) {
  }

  public Path getPathEntityToEntity(Entity entity, Entity targetEntity, float range) {
    int targX = MathHelper.floor(targetEntity.posX);
    int targY = MathHelper.floor(targetEntity.posY + 1.0D);
    int targZ = MathHelper.floor(targetEntity.posZ);

    PathFinder pf = new PathFinder(new WalkNodeProcessor());
    return pf.findPath(entity.world, (EntityLiving) entity, new BlockPos(targX, targY, targZ), range);
  }

}
