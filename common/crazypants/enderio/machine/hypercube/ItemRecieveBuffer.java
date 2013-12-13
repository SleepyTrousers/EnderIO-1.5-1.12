package crazypants.enderio.machine.hypercube;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import crazypants.util.ArrayInventory;

public class ItemRecieveBuffer extends ArrayInventory implements ISidedInventory {

  public ItemRecieveBuffer() {
    super(6);
  }

  public boolean isEmpty() {
    for (ItemStack stack : items) {
      if(stack != null) {
        return false;
      }
    }
    return true;
  }

  @Override
  public int[] getAccessibleSlotsFromSide(int side) {
    if(side < 0 || side >= items.length) {
      return new int[0];
    }
    return new int[] { side };
  }

  @Override
  public boolean canInsertItem(int slot, ItemStack itemStack, int side) {
    if(side < 0 || side >= items.length || slot != side || itemStack == null) {
      return false;
    }
    ItemStack item = items[slot];
    if(item == null) {
      return true;
    }
    if(item.isItemEqual(itemStack) && ItemStack.areItemStackTagsEqual(item, itemStack) && item.stackSize < item.getMaxStackSize()) {
      return true;
    }
    return false;
  }

  @Override
  public boolean canExtractItem(int slot, ItemStack itemStack, int side) {
    if(slot != side) {
      return false;
    }
    return true;
  }

  public void readFromNBT(NBTTagCompound nbtRoot) {
    items = new ItemStack[6];
    for (int i = 0; i < items.length; i++) {
      String key = "recieveBuffer" + i;
      if(nbtRoot.hasKey(key)) {
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
      if(stack != null) {
        NBTTagCompound stackRoot = new NBTTagCompound();
        stack.writeToNBT(stackRoot);
        nbtRoot.setCompoundTag("recieveBuffer" + i, stackRoot);
      }
    }

  }

}
