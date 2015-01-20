package crazypants.enderio.machine.capbank.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import cofh.api.energy.IEnergyContainerItem;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.capbank.TileCapBank;

public class InventoryImpl implements IInventory {

  public static boolean isInventoryEmtpy(TileCapBank cap) {
    for (ItemStack st : cap.getInventory()) {
      if(st != null) {
        return false;
      }
    }
    return true;
  }

  public static boolean isInventoryEmtpy(ItemStack[] inv) {
    if(inv == null) {
      return true;
    }
    for (ItemStack st : inv) {
      if(st != null) {
        return false;
      }
    }
    return true;
  }

  private ItemStack[] inventory;

  private TileCapBank capBank;

  public InventoryImpl() {
  }

  public TileCapBank getCapBank() {
    return capBank;
  }

  public void setCapBank(TileCapBank cap) {
    capBank = cap;
    if(cap == null) {
      inventory = null;
      return;
    }
    inventory = cap.getInventory();
  }

  public boolean isEmtpy() {
    return isInventoryEmtpy(inventory);
  }

  public ItemStack[] getStacks() {
    return inventory;
  }

  @Override
  public ItemStack getStackInSlot(int slot) {
    if(inventory == null) {
      return null;
    }
    if(slot < 0 || slot >= inventory.length) {
      return null;
    }
    return inventory[slot];
  }

  @Override
  public ItemStack decrStackSize(int fromSlot, int amount) {
    if(inventory == null) {
      return null;
    }

    if(fromSlot < 0 || fromSlot >= inventory.length) {
      return null;
    }
    ItemStack item = inventory[fromSlot];
    if(item == null) {
      return null;
    }
    if(item.stackSize <= amount) {
      ItemStack result = item.copy();
      inventory[fromSlot] = null;
      return result;
    }
    item.stackSize -= amount;
    return item.copy();
  }

  @Override
  public void setInventorySlotContents(int slot, ItemStack itemstack) {
    if(inventory == null) {
      return;
    }
    if(slot < 0 || slot >= inventory.length) {
      return;
    }
    inventory[slot] = itemstack;
  }

  @Override
  public int getSizeInventory() {
    return 4;
  }

  //--- constant values

  @Override
  public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
    return null;
  }

  @Override
  public String getInventoryName() {
    return EnderIO.blockCapBank.getUnlocalizedName() + ".name";
  }

  @Override
  public boolean hasCustomInventoryName() {
    return false;
  }

  @Override
  public int getInventoryStackLimit() {
    return 1;
  }

  @Override
  public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
    return true;
  }

  @Override
  public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
    if(itemstack == null) {
      return false;
    }
    return itemstack.getItem() instanceof IEnergyContainerItem;
  }

  @Override
  public void openInventory() {
  }

  @Override
  public void closeInventory() {
  }

  @Override
  public void markDirty() {
  }

}
