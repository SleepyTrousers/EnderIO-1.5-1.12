package crazypants.enderio.api.redstone;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderio.api.DyeColor;

/**
 * Implement this on blocks that need to receive and react to redstone signals
 * from conduits.
 */
public interface IRedstoneReceiver extends IRedstoneConnectable {

  /**
   * Callback for when the network is modified. Use this to update your block's
   * state based on redstone input.
   * 
   * @param world
   *          {@link World} object
   * @param x
   *          X position of your block
   * @param y
   *          Y position of your block
   * @param z
   *          Z position of your block
   * @param to
   *          The {@link ForgeDirection} that the output is going to
   * 
   * @return A {@link Signalbyte[]} of redstone strength, in order of color. The
   *         order of colors is defined in {@link DyeColor}.
   */
  void signalChanged(World world, int x, int y, int z, ForgeDirection from, Signal signal);

}
