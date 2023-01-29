package crazypants.enderio.machine.hypercube;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.enderio.core.common.util.ArrayInventory;

public class ItemRecieveBuffer extends ArrayInventory implements ISidedInventory {

    private static final int[] ALL_SLOTS = new int[] { 0, 1, 2, 3, 4, 5 };
    private boolean recieveEnabled;

    TileHyperCube hc;

    public ItemRecieveBuffer(TileHyperCube hc) {
        super(6);
        this.hc = hc;
    }

    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (stack != null) {
                return false;
            }
        }
        return true;
    }

    public ItemStack[] getItems() {
        return items;
    }

    public void setRecieveEnabled(boolean canRecieveItems) {
        recieveEnabled = canRecieveItems;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return ALL_SLOTS;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack itemStack, int side) {
        if (!recieveEnabled || side < 0 || side >= items.length || slot != side || itemStack == null) {
            return false;
        }
        ItemStack item = items[slot];
        if (item == null) {
            return true;
        }
        return false;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        super.setInventorySlotContents(slot, stack);
        hc.pushRecieveBuffer();
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack itemStack, int side) {
        return true;
    }

    public void readFromNBT(NBTTagCompound nbtRoot) {
        items = new ItemStack[6];
        for (int i = 0; i < items.length; i++) {
            String key = "recieveBuffer" + i;
            if (nbtRoot.hasKey(key)) {
                NBTTagCompound stackRoot = nbtRoot.getCompoundTag(key);
                items[i] = ItemStack.loadItemStackFromNBT(stackRoot);
            } else {
                items[i] = null;
            }
        }
    }

    public void writeToNBT(NBTTagCompound nbtRoot) {
        for (int i = 0; i < items.length; i++) {
            ItemStack stack = items[i];
            if (stack != null) {
                NBTTagCompound stackRoot = new NBTTagCompound();
                stack.writeToNBT(stackRoot);
                nbtRoot.setTag("recieveBuffer" + i, stackRoot);
            }
        }
    }
}
