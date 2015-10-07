package crazypants.enderio.machine.painter;

import javax.annotation.Nullable;

import net.minecraft.block.Block;

public interface IPaintableTileEntity {

  void setSourceBlockMetadata(int sourceBlockMetadata);

  int getSourceBlockMetadata();

  void setSourceBlock(Block sourceBlock);

  @Nullable
  Block getSourceBlock();

}
