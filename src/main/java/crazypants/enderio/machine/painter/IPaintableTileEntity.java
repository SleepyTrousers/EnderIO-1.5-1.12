package crazypants.enderio.machine.painter;

import net.minecraft.block.Block;

public interface IPaintableTileEntity {

    void setSourceBlockMetadata(int sourceBlockMetadata);

    int getSourceBlockMetadata();

    void setSourceBlock(Block sourceBlock);

    Block getSourceBlock();
}
