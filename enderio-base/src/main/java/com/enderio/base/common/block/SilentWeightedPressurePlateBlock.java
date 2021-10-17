package com.enderio.base.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.WeightedPressurePlateBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

public class SilentWeightedPressurePlateBlock extends WeightedPressurePlateBlock {

    public SilentWeightedPressurePlateBlock(WeightedPressurePlateBlock from) {
        super(getWeight(from),  BlockBehaviour.Properties.copy(from));
    }

    private static int getWeight(WeightedPressurePlateBlock from) {
        try {
            return ObfuscationReflectionHelper.getPrivateValue(WeightedPressurePlateBlock.class, from, "maxWeight");
        } catch (Exception e) {
            //TODO: Log
            return 15;
        }
    }

    @Override
    protected void playOnSound(LevelAccessor pLevel, BlockPos pPos) {

    }

    @Override
    protected void playOffSound(LevelAccessor pLevel, BlockPos pPos) {

    }

}
