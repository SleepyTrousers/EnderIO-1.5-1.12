package crazypants.enderio.api.redstone;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * Implement this on your Block or TileEntity to control whether insulated
 * restone conduits will automatically connect to your block.
 * <p>
 * Implementing on both is not recommended and will likely not work as expected.
 */
public interface IRedstoneConnectable {

  boolean shouldRedstoneConduitConnect(World world, int x, int y, int z, EnumFacing from);

}
