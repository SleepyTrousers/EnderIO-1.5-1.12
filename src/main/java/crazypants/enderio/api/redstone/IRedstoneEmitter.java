package crazypants.enderio.api.redstone;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Implement this on a block which can control colored redstone conduit outputs.
 */
public interface IRedstoneEmitter extends IRedstoneConnectable {

  /**
   * Used to control the input a redstone conduit receieves when connected to
   * your block.
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
   * @return A {@code byte[]} of redstone strength, in order of color. The order
   *         of colors is defined in {@link DyeColor}.
   */
  byte[] getOutputs(World world, int x, int y, int z, ForgeDirection to);
}
