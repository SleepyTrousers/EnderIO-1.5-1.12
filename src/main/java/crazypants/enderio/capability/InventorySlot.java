package crazypants.enderio.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandler;

import com.enderio.core.common.util.ItemUtil;
import com.google.common.base.Predicate;

public class InventorySlot implements IItemHandler {

  private ItemStack itemStack;
  private final Predicate<ItemStack> filterIn, filterOut;
  private final Callback<ItemStack> callback;
  private final int limit;

  public InventorySlot() {
    this(null, null, null, null, -1);
  }

  public InventorySlot(Callback<ItemStack> callback) {
    this(null, null, null, callback, -1);
  }

  public InventorySlot(ItemStack itemStack) {
    this(itemStack, null, null, null, -1);
  }

  public InventorySlot(ItemStack itemStack, Callback<ItemStack> callback) {
    this(itemStack, null, null, callback, -1);
  }

  public InventorySlot(Predicate<ItemStack> filterIn, Predicate<ItemStack> filterOut) {
    this(null, filterIn, filterOut, null, -1);
  }

  public InventorySlot(Predicate<ItemStack> filterIn, Predicate<ItemStack> filterOut, Callback<ItemStack> callback) {
    this(null, filterIn, filterOut, callback, -1);
  }

  public InventorySlot(int limit) {
    this(null, null, null, null, limit);
  }

  public InventorySlot(Callback<ItemStack> callback, int limit) {
    this(null, null, null, callback, limit);
  }

  public InventorySlot(ItemStack itemStack, int limit) {
    this(itemStack, null, null, null, limit);
  }

  public InventorySlot(ItemStack itemStack, Callback<ItemStack> callback, int limit) {
    this(itemStack, null, null, callback, limit);
  }

  public InventorySlot(Predicate<ItemStack> filterIn, Predicate<ItemStack> filterOut, int limit) {
    this(null, filterIn, filterOut, null, limit);
  }

  public InventorySlot(Predicate<ItemStack> filterIn, Predicate<ItemStack> filterOut, Callback<ItemStack> callback, int limit) {
    this(null, filterIn, filterOut, callback, limit);
  }

  public InventorySlot(ItemStack itemStack, Predicate<ItemStack> filterIn, Predicate<ItemStack> filterOut) {
    this(itemStack, filterIn, filterOut, null, -1);
  }

  public InventorySlot(ItemStack itemStack, Predicate<ItemStack> filterIn, Predicate<ItemStack> filterOut, Callback<ItemStack> callback) {
    this(itemStack, filterIn, filterOut, callback, -1);
  }

  public InventorySlot(ItemStack itemStack, Predicate<ItemStack> filterIn, Predicate<ItemStack> filterOut, int limit) {
    this(itemStack, filterIn, filterOut, null, limit);
  }

  public InventorySlot(ItemStack itemStack, Predicate<ItemStack> filterIn, Predicate<ItemStack> filterOut, Callback<ItemStack> callback, int limit) {
    this.itemStack = itemStack;
    this.filterIn = filterIn != null ? filterIn : Filters.ALWAYS_TRUE;
    this.filterOut = filterOut != null ? filterOut : Filters.ALWAYS_TRUE;
    this.callback = callback != null ? callback : Filters.NO_CALLBACK;
    this.limit = limit > 0 ? limit : Integer.MAX_VALUE;
  }

  @Override
  public int getSlots() {
    return 1;
  }

  @Override
  public ItemStack getStackInSlot(int slot) {
    return slot == 0 ? itemStack : null;
  }

  @Override
  public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
    if (slot == 0 && filterIn.apply(stack)) {
      if (itemStack == null) {
        int max = Math.min(limit, stack.getMaxStackSize());
        if (!simulate) {
          itemStack = stack.copy();
        }
        if (stack.stackSize <= max) {
          if (!simulate) {
            callback.onChange(null, itemStack);
          }
          return null;
        }
        if (!simulate) {
          itemStack.stackSize = max;
          callback.onChange(null, itemStack);
        }
        ItemStack result = stack.copy();
        result.stackSize -= max;
        return result;
      }
      if (ItemUtil.areStackMergable(itemStack, stack)) {
        int max = Math.min(limit, stack.getMaxStackSize());
        int target = itemStack.stackSize + stack.stackSize;
        if (target <= max) {
          if (!simulate) {
            ItemStack oldStack = itemStack.copy();
            itemStack.stackSize = target;
            callback.onChange(oldStack, itemStack);
          }
          return null;
        }
        if (!simulate) {
          ItemStack oldStack = itemStack.copy();
          itemStack.stackSize = max;
          callback.onChange(oldStack, itemStack);
        }
        ItemStack result = stack.copy();
        result.stackSize -= max - target;
        return result;
      }
    }
    return stack;
  }

  @Override
  public ItemStack extractItem(int slot, int amount, boolean simulate) {
    if (slot == 0 && itemStack != null && filterOut.apply(itemStack)) {
      ItemStack result = itemStack.copy();
      if (amount >= itemStack.stackSize) {
        if (!simulate) {
          callback.onChange(itemStack, null);
          itemStack = null;
        }
      } else {
        if (!simulate) {
          ItemStack oldStack = itemStack.copy();
          itemStack.stackSize -= amount;
          callback.onChange(oldStack, itemStack);
        }
        result.stackSize = amount;
      }
      return result;
    }
    return null;
  }

  public void writeToNBT(NBTTagCompound tag) {
    if (itemStack != null) {
      itemStack.writeToNBT(tag);
    }
  }

  public void readFromNBT(NBTTagCompound tag) {
    itemStack = ItemStack.loadItemStackFromNBT(tag);
  }

  public void clear() {
    itemStack = null;
  }

  public void set(ItemStack stack) {
    itemStack = stack;
  }

}
