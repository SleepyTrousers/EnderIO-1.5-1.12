package com.enderio.base.common.block;

import com.enderio.base.common.blockentity.EIOBlockEntities;
import com.enderio.base.common.blockentity.GraveBlockEntity;
import com.enderio.base.common.capability.EIOCapabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class GraveBlock extends Block implements EntityBlock{
    
    public GraveBlock(Properties p_49795_) {
        super(p_49795_);
        // TODO Auto-generated constructor stub
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        BlockEntity be = pLevel.getBlockEntity(pPos);
        if (be instanceof GraveBlockEntity grave) {
            grave.getCapability(EIOCapabilities.OWNER, pHit.getDirection()).ifPresent(owner -> {
                if (pPlayer.getUUID().equals(owner.getUUID()) || owner.getUUID() == null) {
                    grave.getItems().forEach(item ->{
                        if (!pPlayer.addItem(item)) {
                            Containers.dropItemStack(pLevel, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), item);
                        }
                    });
                    pLevel.removeBlock(grave.getBlockPos(), false);
                    pLevel.removeBlockEntity(grave.getBlockPos());
                } else {
                    //TODO message
                }
            });
        }
        return InteractionResult.CONSUME;
    }
    
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return EIOBlockEntities.GRAVE.create(pPos, pState);
    }
    
    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        if (pLevel.getBlockEntity(pPos) instanceof GraveBlockEntity grave) {
            grave.getCapability(EIOCapabilities.OWNER).ifPresent(owner -> owner.setUUID(pPlacer.getUUID()));
        }
    }
}
