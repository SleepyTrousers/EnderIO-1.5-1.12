package com.enderio.core.client.render;

import net.minecraft.world.item.ItemStack;

public interface IItemOverlayRender {
    void renderOverlay(ItemStack pStack, int pXPosition, int pYPosition);
}
