package crazypants.enderio.machine.power;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerCapacitorBank extends Container {

  private final TileCapacitorBank tileEntity;

  public ContainerCapacitorBank(InventoryPlayer playerInv, TileCapacitorBank te) {

    tileEntity = te;

    addSlotToContainer(new Slot(tileEntity, 0, 59, 59) {
      @Override
      public boolean isItemValid(ItemStack itemStack) {
        return tileEntity.isItemValidForSlot(0, itemStack);
      }
    });

    addSlotToContainer(new Slot(tileEntity, 1, 79, 59) {
      @Override
      public boolean isItemValid(ItemStack itemStack) {
        return tileEntity.isItemValidForSlot(1, itemStack);
      }
    });

    addSlotToContainer(new Slot(tileEntity, 2, 99, 59) {
      @Override
      public boolean isItemValid(ItemStack itemStack) {
        return tileEntity.isItemValidForSlot(2, itemStack);
      }
    });

    addSlotToContainer(new Slot(tileEntity, 3, 119, 59) {
      @Override
      public boolean isItemValid(ItemStack itemStack) {
        return tileEntity.isItemValidForSlot(3, itemStack);
      }
    });

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
    return true;
  }

  @Override
  public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotIndex) {

    int startPlayerSlot = 4;
    int endPlayerSlot = startPlayerSlot + 26;
    int startHotBarSlot = endPlayerSlot + 1;
    int endHotBarSlot = startHotBarSlot + 9;

    ItemStack copystack = null;
    Slot slot = (Slot) inventorySlots.get(slotIndex);
    if(slot != null && slot.getHasStack()) {

      ItemStack origStack = slot.getStack();
      copystack = origStack.copy();

      if(slotIndex < 4) {
        // merge from machine input slots to inventory
        if(!mergeItemStack(origStack, startPlayerSlot, endHotBarSlot, false)) {
          return null;
        }

      } else {
        //Check from inv-> charge then inv->hotbar or hotbar->inv
        if(slotIndex >= startPlayerSlot) {
          if(!tileEntity.isItemValidForSlot(0, origStack) || !mergeItemStack(origStack, 0, 4, false)) {

            if(slotIndex <= endPlayerSlot) {
              if(!mergeItemStack(origStack, startHotBarSlot, endHotBarSlot, false)) {
                return null;
              }
            } else if(slotIndex >= startHotBarSlot && slotIndex <= endHotBarSlot) {
              if(!mergeItemStack(origStack, startPlayerSlot, endPlayerSlot, false)) {
                return null;
              }
            }

          }
        }
      }

      if(origStack.stackSize == 0) {
        slot.putStack((ItemStack) null);
      } else {
        slot.onSlotChanged();
      }

      slot.onSlotChanged();

      if(origStack.stackSize == copystack.stackSize) {
        return null;
      }

      slot.onPickupFromSlot(entityPlayer, origStack);
    }

    return copystack;
  }

}
