package crazypants.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraftforge.common.util.ForgeDirection;
import cofh.api.transport.IItemDuct;
import cpw.mods.fml.common.Loader;
import crazypants.enderio.Log;

public class ItemUtil {

  public static final List<IItemReceptor> receptors = new ArrayList<IItemReceptor>();

  static {
    try {
      Class.forName("crazypants.util.BuildcraftUtil");
    } catch (Exception e) {
      if(Loader.isModLoaded("BuildCraft|Transport")) {
        Log.warn("ItemUtil: Could not register Build Craft pipe handler. Machines will not be able to output to BC pipes.");
      } //Don't log if BC isn't installed, but we still check in case another mod is using their API
    }
  }

  public static String getDurabilityString(ItemStack item) {
    if(item == null) {
      return null;
    }
    return Lang.localize("item.darkSteel.tooltip.durability") + " " + (item.getMaxDamage() - item.getItemDamage()) + "/" + item.getMaxDamage();
  }

  public static NBTTagCompound getOrCreateNBT(ItemStack stack) {
    if(stack.stackTagCompound == null) {
      stack.stackTagCompound = new NBTTagCompound();
    }
    return stack.stackTagCompound;
  }

  public static int doInsertItem(Object into, ItemStack item, ForgeDirection side) {
    if(into == null || item == null) {
      return 0;
    }
    if(into instanceof ISidedInventory) {
      return ItemUtil.doInsertItemInv((ISidedInventory) into, item, side);
    } else if(into instanceof IInventory) {
      return ItemUtil.doInsertItemInv(getInventory((IInventory) into), item, side);
    } else if(into instanceof IItemDuct) {
      return ItemUtil.doInsertItem((IItemDuct) into, item, side);
    }

    for (IItemReceptor rec : receptors) {
      if(rec.canInsertIntoObject(into, side)) {
        return rec.doInsertItem(into, item, side);
      }
    }

    return 0;
  }

  public static int doInsertItem(IItemDuct con, ItemStack item, ForgeDirection inventorySide) {
    int startedWith = item.stackSize;
    ItemStack remaining = con.insertItem(inventorySide, item);
    if(remaining == null) {
      return startedWith;
    }
    return startedWith - remaining.stackSize;
  }

  /*
   * Insert items into an IInventory or an ISidedInventory.
   */
  private static int doInsertItemInv(IInventory inv, ItemStack item, ForgeDirection inventorySide) {
    ISidedInventory sidedInv = inv instanceof ISidedInventory ? (ISidedInventory) inv : null;
    ISlotIterator slots;

    if (sidedInv != null) {
      if(inventorySide == null) {
        inventorySide = ForgeDirection.UNKNOWN;
      }
      slots = new sidedSlotter(sidedInv.getAccessibleSlotsFromSide(inventorySide.ordinal()));
    } else {
      slots = new invSlotter(inv.getSizeInventory());
    }
    
    int numInserted = 0;
    int numToInsert = item.stackSize;

    // PASS1: Try to add to an existing stack
    while (numToInsert > 0 && slots.hasNext()) {
      int slot = slots.nextSlot();
      if(sidedInv == null || sidedInv.canInsertItem(slot, item, inventorySide.ordinal())) {
        ItemStack contents = inv.getStackInSlot(slot);
        if(contents != null && areStackMergable(contents, item)) {
          int freeSpace = Math.min(inv.getInventoryStackLimit(), contents.getMaxStackSize()) - contents.stackSize; // some inventories like using itemstacks with invalid stack sizes
          if (freeSpace > 0) {
            int noToInsert = Math.min(numToInsert, freeSpace);
            ItemStack toInsert = item.copy();
            toInsert.stackSize = contents.stackSize + noToInsert;
            // isItemValidForSlot() may check the stacksize, so give it the number the stack would have in the end.
            // If it does something funny, like "only even numbers", we are screwed.
            if(sidedInv != null || inv.isItemValidForSlot(slot, toInsert)) {
              numInserted += noToInsert;
              numToInsert -= noToInsert;
              inv.setInventorySlotContents(slot, toInsert);
            }
          }
        }
      }
    }

    slots.reset();
    // PASS2: Try to insert into an empty slot
    while (numToInsert > 0 && slots.hasNext()) {
      int slot = slots.nextSlot();
      if(sidedInv == null || sidedInv.canInsertItem(slot, item, inventorySide.ordinal())) {
        ItemStack contents = inv.getStackInSlot(slot);
        if(contents == null) {
          ItemStack toInsert = item.copy();
          toInsert.stackSize = min(numToInsert, inv.getInventoryStackLimit(), toInsert.getMaxStackSize()); // some inventories like using itemstacks with invalid stack sizes
          if(sidedInv != null || inv.isItemValidForSlot(slot, toInsert)) {
            numInserted += toInsert.stackSize;
            numToInsert -= toInsert.stackSize;
            inv.setInventorySlotContents(slot, toInsert);
          }
        }
      }
    }

    if(numInserted > 0) {
      inv.markDirty();
    }
    return numInserted;
    
  }

  private static int min(int i1, int i2, int i3) {
    return i1 < i2 ? (i1 < i3 ? i1 : i3) : (i2 < i3 ? i2 : i3);
  }
  
  public static boolean isStackFull(ItemStack contents) {
    if(contents == null) {
      return false;
    }
    return contents.stackSize >= contents.getMaxStackSize();
  }

  public static IInventory getInventory(IInventory inv) {
    if(inv instanceof TileEntityChest) {
      TileEntityChest chest = (TileEntityChest) inv;
      TileEntityChest neighbour = null;
      if(chest.adjacentChestXNeg != null) {
        neighbour = chest.adjacentChestXNeg;
      } else if(chest.adjacentChestXPos != null) {
        neighbour = chest.adjacentChestXPos;
      } else if(chest.adjacentChestZNeg != null) {
        neighbour = chest.adjacentChestZNeg;
      } else if(chest.adjacentChestZPos != null) {
        neighbour = chest.adjacentChestZPos;
      }
      if(neighbour != null) {
        return new InventoryLargeChest("", inv, neighbour);
      }
      return inv;
    }
    return inv;
  }

  /**
   * Checks if items, damage and NBT are equal and the items are stackable.
   * 
   * @param s1
   * @param s2
   * @return
   */
  public static boolean areStackMergable(ItemStack s1, ItemStack s2) {
    if(s1 == null || s2 == null || !s1.isStackable() || !s2.isStackable()) {
      return false;
    }
    if(!s1.isItemEqual(s2)) {
      return false;
    }
    return ItemStack.areItemStackTagsEqual(s1, s2);
  }

  /**
   * Checks if items, damage and NBT are equal.
   * 
   * @param s1
   * @param s2
   * @return
   */
  public static boolean areStacksEqual(ItemStack s1, ItemStack s2) {
    if(s1 == null || s2 == null) {
      return false;
    }
    if(!s1.isItemEqual(s2)) {
      return false;
    }
    return ItemStack.areItemStackTagsEqual(s1, s2);
  }

  private interface ISlotIterator {
    int nextSlot();
    boolean hasNext();
    void reset();
  }
  
  private static class invSlotter implements ISlotIterator {
    final private int size;
    private int current;
    invSlotter(int size) {
      this.size = size;
      this.current = 0;
    }
    @Override
    public int nextSlot() {
      return current++;
    }
    @Override
    public void reset() {
      current = 0;
    }
    @Override
    public boolean hasNext() {
      return current < size;
    }
  }
  
  private static class sidedSlotter implements ISlotIterator {
    final private int[] slots;
    private int current;
    sidedSlotter(int[] slots) {
      this.slots = slots;
      this.current = 0;
    }
    @Override
    public int nextSlot() {
      return slots[current++];
    }
    @Override
    public void reset() {
      current = 0;
    }
    @Override
    public boolean hasNext() {
      return slots != null && current < slots.length;
    }
  }
  
}
