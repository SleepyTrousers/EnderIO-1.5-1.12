package com.enderio.base.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;

public class DarkSteelLadderBlock extends LadderBlock {
    public DarkSteelLadderBlock(Properties p_54345_) {
        super(p_54345_);
    }

    @Override
    public void entityInside(@Nonnull BlockState pState, @Nonnull Level pLevel, @Nonnull BlockPos pPos, @Nonnull Entity pEntity) {
        if (!(pEntity instanceof Player playerEntity) || pEntity.isOnGround() || pEntity.isCrouching() || !playerEntity.onClimbable())
            return;

        // TODO: Make the player go faster.
    }
}
