package crazypants.enderio.machine.alloy;

import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;

public class ContainerAlloySmelter extends Container {

  final TileAlloySmelter tileEntity;
  private int progress = 0;

  public ContainerAlloySmelter(InventoryPlayer playerInv, TileAlloySmelter te) {

    this.tileEntity = te;

    addSlotToContainer(new Slot(tileEntity, 0, 54, 17) {
      @Override
      public boolean isItemValid(ItemStack itemStack) {
        return tileEntity.isItemValidForSlot(0, itemStack);
      }      
    });
    addSlotToContainer(new Slot(tileEntity, 1, 78, 7) {
      @Override
      public boolean isItemValid(ItemStack itemStack) {
        return tileEntity.isItemValidForSlot(1, itemStack);
      }
    });
    addSlotToContainer(new Slot(tileEntity, 2, 103, 17) {
      @Override
      public boolean isItemValid(ItemStack itemStack) {
        return tileEntity.isItemValidForSlot(2, itemStack);
      }
    });
    addSlotToContainer(new Slot(tileEntity, 3, 79, 57) {
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

      if (slotIndex == 3) {
        
        if (!mergeItemStack(origStack, 4, 39, true)) {
          return null;
        }
        slot.onSlotChange(origStack, itemstack);
        
      } else if (slotIndex != 1 && slotIndex != 0 && slotIndex != 2) {
        
        boolean merged = false;
       
        if (tileEntity.isItemValidForSlot(0, origStack)) {
          merged = mergeItemStack(origStack, 0, 1, false);                
        } 
        if (!merged && tileEntity.isItemValidForSlot(1, origStack)) {
          merged = mergeItemStack(origStack, 1, 2, false);
        } 
        if (!merged && tileEntity.isItemValidForSlot(2, origStack)) {
          merged = mergeItemStack(origStack, 2, 3, false);
        } else if (!merged && slotIndex >= 4 && slotIndex < 30) {
          if (!this.mergeItemStack(origStack, 31, 40, false)) {
            return null;
          }
        } else if (!merged && slotIndex >= 31 && slotIndex < 40 && !this.mergeItemStack(origStack, 4, 31, false)) {
          return null;
        }
        
      } else if (!mergeItemStack(origStack, 4, 39, false)) {
        return null;
      }

      if (origStack.stackSize == 0) {
        slot.putStack((ItemStack) null);        
      } 
      slot.onSlotChanged();      

      if (origStack.stackSize == itemstack.stackSize) {
        return null;
      }

      slot.onPickupFromSlot(entityPlayer, origStack);
    }

    return itemstack;
  }
 


}
