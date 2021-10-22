package com.enderio.decoration.common.block.painted;

import com.enderio.decoration.common.blockentity.DecorBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PaintedSlabBlock extends SlabBlock implements EntityBlock {

    public PaintedSlabBlock(Properties p_56359_) {
        super(p_56359_);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return DecorBlockEntities.DOUBLE_PAINTED.create(pos, state);
    }

}
