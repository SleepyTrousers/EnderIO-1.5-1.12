package crazypants.enderio.machine.painter;

import net.minecraft.block.state.IBlockState;

@Deprecated
public interface IPaintableTileEntity {

  @Deprecated
  void setSourceBlock(IBlockState sourceBlock);

  @Deprecated
  IBlockState getSourceBlock();

}
