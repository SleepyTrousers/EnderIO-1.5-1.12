package crazypants.enderio.conduit.gui.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.item.filter.IItemFilterUpgrade;

public class InventoryUpgrades implements IInventory {

    IItemConduit itemConduit;
    ForgeDirection dir;

    public InventoryUpgrades(IItemConduit itemConduit, ForgeDirection dir) {
        this.itemConduit = itemConduit;
        this.dir = dir;
    }

    @Override
    public int getSizeInventory() {
        return 4;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        switch (slot) {
            case 0:
                return itemConduit.getSpeedUpgrade(dir);
            case 1:
                return itemConduit.getFunctionUpgrade(dir);
            case 2:
                return itemConduit.getInputFilterUpgrade(dir);
            case 3:
                return itemConduit.getOutputFilterUpgrade(dir);
            default:
                return null;
        }
    }

    @Override
    public ItemStack decrStackSize(int slot, int num) {
        ItemStack current = getStackInSlot(slot);
        if (current == null) {
            return current;
        }
        ItemStack result;
        ItemStack remaining;
        if (num >= current.stackSize) {
            result = current.copy();
            remaining = null;
        } else {
            result = current.copy();
            result.stackSize = num;
            remaining = current.copy();
            remaining.stackSize -= num;
        }
        setInventorySlotContents(slot, remaining);
        return result;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int var1) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack var2) {
        switch (slot) {
            case 0:
                itemConduit.setSpeedUpgrade(dir, var2);
                break;
            case 1:
                itemConduit.setFunctionUpgrade(dir, var2);
                break;
            case 2:
                itemConduit.setInputFilterUpgrade(dir, var2);
                break;
            case 3:
                itemConduit.setOutputFilterUpgrade(dir, var2);
                break;
        }
    }

    @Override
    public String getInventoryName() {
        return "Upgrades";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 15;
    }

    @Override
    public void markDirty() {}

    @Override
    public boolean isUseableByPlayer(EntityPlayer var1) {
        return true;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack item) {
        if (item == null) {
            return false;
        }
        switch (slot) {
            case 0:
                return item.getItem() == EnderIO.itemExtractSpeedUpgrade;
            case 1:
                return item.getItem() == EnderIO.itemFunctionUpgrade;
            case 2:
            case 3:
                return item.getItem() instanceof IItemFilterUpgrade;
        }
        return false;
    }
}
