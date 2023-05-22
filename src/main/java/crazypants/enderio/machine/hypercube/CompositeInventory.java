package crazypants.enderio.machine.hypercube;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.common.util.InventoryWrapper;

import crazypants.enderio.ModObject;

public class CompositeInventory implements ISidedInventory {

    private final List<InvEntry> inventories = new ArrayList<InvEntry>();
    private int size = 0;

    public void addInventory(CompositeInventory inv) {
        for (InvEntry ie : inv.inventories) {
            addInventory(ie.inv, ie.side);
        }
    }

    public void addInventory(IInventory inv, ForgeDirection side) {
        if (inv == null) {
            return;
        }
        inventories.add(new InvEntry(inv, size, side));
        updateSize();
    }

    public void removeInventory(IInventory inv) {
        if (inv == null) {
            return;
        }
        InvEntry remove = null;
        for (InvEntry ie : inventories) {
            if (ie.inv == inv || ie.origInv == inv) {
                remove = ie;
                break;
            }
        }
        if (remove != null) {
            inventories.remove(remove);
            updateSize();
        }
    }

    private void updateSize() {
        size = 0;
        for (InvEntry inv : inventories) {
            if (inv != null && inv.inv != null) {
                inv.startIndex = size;
                inv.endIndex = inv.startIndex + inv.inv.getSizeInventory() - 1;
                size += inv.inv.getSizeInventory();
            }
        }
    }

    @Override
    public int getSizeInventory() {
        return size;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        for (InvEntry inv : inventories) {
            if (inv.containsSlot(i)) {
                return inv.inv.getStackInSlot(inv.getSlot(i));
            }
        }
        return null;
    }

    @Override
    public ItemStack decrStackSize(int slot, int j) {
        for (InvEntry inv : inventories) {
            if (inv.containsSlot(slot)) {
                return inv.inv.decrStackSize(inv.getSlot(slot), j);
            }
        }
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack itemStack) {
        for (InvEntry inv : inventories) {
            if (inv.containsSlot(slot)) {
                inv.inv.setInventorySlotContents(inv.getSlot(slot), itemStack);
                return;
            }
        }
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int var1) {
        List<Integer> resList = new ArrayList<Integer>();
        for (InvEntry inv : inventories) {
            int[] slots = inv.inv.getAccessibleSlotsFromSide(inv.side.ordinal());
            if (slots != null) {
                for (int i = 0; i < slots.length; i++) {
                    resList.add(slots[i]);
                }
            }
        }

        int[] result = new int[resList.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = resList.get(i);
        }
        return result;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack item, int side) {
        for (InvEntry inv : inventories) {
            if (inv != null && inv.containsSlot(slot)) {
                return inv.inv.canInsertItem(inv.getSlot(slot), item, side);
            }
        }
        return false;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack item, int side) {
        for (InvEntry inv : inventories) {
            if (inv != null && inv.containsSlot(slot)) {
                return inv.inv.canExtractItem(inv.getSlot(slot), item, side);
            }
        }
        return false;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack item) {
        for (InvEntry inv : inventories) {
            if (inv != null && inv.containsSlot(slot)) {
                return inv.inv.isItemValidForSlot(inv.getSlot(slot), item);
            }
        }
        return false;
    }

    @Override
    public void markDirty() {
        for (InvEntry inv : inventories) {
            if (inv != null) {
                inv.inv.markDirty();
            }
        }
    }

    // ---------------- Inventory

    @Override
    public String getInventoryName() {
        return ModObject.blockHyperCube.unlocalisedName;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer) {
        return false;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public ItemStack getStackInSlotOnClosing(int i) {
        return null;
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    private static class InvEntry {

        IInventory origInv;
        ISidedInventory inv;
        int startIndex;
        int endIndex;
        ForgeDirection side;

        InvEntry(IInventory inventory, int startIndex, ForgeDirection side) {
            this.origInv = inventory;
            inv = InventoryWrapper.asSidedInventory(inventory);
            this.endIndex = startIndex + inv.getSizeInventory() - 1;
            this.side = side;
        }

        boolean containsSlot(int index) {
            return index >= startIndex && index <= endIndex;
        }

        int getSlot(int index) {
            return index - startIndex;
        }
    }
}
