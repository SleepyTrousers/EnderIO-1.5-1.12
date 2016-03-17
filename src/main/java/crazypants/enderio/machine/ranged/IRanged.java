package crazypants.enderio.machine.ranged;

import com.enderio.core.common.util.BlockCoord;

import net.minecraft.world.World;

public interface IRanged {

  World getWorld();
  
  //NB: We cant use getWorld as this has to be named differently to the TE method due to obf
  World getRangeWorldObj();

  BlockCoord getLocation();

  float getRange();

  boolean isShowingRange();

}
