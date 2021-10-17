package com.enderio.base.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.WeightedPressurePlateBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class SilentWeightedPressurePlateBlock extends WeightedPressurePlateBlock {

    public SilentWeightedPressurePlateBlock(WeightedPressurePlateBlock from) {
        super(from.maxWeight,  BlockBehaviour.Properties.copy(from));
    }

    @Override
    protected void playOnSound(LevelAccessor pLevel, BlockPos pPos) {

    }

    @Override
    protected void playOffSound(LevelAccessor pLevel, BlockPos pPos) {

    }

}
