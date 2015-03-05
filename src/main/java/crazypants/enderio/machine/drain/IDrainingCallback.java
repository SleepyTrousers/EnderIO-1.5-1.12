package crazypants.enderio.machine.drain;

import net.minecraft.world.World;
import crazypants.util.BlockCoord;

public interface IDrainingCallback {

  public void onWaterDrain(World world, BlockCoord bc);

  public void onWaterDrainNearby(World world, BlockCoord bc);

}
