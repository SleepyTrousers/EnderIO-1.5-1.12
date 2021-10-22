package com.enderio.base.common.blockentity;

import java.util.Collection;

import com.enderio.base.common.capability.EIOCapabilities;
import com.enderio.base.common.capability.owner.IOwner;
import com.enderio.base.common.capability.owner.Owner;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GraveBlockEntity extends BlockEntity {
    private final Owner owner = new Owner();
    private final LazyOptional<IOwner> ownerLazy = LazyOptional.of(() -> owner);
    private final GraveItemStackHandler itemHandler = new GraveItemStackHandler();
    private final LazyOptional<IItemHandler> itemLazy = LazyOptional.of(() -> itemHandler);

    public GraveBlockEntity(BlockEntityType<?> type, BlockPos pWorldPosition, BlockState pBlockState) {
        super(type, pWorldPosition, pBlockState);
    }

    // region Items

    public void addDrops(Collection<ItemEntity> drops) {
        NonNullList<ItemStack> stacks = NonNullList.create();
        drops.forEach(entity -> stacks.add(entity.getItem()));
        this.itemHandler.setItems(stacks);
    }

    public Collection<ItemStack> getItems() {
        return this.itemHandler.getItems();
    }

    // endregion

    // region Capabilities

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        ownerLazy.invalidate();
        itemLazy.invalidate();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        if (cap == EIOCapabilities.OWNER) {
            return this.ownerLazy.cast();
        }
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return this.itemLazy.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void load(CompoundTag pTag) {
        owner.deserializeNBT(pTag.getCompound("owner"));
        itemHandler.deserializeNBT(pTag.getCompound("inv"));
        super.load(pTag);
    }

    @Nonnull
    @Override
    public CompoundTag save(CompoundTag pTag) {
        pTag.put("owner", owner.serializeNBT());
        pTag.put("inv", itemHandler.serializeNBT());
        return super.save(pTag);
    }

    // endregion

    // region Networking

    @Override
    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(this.worldPosition, 0, this.getUpdateTag());
    }

    @Override
    @Nonnull
    public CompoundTag getUpdateTag() {
        return this.save(new CompoundTag());
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.load(pkt.getTag());
    }

    // endregion

    private static class GraveItemStackHandler extends ItemStackHandler {

        public void setItems(NonNullList<ItemStack> items) {
            this.stacks = items;
        }

        public NonNullList<ItemStack> getItems() {
            return stacks;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return stack;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }
    }
}
