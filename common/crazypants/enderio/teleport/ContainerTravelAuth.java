package crazypants.enderio.teleport;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import crazypants.gui.TemplateSlot;
import crazypants.util.ArrayInventory;

public class ContainerTravelAuth extends Container {

  ItemStack[] enteredPassword = new ItemStack[5];

  boolean dirty = false;

  public ContainerTravelAuth(InventoryPlayer playerInv) {

    ArrayInventory arrInv = new ArrayInventory(enteredPassword) {

      @Override
      public void onInventoryChanged() {
        super.onInventoryChanged();
        dirty = true;
      }

      @Override
      public int getSizeInventory() {
        return items.length;
      }

      @Override
      public ItemStack getStackInSlot(int i) {
        if(i < 0 || i >= items.length) {
          return null;
        }
        return items[i];
      }

      @Override
      public ItemStack decrStackSize(int fromSlot, int amount) {
        ItemStack item = items[fromSlot];
        items[fromSlot] = null;
        if(item == null) {
          return null;
        }
        item.stackSize = 0;
        return item;
      }

      @Override
      public ItemStack getStackInSlotOnClosing(int i) {
        return null;
      }

      @Override
      public void setInventorySlotContents(int i, ItemStack itemstack) {
        if(itemstack != null) {
          items[i] = itemstack.copy();
          items[i].stackSize = 0;
        } else {
          items[i] = null;
        }
      }

      @Override
      public String getInvName() {
        return "Password";
      }

      @Override
      public boolean isInvNameLocalized() {
        return true;
      }

      @Override
      public int getInventoryStackLimit() {
        return 0;
      }

    };
    int x = 44;
    int y = 28;
    for (int i = 0; i < 5; i++) {
      addSlotToContainer(new TemplateSlot(arrInv, i, x, y));
      x += 18;
    }

    // add players inventory
    for (int i = 0; i < 3; ++i) {
      for (int j = 0; j < 9; ++j) {
        addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 83 + i * 18));
      }
    }

    for (int i = 0; i < 9; ++i) {
      addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 141));
    }

  }

  @Override
  public boolean canInteractWith(EntityPlayer entityplayer) {
    return true;
  }

  @Override
  public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
    return null;
  }

}
