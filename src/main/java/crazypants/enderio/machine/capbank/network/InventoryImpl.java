package crazypants.enderio.machine.capbank.network;

import com.enderio.core.common.util.Util;
import crazypants.enderio.machine.capbank.TileCapBank;
import crazypants.enderio.power.PowerHandlerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;

import static crazypants.enderio.ModObject.blockCapBank;

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
  public ItemStack decrStackSize(int slot, int amount) {
    return Util.decrStackSize(this, slot, amount);
  }

  @Override
  public void setInventorySlotContents(int slot, @Nullable ItemStack itemstack) {
    if(inventory == null) {
      return;
    }
    if(slot < 0 || slot >= inventory.length) {
      return;
    }
    inventory[slot] = itemstack;
  }

  @Override
  public ItemStack removeStackFromSlot(int index) {
    if(inventory == null) {
      return null;
    }
    ItemStack res = getStackInSlot(index);
    setInventorySlotContents(index, null);
    return res;
  }

  @Override
  public void clear() {
    if(inventory == null) {
      return;
    }
    for(int i=0;i<inventory.length;i++) {
      inventory[i] = null;
    }
  }
  
  @Override
  public int getSizeInventory() {
    return 4;
  }

  //--- constant values

  @Override
  public String getName() {
    return blockCapBank.getBlock().getUnlocalizedName() + ".name";
  }

  @Override
  public boolean hasCustomName() {
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
    if(itemStack.isEmpty()) {
      return false;
    }
    return PowerHandlerUtil.getCapability(itemstack, null) != null;
  }

  @Override
  public void openInventory(EntityPlayer e) {
  }

  @Override
  public void closeInventory(EntityPlayer e) {
  }

  @Override
  public void markDirty() {
    if (capBank != null) {
      capBank.markDirty();
    }
  }

  @Override
  public ITextComponent getDisplayName() {
    return hasCustomName() ? new TextComponentString(getName()) : new TextComponentTranslation(getName(), new Object[0]);
  }

  @Override
  public int getField(int id) {
    return 0;
  }

  @Override
  public void setField(int id, int value) {
  }

  @Override
  public int getFieldCount() {
    return 0;
  }

  

}
