package crazypants.enderio.base.machine.interfaces;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * Interface for blocks that want to return a different TileEntity for purposes of checking their data, e.g. for the TOP integration
 *
 */
public interface ITEProxy {

  @Nullable
  TileEntity getParent(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state);

}
