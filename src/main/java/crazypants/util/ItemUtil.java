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
      return ItemUtil.doInsertItem((ISidedInventory) into, item, side);
    } else if(into instanceof IInventory) {
      return ItemUtil.doInsertItem(getInventory((IInventory) into), item);
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

  public static int doInsertItem(ISidedInventory sidedInv, ItemStack item, ForgeDirection inventorySide) {

    if(inventorySide == null) {
      inventorySide = ForgeDirection.UNKNOWN;
    }

    int[] slots = sidedInv.getAccessibleSlotsFromSide(inventorySide.ordinal());
    if(slots == null) {
      return 0;
    }
    int numInserted = 0;
    int numToInsert = item.stackSize;
    for (int i = 0; i < slots.length && numToInsert > 0; i++) {
      int slot = slots[i];
      if(sidedInv.canInsertItem(slot, item, inventorySide.ordinal())) {
        ItemStack contents = sidedInv.getStackInSlot(slot);
        ItemStack toInsert = item.copy();
        toInsert.stackSize = Math.min(toInsert.stackSize, sidedInv.getInventoryStackLimit());
        toInsert.stackSize = Math.min(toInsert.stackSize, toInsert.getMaxStackSize()); // some inventories like filing cap
        toInsert.stackSize = Math.min(toInsert.stackSize, numToInsert);
        int inserted = 0;
        if(contents == null) {
          inserted = toInsert.stackSize;
        } else {
          if(contents.isItemEqual(item) && ItemStack.areItemStackTagsEqual(contents, item)) {
            int space = sidedInv.getInventoryStackLimit() - contents.stackSize;
            space = Math.min(space, contents.getMaxStackSize() - contents.stackSize);
            inserted += Math.min(space, toInsert.stackSize);
            toInsert.stackSize = contents.stackSize + inserted;
          } else {
            toInsert.stackSize = 0;
          }
        }

        if(inserted > 0) {
          numInserted += inserted;
          numToInsert -= inserted;
          sidedInv.setInventorySlotContents(slot, toInsert);
        }
      }
    }
    if(numInserted > 0) {
      sidedInv.markDirty();
    }
    return numInserted;
  }

  public static int doInsertItem(IInventory inv, ItemStack item) {
    int numInserted = 0;
    int numToInsert = item.stackSize;
    for (int slot = 0; slot < inv.getSizeInventory() && numToInsert > 0; slot++) {
      ItemStack contents = inv.getStackInSlot(slot);
      if(!isStackFull(contents)) {
        ItemStack toInsert = item.copy();
        toInsert.stackSize = Math.min(toInsert.stackSize, inv.getInventoryStackLimit());
        toInsert.stackSize = Math.min(toInsert.stackSize, toInsert.getMaxDamage()); // some inventories like filing cap
        toInsert.stackSize = Math.min(toInsert.stackSize, numToInsert);
        int inserted = 0;
        if(contents == null) {
          inserted = toInsert.stackSize;
        } else {
          if(contents.isItemEqual(item) && ItemStack.areItemStackTagsEqual(contents, item)) {
            int space = inv.getInventoryStackLimit() - contents.stackSize;
            space = Math.min(space, contents.getMaxStackSize() - contents.stackSize);
            inserted += Math.min(space, toInsert.stackSize);
            toInsert.stackSize = contents.stackSize + inserted;
          } else {
            toInsert.stackSize = 0;
          }
        }
        if(!inv.isItemValidForSlot(slot, toInsert)) {
          inserted = 0;
        }

        if(inserted > 0) {
          numInserted += inserted;
          numToInsert -= inserted;
          inv.setInventorySlotContents(slot, toInsert);
        }
      }
    }
    if(numInserted > 0) {
      inv.markDirty();
    }
    return numInserted;
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

}
