package com.enderio.base.common.block.glass;

import com.enderio.core.client.render.IItemOverlayRender;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class FusedQuartzItem extends BlockItem implements IItemOverlayRender {
    public FusedQuartzItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public void renderOverlay(ItemStack pStack, int pXPosition, int pYPosition) {
        // TODO: Icons
    }
}
