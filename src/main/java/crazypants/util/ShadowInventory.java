package crazypants.util;

import com.enderio.core.common.util.Util;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class ShadowInventory implements IInventory {
    private final ItemStack[] items;
    private final IInventory master;

    public ShadowInventory(IInventory master) {
        this.master = master;
        items = new ItemStack[master.getSizeInventory()];
        for (int i = 0; i < master.getSizeInventory(); i++) {
            items[i] = master.getStackInSlot(i);
        }
    }

    @Override
    public int getSizeInventory() {
        return master.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int p_70301_1_) {
        return items[p_70301_1_];
    }

    @Override
    public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
        return Util.decrStackSize(this, p_70298_1_, p_70298_2_);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
        ItemStack stack = getStackInSlot(p_70304_1_);
        setInventorySlotContents(p_70304_1_, null);
        return stack;
    }

    @Override
    public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
        items[p_70299_1_] = p_70299_2_;
    }

    @Override
    public String getInventoryName() {
        return master.getInventoryName();
    }

    @Override
    public boolean hasCustomInventoryName() {
        return master.hasCustomInventoryName();
    }

    @Override
    public int getInventoryStackLimit() {
        return master.getInventoryStackLimit();
    }

    @Override
    public void markDirty() {}

    @Override
    public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
        return master.isUseableByPlayer(p_70300_1_);
    }

    @Override
    public void openInventory() {
        master.openInventory();
    }

    @Override
    public void closeInventory() {
        master.closeInventory();
    }

    @Override
    public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
        return master.isItemValidForSlot(p_94041_1_, p_94041_2_);
    }
}
