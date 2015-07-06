package crazypants.enderio.machine.gui;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public abstract class ContainerEIO extends Container {

  /**
   * Added validation of slot input
   */
  @Override
  protected boolean mergeItemStack(ItemStack par1ItemStack, int fromIndex, int toIndex, boolean reversOrder) {

    boolean result = false;
    int checkIndex = fromIndex;

    if(reversOrder) {
      checkIndex = toIndex - 1;
    }

    Slot slot;
    ItemStack itemstack1;

    if(par1ItemStack.isStackable()) {

      while (par1ItemStack.stackSize > 0 && (!reversOrder && checkIndex < toIndex || reversOrder && checkIndex >= fromIndex)) {
        slot = (Slot) this.inventorySlots.get(checkIndex);
        itemstack1 = slot.getStack();

        if (itemstack1 != null && itemstack1.getItem() == par1ItemStack.getItem()
            && (!par1ItemStack.getHasSubtypes() || par1ItemStack.getItemDamage() == itemstack1.getItemDamage())
            && ItemStack.areItemStackTagsEqual(par1ItemStack, itemstack1) && slot.isItemValid(par1ItemStack)
            && par1ItemStack != itemstack1) {

          int mergedSize = itemstack1.stackSize + par1ItemStack.stackSize;
          int maxStackSize =  Math.min(par1ItemStack.getMaxStackSize(), slot.getSlotStackLimit());
          if(mergedSize <= maxStackSize) {
            par1ItemStack.stackSize = 0;
            itemstack1.stackSize = mergedSize;
            slot.onSlotChanged();
            result = true;
          } else if(itemstack1.stackSize < maxStackSize) {
            par1ItemStack.stackSize -= maxStackSize - itemstack1.stackSize;
            itemstack1.stackSize = maxStackSize;
            slot.onSlotChanged();
            result = true;
          }
        }

        if(reversOrder) {
          --checkIndex;
        } else {
          ++checkIndex;
        }
      }
    }

    if(par1ItemStack.stackSize > 0) {
      if(reversOrder) {
        checkIndex = toIndex - 1;
      } else {
        checkIndex = fromIndex;
      }

      while (!reversOrder && checkIndex < toIndex || reversOrder && checkIndex >= fromIndex) {
        slot = (Slot) this.inventorySlots.get(checkIndex);
        itemstack1 = slot.getStack();

        if(itemstack1 == null && slot.isItemValid(par1ItemStack)) {
          ItemStack in = par1ItemStack.copy();
          in.stackSize = Math.min(in.stackSize, slot.getSlotStackLimit());

          slot.putStack(in);
          slot.onSlotChanged();
          if(in.stackSize >= par1ItemStack.stackSize) {
            par1ItemStack.stackSize = 0;
          } else {
            par1ItemStack.stackSize -= in.stackSize;
          }
          result = true;
          break;
        }

        if(reversOrder) {
          --checkIndex;
        } else {
          ++checkIndex;
        }
      }
    }

    return result;
  }

}
