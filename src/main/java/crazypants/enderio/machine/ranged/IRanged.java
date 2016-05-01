package crazypants.enderio.machine.ranged;

import net.minecraft.world.World;

import com.enderio.core.common.util.BlockCoord;

public interface IRanged {

  // NB: We cant use getWorld as this has to be named differently to the TE method due to obf
  World getRangeWorldObj();

  BlockCoord getLocation();

  float getRange();

  boolean isShowingRange();

}
