package crazypants.enderio.render;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface IRenderMapper {

  List<IBlockState> mapBlockRender(IBlockState state, IBlockAccess world, BlockPos pos);

}
