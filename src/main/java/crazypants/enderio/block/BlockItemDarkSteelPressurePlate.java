package crazypants.enderio.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;

public class BlockItemDarkSteelPressurePlate extends ItemBlockWithMetadata {

    public BlockItemDarkSteelPressurePlate(Block block) {
        super(block, block);
    }

    @Override
    public int getMetadata(int p_77647_1_) {
        return 0;
    }
}
