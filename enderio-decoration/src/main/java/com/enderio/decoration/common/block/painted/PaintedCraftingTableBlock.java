package com.enderio.decoration.common.block.painted;

import com.enderio.decoration.common.block.DecorBlocks;
import com.enderio.decoration.common.blockentity.DecorBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PaintedCraftingTableBlock extends CraftingTableBlock implements EntityBlock {

    private static final Component CONTAINER_TITLE = new TranslatableComponent("container.crafting");

    public PaintedCraftingTableBlock(Properties p_52225_) {
        super(p_52225_);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return DecorBlockEntities.SINGLE_PAINTED.create(pos, state);
    }

    @Override
    public MenuProvider getMenuProvider(@Nonnull BlockState pState, @Nonnull Level pLevel, @Nonnull BlockPos pPos) {
        return new SimpleMenuProvider((p_52229_, p_52230_, p_52231_) -> new CraftingMenu(p_52229_, p_52230_, ContainerLevelAccess.create(pLevel, pPos)) {
            @Override
            public boolean stillValid(@Nonnull Player pPlayer) {
                try {
                    return stillValid(access, pPlayer, DecorBlocks.PAINTED_CRAFTING_TABLE.get());
                } catch (Exception e) {
                    return false;
                }
            }
        }, CONTAINER_TITLE);
    }
}
