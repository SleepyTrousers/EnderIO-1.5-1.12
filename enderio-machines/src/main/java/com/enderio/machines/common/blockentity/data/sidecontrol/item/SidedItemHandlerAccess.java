package com.enderio.machines.common.blockentity.data.sidecontrol.item;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class SidedItemHandlerAccess implements IItemHandler {

    private final ItemHandlerMaster master;
    private final Direction direction;

    public SidedItemHandlerAccess(ItemHandlerMaster master, Direction direction) {
        this.master = master;
        this.direction = direction;
    }

    @Override
    public int getSlots() {
        return master.getSlots();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return master.getStackInSlot(slot);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (master.getConfig().getIO(direction).canInput())
            return master.insertItem(slot, stack, simulate);
        return stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (master.getConfig().getIO(direction).canOutput())
            return master.extractItem(slot, amount, simulate);
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return master.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return master.isItemValid(slot, stack);
    }
}
