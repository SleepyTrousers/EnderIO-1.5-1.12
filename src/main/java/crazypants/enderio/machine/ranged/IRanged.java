package crazypants.enderio.machine.ranged;

import net.minecraft.world.World;
import crazypants.util.BlockCoord;

public interface IRanged {

  World getWorldObj();

  BlockCoord getLocation();

  float getRange();

  boolean isShowingRange();

}
