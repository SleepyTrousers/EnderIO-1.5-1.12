package crazypants.enderio.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public abstract class AbstractMachineContainer extends Container {

  protected final AbstractMachineEntity tileEntity;

  public AbstractMachineContainer(InventoryPlayer playerInv, AbstractMachineEntity te) {
    this.tileEntity = te;

    addMachineSlots(playerInv);

    // add players inventory
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

  protected abstract void addMachineSlots(InventoryPlayer playerInv);

  @Override
  public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotIndex) {
    boolean hasOutput = tileEntity.getSizeInventory() > 1;
    int outputSlot = tileEntity.getSizeInventory() - 1;    
    int startPlayerSlot = outputSlot + 1;
    int endPlayerSlot = startPlayerSlot + 26;
    int startHotBarSlot = endPlayerSlot + 1;
    int endHotBarSlot = startHotBarSlot + 9;

    ItemStack copystack = null;
    Slot slot = (Slot) inventorySlots.get(slotIndex);
    if (slot != null && slot.getHasStack()) {

      ItemStack origStack = slot.getStack();
      copystack = origStack.copy();

      if (hasOutput ? (slotIndex < outputSlot) : (slotIndex <= outputSlot)) {
        //merge from machine input slots to inventory
        if (!mergeItemStack(origStack, startPlayerSlot, endHotBarSlot, false)) {
          return null;
        }
        
      } else if (hasOutput && slotIndex == outputSlot) { // merge result

        if (!mergeItemStack(origStack, startPlayerSlot, endHotBarSlot, true)) {
          return null;
        }

      } else /* if (slotIndex > outputSlot) */{ // from inventory into inputs

        boolean merged = false;
        for (int i = 0; (hasOutput ? (i < outputSlot) : (i <= outputSlot)) && !merged; i++) {
          if (tileEntity.isItemValidForSlot(i, origStack)) {
            merged = mergeItemStack(origStack, i, i + 1, false);
          }
        }

        // If we cant merge into the machine, see if we can merge stacks within
        // the inventory
        if (!merged) {

          if ((hasOutput ? (slotIndex < outputSlot) : (slotIndex <= outputSlot)) && slotIndex < startHotBarSlot) {
            // merge into hotbar
            if (!mergeItemStack(origStack, startHotBarSlot, endHotBarSlot, false)) {              
              return null;
            } 
            
            //merge from hotbar
          } else if (!mergeItemStack(origStack, startPlayerSlot, endPlayerSlot, false)) {
            return null;
          }
        }

      }      

      if (origStack.stackSize == 0) {
        slot.putStack((ItemStack) null);
      } else {
        slot.onSlotChanged();
      }

      if (origStack.stackSize == copystack.stackSize) {
        return null;
      }

      slot.onPickupFromSlot(entityPlayer, origStack);
    }

    return copystack;
  }

}
