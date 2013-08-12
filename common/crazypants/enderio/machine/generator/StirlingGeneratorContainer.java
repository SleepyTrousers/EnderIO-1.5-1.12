package crazypants.enderio.machine.generator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class StirlingGeneratorContainer extends Container {

  final TileEntityStirlingGenerator tileEntity;
  private int progress = 0;

  public StirlingGeneratorContainer(InventoryPlayer playerInv, TileEntityStirlingGenerator te) {

    this.tileEntity = te;

    addSlotToContainer(new Slot(tileEntity, 0, 80, 34) {
      @Override
      public boolean isItemValid(ItemStack itemStack) {
        return tileEntity.isItemValidForSlot(0, itemStack);
      }      
    });    
    
    for (int i = 0; i < 3; ++i) {
      for (int j = 0; j < 9; ++j) {
        addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
      }
    }

    for (int i = 0; i < 9; ++i) {
      addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 142));
    }    

  }

  @Override
  public boolean canInteractWith(EntityPlayer entityplayer) {
    return tileEntity.isUseableByPlayer(entityplayer);
  }

  
  @Override
  public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotIndex) {
    ItemStack itemstack = null;
    Slot slot = (Slot) inventorySlots.get(slotIndex);    
    if (slot != null && slot.getHasStack()) {
      ItemStack origStack = slot.getStack();
      itemstack = origStack.copy();

      if (slotIndex != 0) {        
        if (tileEntity.isItemValidForSlot(0, origStack)) {
          if (!this.mergeItemStack(origStack, 0, 1, false)) {
            return null;
          }               
        } else if (slotIndex >= 30 && slotIndex < 39 && !this.mergeItemStack(origStack, 1, 28, false)) {
          return null;
        }
        
      } else if (!mergeItemStack(origStack, 1, 37, false)) {
        return null;
      }

      if (origStack.stackSize == 0) {
        slot.putStack((ItemStack) null);
      } else {
        slot.onSlotChanged();
      }

      if (origStack.stackSize == itemstack.stackSize) {
        return null;
      }

      slot.onPickupFromSlot(entityPlayer, origStack);
    }

    return itemstack;
  }
 

}
