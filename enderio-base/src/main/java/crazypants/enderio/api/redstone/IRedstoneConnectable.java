package crazypants.enderio.api.redstone;

import javax.annotation.Nonnull;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * NOTE: This is no longer needed, instead override canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) on your Block.
 *
 * Implement this on your Block or TileEntity to control whether insulated redstone conduits will automatically connect to your block.
 * <p>
 * Implementing on both is not recommended and will likely not work as expected.
 */
@Deprecated
public interface IRedstoneConnectable {

  boolean shouldRedstoneConduitConnect(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing from);

}
