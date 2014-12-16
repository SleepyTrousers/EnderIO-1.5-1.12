package crazypants.enderio.api.redstone;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Implement this on your Block or TileEntity to control whether insulated
 * restone conduits will automatically connect to your block.
 * <p>
 * Implementing on both is not recommended and will likely not work as expected.
 */
public interface IRedstoneConnectable {

  /**
   * When placed, should a redstone conduit automatically connect to your
   * Block/TileEntity
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
   *          The {@link ForgeDirection} that the conduit is coming from
   * @return
   *          Whether to automatically connect a conduit in the given direction
   */
  boolean shouldRedstoneConduitConnect(World world, int x, int y, int z, ForgeDirection from);

}
