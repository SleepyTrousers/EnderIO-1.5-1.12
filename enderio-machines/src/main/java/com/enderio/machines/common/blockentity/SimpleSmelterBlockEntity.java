package com.enderio.machines.common.blockentity;

import com.enderio.machines.common.blockentity.data.sidecontrol.item.ItemHandlerMaster;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SimpleSmelterBlockEntity extends AbstractMachineBlockEntity {
    private final ItemStackHandler itemHandler = createItemHandler();
    private final LazyOptional<IItemHandler> itemHandlerLazy = LazyOptional.of(() -> itemHandler);

    public SimpleSmelterBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);
    }

    @Override
    public ItemHandlerMaster getItemHandlerMaster() {
        return null;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return itemHandlerLazy.cast();
        }

        return super.getCapability(cap, side);
    }

    private ItemStackHandler createItemHandler() {
        return new ItemStackHandler(4) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if (slot == 0)
                    return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0;
                return false;
            }
        };
    }

    @Nullable
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return null;
    }
}
