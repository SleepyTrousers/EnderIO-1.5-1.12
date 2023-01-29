package crazypants.enderio.enderface;

import net.minecraft.inventory.Slot;

public class ContainerWrapper extends net.minecraft.inventory.Container {

    public net.minecraft.inventory.Container wrapped;

    public ContainerWrapper(net.minecraft.inventory.Container wrapped) {
        this.wrapped = wrapped;
        this.inventoryItemStacks = wrapped.inventoryItemStacks;
        this.inventorySlots = wrapped.inventorySlots;
        this.windowId = wrapped.windowId;
    }

    @Override
    public boolean canDragIntoSlot(Slot par1Slot) {
        return wrapped.canDragIntoSlot(par1Slot);
    }

    @Override
    public boolean canInteractWith(net.minecraft.entity.player.EntityPlayer arg0) {
        return true;
    }

    @Override
    public net.minecraft.inventory.Slot getSlot(int arg0) {
        return wrapped.getSlot(arg0);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public java.util.List getInventory() {
        return wrapped.getInventory();
    }

    @Override
    public boolean enchantItem(net.minecraft.entity.player.EntityPlayer arg0, int arg1) {
        return wrapped.enchantItem(arg0, arg1);
    }

    @Override
    public net.minecraft.item.ItemStack slotClick(int arg0, int arg1, int arg2,
            net.minecraft.entity.player.EntityPlayer arg3) {
        return wrapped.slotClick(arg0, arg1, arg2, arg3);
    }

    @Override
    public boolean func_94530_a(net.minecraft.item.ItemStack arg0, net.minecraft.inventory.Slot arg1) {
        return wrapped.func_94530_a(arg0, arg1);
    }

    @Override
    public void addCraftingToCrafters(net.minecraft.inventory.ICrafting arg0) {
        wrapped.addCraftingToCrafters(arg0);
    }

    @Override
    public void detectAndSendChanges() {
        wrapped.detectAndSendChanges();
    }

    @Override
    public void removeCraftingFromCrafters(net.minecraft.inventory.ICrafting arg0) {
        wrapped.removeCraftingFromCrafters(arg0);
    }

    @Override
    public net.minecraft.inventory.Slot getSlotFromInventory(net.minecraft.inventory.IInventory arg0, int arg1) {
        return wrapped.getSlotFromInventory(arg0, arg1);
    }

    @Override
    public net.minecraft.item.ItemStack transferStackInSlot(net.minecraft.entity.player.EntityPlayer arg0, int arg1) {
        return wrapped.transferStackInSlot(arg0, arg1);
    }

    @Override
    public void onCraftMatrixChanged(net.minecraft.inventory.IInventory arg0) {
        wrapped.onCraftMatrixChanged(arg0);
    }

    @Override
    public void putStackInSlot(int arg0, net.minecraft.item.ItemStack arg1) {
        wrapped.putStackInSlot(arg0, arg1);
    }

    @Override
    public void putStacksInSlots(net.minecraft.item.ItemStack[] arg0) {
        wrapped.putStacksInSlots(arg0);
    }

    @Override
    public void updateProgressBar(int arg0, int arg1) {
        wrapped.updateProgressBar(arg0, arg1);
    }

    @Override
    public short getNextTransactionID(net.minecraft.entity.player.InventoryPlayer arg0) {
        return wrapped.getNextTransactionID(arg0);
    }

    @Override
    public boolean isPlayerNotUsingContainer(net.minecraft.entity.player.EntityPlayer arg0) {
        return wrapped.isPlayerNotUsingContainer(arg0);
    }

    @Override
    public void setPlayerIsPresent(net.minecraft.entity.player.EntityPlayer arg0, boolean arg1) {
        wrapped.setPlayerIsPresent(arg0, arg1);
    }
}
