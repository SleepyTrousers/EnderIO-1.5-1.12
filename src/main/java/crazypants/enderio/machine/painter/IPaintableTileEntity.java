package crazypants.enderio.machine.painter;

import net.minecraft.block.state.IBlockState;

public interface IPaintableTileEntity {

  void setSourceBlock(IBlockState sourceBlock);

  IBlockState getSourceBlock();

}
