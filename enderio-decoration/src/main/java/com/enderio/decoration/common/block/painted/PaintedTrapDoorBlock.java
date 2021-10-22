package com.enderio.decoration.common.block.painted;

import com.enderio.decoration.common.blockentity.DecorBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PaintedTrapDoorBlock extends TrapDoorBlock implements EntityBlock {

    public PaintedTrapDoorBlock(Properties p_52225_) {
        super(p_52225_);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return DecorBlockEntities.SINGLE_PAINTED.create(pos, state);
    }
}
