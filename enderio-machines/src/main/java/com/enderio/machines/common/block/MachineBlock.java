package com.enderio.machines.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

public class MachineBlock extends BaseEntityBlock {
    private BiFunction<BlockPos, BlockState, BlockEntity> beFactory;

    public MachineBlock(Properties p_49795_, BiFunction<BlockPos, BlockState, BlockEntity> beFactory) {
        super(p_49795_);
        this.beFactory = beFactory;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return beFactory.apply(pPos, pState);
    }
}
