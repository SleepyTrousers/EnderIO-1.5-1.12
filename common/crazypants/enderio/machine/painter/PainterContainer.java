package crazypants.enderio.machine.painter;

import crazypants.enderio.machine.AbstractMachineEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class PainterContainer extends Container {

  final AbstractMachineEntity tileEntity;
  private int progress = 0;

  public PainterContainer(InventoryPlayer playerInv, AbstractMachineEntity te) {

    this.tileEntity = te;

    addSlotToContainer(new Slot(tileEntity, 0, 67, 34) {
      @Override
      public boolean isItemValid(ItemStack itemStack) {
        return tileEntity.isItemValidForSlot(0, itemStack);
      }      
    });
    addSlotToContainer(new Slot(tileEntity, 1, 37, 34) {
      @Override
      public boolean isItemValid(ItemStack itemStack) {
        return tileEntity.isItemValidForSlot(1, itemStack);
      }
    });
    addSlotToContainer(new Slot(tileEntity, 2, 121, 34) {
      @Override
      public boolean isItemValid(ItemStack par1ItemStack) {
        return false;
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

      if (slotIndex == 2) {
        
        if (!mergeItemStack(origStack, 3, 39, true)) {
          return null;
        }
        slot.onSlotChange(origStack, itemstack);
        
      } else if (slotIndex != 1 && slotIndex != 0) {
        
        if (tileEntity.isItemValidForSlot(0, origStack)) {
          if (!this.mergeItemStack(origStack, 0, 1, false)) {
            return null;
          }       
        } else if (tileEntity.isItemValidForSlot(1, origStack)) {
          if (!this.mergeItemStack(origStack, 1, 2, false)) {
            return null;
          }
        } else if (slotIndex >= 3 && slotIndex < 30) {
          if (!this.mergeItemStack(origStack, 30, 39, false)) {
            return null;
          }
        } else if (slotIndex >= 30 && slotIndex < 39 && !this.mergeItemStack(origStack, 3, 30, false)) {
          return null;
        }
        
      } else if (!mergeItemStack(origStack, 3, 39, false)) {
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
