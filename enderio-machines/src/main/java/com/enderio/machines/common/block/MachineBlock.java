package com.enderio.machines.common.block;

import com.enderio.machines.common.blockentity.AbstractMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

public class MachineBlock extends BaseEntityBlock {
    private BiFunction<BlockPos, BlockState, AbstractMachineBlockEntity> beFactory;

    public MachineBlock(Properties p_49795_, BiFunction<BlockPos, BlockState, AbstractMachineBlockEntity> beFactory) {
        super(p_49795_);
        this.beFactory = beFactory;
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        super.neighborChanged(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving);
        updateBlockEntityCache(pLevel, pPos);
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        super.onNeighborChange(state, level, pos, neighbor);
        updateBlockEntityCache(level, pos);
    }

    private void updateBlockEntityCache(LevelReader level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof AbstractMachineBlockEntity machineBlockEntity) {
            machineBlockEntity.updateCache();
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return beFactory.apply(pPos, pState);
    }
}
