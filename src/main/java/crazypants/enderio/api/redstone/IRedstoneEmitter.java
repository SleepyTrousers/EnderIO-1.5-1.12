package crazypants.enderio.api.redstone;

import java.util.EnumMap;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderio.api.DyeColor;

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
   * @return An {@link EnumMap} of {@link DyeColor} to {@link Integer}. The
   *         integer represents the strength of the output on the given channel.
   */
  EnumMap<DyeColor, Integer> getOutputs(World world, int x, int y, int z, ForgeDirection to);
}
