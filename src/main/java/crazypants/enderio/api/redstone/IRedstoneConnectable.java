package crazypants.enderio.api.redstone;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Implement this on your Block to control whether insulated restone conduits
 * will automatically connect to your block and how.
 * <p>
 * This is the base interface for {@link IRedstoneReceiver} and
 * {@link IRedstoneEmitter}
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
   * @return Whether to automatically connect a conduit in the given direction
   */
  boolean shouldRedstoneConduitConnect(World world, int x, int y, int z, ForgeDirection from);

  /**
   * Whether this connection is "special", meaning it will not allow the user to
   * choose the output signal color.
   * 
   * @param world
   *          {@link World} object
   * @param x
   *          X position of your block
   * @param y
   *          Y position of your block
   * @param z
   *          Z position of your block
   * @param from
   *          The {@link ForgeDirection} that the conduit is coming from
   * 
   * @return Whether this connection is special
   */
  boolean isSpecialConnection(World world, int x, int y, int z, ForgeDirection from);

}
