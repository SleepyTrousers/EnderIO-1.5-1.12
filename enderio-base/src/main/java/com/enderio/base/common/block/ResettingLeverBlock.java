package com.enderio.base.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Random;

public class ResettingLeverBlock extends LeverBlock {

    private final int delay;
    private final boolean inverted;

    public ResettingLeverBlock(int delaySeconds, boolean inverted) {
        super(BlockBehaviour.Properties.copy(Blocks.LEVER));
        this.delay = delaySeconds * 20;
        this.inverted = inverted;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pState.getValue(POWERED)) {
            pLevel
                .getBlockTicks()
                .scheduleTick(pPos, this, delay);
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Deprecated
    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, Random pRand) {
        super.tick(pState, pLevel, pPos, pRand);
        if (pState.getValue(POWERED) && !pLevel.isClientSide) {
            BlockState blockstate = this.pull(pState, pLevel, pPos);
            float f = blockstate.getValue(POWERED) ? 0.6F : 0.5F;
            pLevel.playSound(null, pPos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3F, f);
        }
    }

    @Override
    public int getSignal(BlockState pBlockState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide) {
        int res = super.getSignal(pBlockState, pBlockAccess, pPos,pSide);
        return inverted ? 15 - res : res;
    }

    @Override
    public int getDirectSignal(BlockState pBlockState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide) {
        int res = super.getDirectSignal(pBlockState, pBlockAccess, pPos, pSide);
        return inverted ? 15 - res : res;
    }

}
