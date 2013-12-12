package crazypants.enderio.machine.hypercube;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import crazypants.enderio.ModObject;
import crazypants.util.InventoryWrapper;

public class CompositeInventory implements ISidedInventory {

  private final List<InvEntry> inventories = new ArrayList<InvEntry>();
  private int size = 0;

  public void addInventory(IInventory inv) {
    if(inv == null) {
      return;
    }
    inventories.add(new InvEntry(InventoryWrapper.asSidedInventory(inv), size));
    size += inv.getSizeInventory();
  }

  public void removeInventory(IInventory inv) {
    if(inv == null) {
      return;
    }
    inventories.remove(inv);
    updateSize();
  }

  private void updateSize() {
    size = 0;
    for (InvEntry inv : inventories) {
      if(inv != null && inv.inv != null) {
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
      if(inv.containsSlot(i)) {
        return inv.inv.getStackInSlot(inv.getSlot(i));
      }
    }
    return null;
  }

  @Override
  public ItemStack decrStackSize(int slot, int j) {
    for (InvEntry inv : inventories) {
      if(inv.containsSlot(slot)) {
        return inv.inv.decrStackSize(inv.getSlot(slot), j);
      }
    }
    return null;
  }

  @Override
  public void setInventorySlotContents(int slot, ItemStack itemStack) {
    for (InvEntry inv : inventories) {
      if(inv.containsSlot(slot)) {
        inv.inv.setInventorySlotContents(inv.getSlot(slot), itemStack);
        return;
      }
    }

  }

  @Override
  public int[] getAccessibleSlotsFromSide(int var1) {
    List<Integer> resList = new ArrayList<Integer>();
    for (InvEntry inv : inventories) {
      int[] slots = inv.inv.getAccessibleSlotsFromSide(var1);
      if(slots != null) {
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
      if(inv != null && inv.containsSlot(slot)) {
        return inv.inv.canInsertItem(inv.getSlot(slot), item, side);
      }
    }
    return false;
  }

  @Override
  public boolean canExtractItem(int slot, ItemStack item, int side) {
    for (InvEntry inv : inventories) {
      if(inv != null && inv.containsSlot(slot)) {
        return inv.inv.canExtractItem(inv.getSlot(slot), item, side);
      }
    }
    return false;
  }

  @Override
  public boolean isItemValidForSlot(int slot, ItemStack item) {
    for (InvEntry inv : inventories) {
      if(inv != null && inv.containsSlot(slot)) {
        return inv.inv.isItemValidForSlot(inv.getSlot(slot), item);
      }
    }
    return false;
  }

  @Override
  public void onInventoryChanged() {
    for (InvEntry inv : inventories) {
      if(inv != null) {
        inv.inv.onInventoryChanged();
      }
    }
  }

  //---------------- Inventory

  @Override
  public String getInvName() {
    return ModObject.blockHyperCube.name;
  }

  @Override
  public boolean isInvNameLocalized() {
    return false;
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
  public void openChest() {
  }

  @Override
  public void closeChest() {
  }

  @Override
  public ItemStack getStackInSlotOnClosing(int i) {
    return null;
  }

  private static class InvEntry {
    ISidedInventory inv;
    int startIndex;
    int endIndex;

    InvEntry(ISidedInventory inv, int startIndex) {
      this.inv = inv;
      this.endIndex = startIndex + inv.getSizeInventory() - 1;
    }

    boolean containsSlot(int index) {
      return index >= startIndex && index <= endIndex;
    }

    int getSlot(int index) {
      return index - startIndex;
    }

  }

}
