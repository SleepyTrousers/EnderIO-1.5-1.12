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

    addSlotToContainer(new Slot(te, te.getSlotDefinition().getMinUpgradeSlot(), 12, 60) {
      @Override
      public boolean isItemValid(ItemStack itemStack) {
        return tileEntity.isStackValidForSlot(tileEntity.getSlotDefinition().getMinUpgradeSlot(), itemStack);
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
    return tileEntity.isUseableByPlayer(entityplayer);
  }

  protected abstract void addMachineSlots(InventoryPlayer playerInv);

  @Override
  public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotIndex) {

    SlotDefinition slotDef = tileEntity.getSlotDefinition();

    int startPlayerSlot = slotDef.getNumSlots();
    int endPlayerSlot = startPlayerSlot + 26;
    int startHotBarSlot = endPlayerSlot + 1;
    int endHotBarSlot = startHotBarSlot + 9;

    ItemStack copystack = null;
    Slot slot = (Slot) inventorySlots.get(slotIndex);
    if (slot != null && slot.getHasStack()) {

      ItemStack origStack = slot.getStack();
      copystack = origStack.copy();

      if (slotDef.isInputSlot(slotIndex) || slotDef.isUpgradeSlot(slotIndex)) {
        // merge from machine input slots to inventory
        if (!mergeItemStack(origStack, startPlayerSlot, endHotBarSlot, false)) {
          return null;
        }

      } else if (slotDef.isOutputSlot(slotIndex)) {
        // merge result
        if (!mergeItemStack(origStack, startPlayerSlot, endHotBarSlot, true)) {
          return null;
        }
        slot.onSlotChange(origStack, copystack);

      } else {
        // Check from inv->input then inv->upgrade then inv->hotbar or
        // hotbar->inv
        if (slotIndex >= startPlayerSlot) {
          if (!tileEntity.isValidInput(origStack) || !mergeItemStack(origStack, slotDef.getMinInputSlot(), slotDef.getMaxInputSlot() + 1, false)) {
            if (!tileEntity.isValidUpgrade(origStack) || !mergeItemStack(origStack, slotDef.getMinUpgradeSlot(), slotDef.getMaxUpgradeSlot() + 1, false)) {
              if (slotIndex <= endPlayerSlot) {
                if (!mergeItemStack(origStack, startHotBarSlot, endHotBarSlot, false)) {
                  return null;
                }
              } else if (slotIndex >= startHotBarSlot && slotIndex <= endHotBarSlot) {
                if (!mergeItemStack(origStack, startPlayerSlot, endPlayerSlot, false)) {
                  return null;
                }
              }
            }
          }
        }
      }

      if (origStack.stackSize == 0) {
        slot.putStack((ItemStack) null);
      } else {
        slot.onSlotChanged();
      }

      slot.onSlotChanged();

      if (origStack.stackSize == copystack.stackSize) {
        return null;
      }

      slot.onPickupFromSlot(entityPlayer, origStack);
    }

    return copystack;
  }

}
