package crazypants.enderio.api.redstone;

import javax.annotation.Nonnull;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This is no longer used, instead override canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) on your Block.
 */
@Deprecated
public interface IRedstoneConnectable {

  @Deprecated
  boolean shouldRedstoneConduitConnect(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing from);

}
