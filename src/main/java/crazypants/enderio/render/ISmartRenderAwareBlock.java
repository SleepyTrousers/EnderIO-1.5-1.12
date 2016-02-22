package crazypants.enderio.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface ISmartRenderAwareBlock {

  IRenderMapper getRenderMapper(IBlockState state, IBlockAccess world, BlockPos pos);

}
