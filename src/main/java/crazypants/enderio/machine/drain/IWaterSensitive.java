package crazypants.enderio.machine.drain;

import net.minecraft.world.World;
import crazypants.util.BlockCoord;

public interface IWaterSensitive {

  public boolean preventInfiniteWaterForming(World world, BlockCoord bc);
  
}
