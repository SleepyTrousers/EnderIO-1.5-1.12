package com.enderio.machines.common.menu;

import com.enderio.core.common.menu.SyncedMenu;
import com.enderio.machines.common.blockentity.AbstractMachineBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public abstract class MachineMenu<T extends AbstractMachineBlockEntity> extends SyncedMenu<T> {

    protected MachineMenu(@Nullable T blockEntity, Inventory inventory, @Nullable MenuType<?> pMenuType, int pContainerId) {
        super(blockEntity, inventory, pMenuType, pContainerId);
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        //TODO do something
        return true;
    }

    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (pIndex < this.slots.size() - 36) {
                if (!this.moveItemStackTo(itemstack1, this.slots.size() - 36, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, this.slots.size() - 36, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }
}
