package com.enderio.decoration.common.blockentity;

import net.minecraft.world.level.block.Block;

public interface IPaintableBlockEntity {
    Block getPaint();

    default Block[] getPaints() {
        return new Block[] { getPaint() };
    }
}
