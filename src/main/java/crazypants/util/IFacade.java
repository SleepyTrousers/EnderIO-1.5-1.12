package crazypants.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

public interface IFacade {

  IBlockState getFacade(IBlockAccess world, BlockPos pos, EnumFacing side);
  
  
}
