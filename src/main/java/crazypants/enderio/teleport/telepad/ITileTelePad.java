package crazypants.enderio.teleport.telepad;

import com.enderio.core.api.common.util.IProgressTile;
import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.api.teleport.ITelePad;
import crazypants.enderio.power.IInternalPowerReceiver;
import net.minecraft.entity.Entity;

public interface ITileTelePad extends IInternalPowerReceiver, ITelePad, IProgressTile {

  int getUsage();

  Entity getCurrentTarget();

  boolean wasBlocked();

  int getPowerScaled(int powerScale);

  void enqueueTeleport(Entity e, boolean b);

  void dequeueTeleport(Entity e, boolean b);

  void setCoords_internal(BlockCoord bc);

  ITelePad setTargetDim_internal(int dim);

  void setBlocked(boolean wasBlocked);
  
}
