package crazypants.enderio.machines.machine.obelisk.attractor.handlers;

import crazypants.enderio.machines.machine.obelisk.attractor.TileAttractor;
import net.minecraft.entity.EntityLiving;

public interface IMobAttractionHandler {

  boolean canAttract(TileAttractor attractor, EntityLiving entity);

  void startAttracting(TileAttractor attractor, EntityLiving entity);

  void tick(TileAttractor attractor, EntityLiving entity);

  void release(TileAttractor attractor, EntityLiving entity);

}
