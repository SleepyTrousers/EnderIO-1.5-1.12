package crazypants.enderio.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.ItemUtil;
import com.google.common.base.Predicate;

import crazypants.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.IItemHandler;

//TODO 1.11 - use ec version
public class InventorySlot implements IItemHandler {

  private ItemStack itemStack = Prep.getEmpty();
  private final @Nonnull Predicate<ItemStack> filterIn, filterOut;
  private final @Nonnull Callback<ItemStack> callback;
  private final int limit;
  private @Nullable TileEntity owner;

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
    this.limit = limit > 0 ? limit : 64;
  }

  @Override
  public int getSlots() {
    return 1;
  }

  @Override
  public ItemStack getStackInSlot(int slot) {
    return slot == 0 ? itemStack : Prep.getEmpty();
  }

  public boolean isItemValidForSlot(ItemStack stack) {
    return Prep.isValid(stack) && filterIn.apply(stack);
  }

  @Override
  public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
    if (Prep.isInvalid(stack)) {
      return Prep.getEmpty();
    }
    if (slot == 0 && filterIn.apply(stack)) {
      if (Prep.isInvalid(itemStack)) {
        int max = Math.min(limit, stack.getMaxStackSize());
        if (!simulate) {
          itemStack = stack.copy();
        }
        if (stack.stackSize <= max) {
          if (!simulate) {
            onChange(Prep.getEmpty(), itemStack);
          }
          return Prep.getEmpty();
        }
        if (!simulate) {
          itemStack.stackSize = max;
          onChange(Prep.getEmpty(), itemStack);
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
            onChange(oldStack, itemStack);
          }
          return Prep.getEmpty();
        }
        int tomove = max - itemStack.stackSize;
        if (!simulate) {
          ItemStack oldStack = itemStack.copy();
          itemStack.stackSize = max;
          onChange(oldStack, itemStack);
        }
        ItemStack result = stack.copy();
        result.stackSize -= tomove;
        return result;
      }
    }
    return stack;
  }

  @Override
  public ItemStack extractItem(int slot, int amount, boolean simulate) {
    if (slot == 0 && Prep.isValid(itemStack) && filterOut.apply(itemStack)) {
      if (amount >= itemStack.stackSize) {
        if (!simulate) {
          ItemStack oldStack = itemStack;
          itemStack = Prep.getEmpty();
          ;
          onChange(oldStack, itemStack);
          return oldStack;
        } else {
          return itemStack.copy();
        }
      } else {
        if (!simulate) {
          ItemStack oldStack = itemStack.copy();
          itemStack.stackSize -= amount;
          onChange(oldStack, itemStack);
          oldStack.stackSize = amount;
          return oldStack;
        } else {
          ItemStack result = itemStack.copy();
          result.stackSize = amount;
          return result;
        }
      }
    }
    return Prep.getEmpty();
  }

  private void onChange(ItemStack oldStack, ItemStack newStack) {
    callback.onChange(oldStack, newStack);
    if (owner != null) {
      owner.markDirty();
    }
  }

  public void writeToNBT(NBTTagCompound tag) {
    if (Prep.isValid(itemStack)) {
      itemStack.writeToNBT(tag);
    }
  }

  public void readFromNBT(NBTTagCompound tag) {
    itemStack = new ItemStack(tag);
  }

  public void clear() {
    itemStack = Prep.getEmpty();
  }

  public void set(ItemStack stack) {
    itemStack = stack;
  }

  public int getMaxStackSize() {
    return limit;
  }

  void setOwner(TileEntity owner) {
    this.owner = owner;
  }

  TileEntity getOwner() {
    return owner;
  }

}
