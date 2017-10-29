package crazypants.enderio.machine.base.container;

import java.awt.Point;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.common.util.IProgressTile;
import com.enderio.core.common.ContainerEnderCap;
import com.enderio.core.common.inventory.EnderInventory;
import com.enderio.core.common.inventory.EnderInventory.Type;
import com.enderio.core.common.inventory.EnderInventory.View;
import com.enderio.core.common.inventory.EnderSlot;
import com.enderio.core.common.inventory.SlotPredicate;
import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.util.Util;

import crazypants.enderio.machine.base.te.AbstractCapabilityMachineEntity;
import crazypants.util.Prep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public abstract class AbstractCapabilityMachineContainer<E extends AbstractCapabilityMachineEntity> extends ContainerEnderCap<EnderInventory, E> {

  protected Slot upgradeSlot;

  public AbstractCapabilityMachineContainer(@Nonnull InventoryPlayer playerInv, @Nonnull E te, @Nullable EnumFacing facing) {
    super(playerInv, te.getInventory(), te);
  }

  @Override
  protected void addSlots() {
    addMachineSlots(getPlayerInv());

    View upgradeSlots = getItemHandler().getView(Type.UPGRADE);
    if (upgradeSlots.getSlots() == 1) {
      addSlotToContainer(upgradeSlot = new EnderSlot(upgradeSlots, "upgrade", getUpgradeOffset().x, getUpgradeOffset().y) {

        @Override
        public int getSlotStackLimit() {
          return 1;
        }

        @Override
        public boolean isItemValid(@Nonnull ItemStack itemStack) {
          return NullHelper.notnull(upgradeSlots.insertItem(0, itemStack, true), "insertItem returned null").getCount() < itemStack.getCount();
        }
      });
    }
  }

  @Override
  public @Nonnull Point getPlayerInventoryOffset() {
    return new Point(8, 84);
  }

  public @Nonnull Point getUpgradeOffset() {
    return new Point(12, 60);
  }

  public Slot getUpgradeSlot() {
    return upgradeSlot;
  }

  /**
   * ATTN: Do not access any non-static field from this method. Your object has not yet been constructed when it is called!
   */
  protected abstract void addMachineSlots(InventoryPlayer playerInv);

  @Override
  public @Nonnull ItemStack transferStackInSlot(@Nonnull EntityPlayer entityPlayer, int slotNumber) {
    hasAlreadyJustSuccessfullyTransferedAStack = false;
    ItemStack copystack = Prep.getEmpty();
    Slot slot = inventorySlots.get(slotNumber);
    if (slot != null && slot.getHasStack()) {
      ItemStack origStack = slot.getStack();
      if (Prep.isValid(origStack)) {
        copystack = origStack.copy();

        boolean merged = false;
        if (slotNumber < startPlayerSlot) {
          if (mergeItemStack(origStack, true, s -> s.inventory == getPlayerInv())) {
            merged = true;
          }
        } else {
          if (mergeItemStack(origStack, true, Type.INPUT, Type.UPGRADE)) {
            merged = true;
          }
        }

        if (!merged) {
          return Prep.getEmpty();
        }

        if (EnderSlot.is(slot, Type.OUTPUT)) {
          slot.onSlotChange(origStack, copystack);
        }

        if (Prep.isInvalid(origStack)) {
          slot.putStack(Prep.getEmpty());
        } else {
          slot.onSlotChanged();
        }

        if (origStack.getCount() == copystack.getCount()) {
          return Prep.getEmpty();
        }

        slot.onTake(entityPlayer, origStack);
      }
    }

    hasAlreadyJustSuccessfullyTransferedAStack = true;
    return copystack;
  }
  
  protected boolean mergeItemStack(ItemStack stack, boolean reverse, SlotPredicate... slots) {

    int slotid = reverse ? inventorySlots.size() - 1 : 0;
    boolean result = false;

    Slot slot;
    ItemStack itemstack1;
    
    for (SlotPredicate check : slots) {

      if (stack.isStackable()) {
        while (!stack.isEmpty() && (!reverse && slotid < inventorySlots.size() || reverse && slotid >= 0)) {
          slot = this.inventorySlots.get(slotid);
          if (check.test(slot)) {
            itemstack1 = slot.getStack();

            if (!itemstack1.isEmpty() && itemstack1.getItem() == stack.getItem()
                && (!stack.getHasSubtypes() || stack.getItemDamage() == itemstack1.getItemDamage()) && ItemStack.areItemStackTagsEqual(stack, itemstack1)
                && slot.isItemValid(stack) && stack != itemstack1) {

              int mergedSize = itemstack1.getCount() + stack.getCount();
              int maxStackSize = Math.min(stack.getMaxStackSize(), slot.getSlotStackLimit());
              if (mergedSize <= maxStackSize) {
                stack.setCount(0);
                itemstack1.setCount(mergedSize);
                slot.onSlotChanged();
                result = true;
              } else if (itemstack1.getCount() < maxStackSize) {
                stack.shrink(maxStackSize - itemstack1.getCount());
                itemstack1.setCount(maxStackSize);
                slot.onSlotChanged();
                result = true;
              }
            }
          }

          if (reverse) {
            --slotid;
          } else {
            ++slotid;
          }
        }
      }

      if (!stack.isEmpty()) {
        while (!reverse && slotid < inventorySlots.size() || reverse && slotid >= 0) {
          slot = this.inventorySlots.get(slotid);
          if (check.test(slot)) {
            itemstack1 = slot.getStack();

            if (itemstack1.isEmpty() && slot.isItemValid(stack)) {
              ItemStack in = stack.copy();
              in.setCount(Math.min(in.getCount(), slot.getSlotStackLimit()));

              slot.putStack(in);
              slot.onSlotChanged();
              stack.shrink(in.getCount());
              result = true;
              break;
            }

            if (reverse) {
              --slotid;
            } else {
              ++slotid;
            }
          }
        }
      }
    }
    return result;

  }

  private boolean hasAlreadyJustSuccessfullyTransferedAStack = false;

  @Override
  protected void retrySlotClick(int slotId, int clickedButton, boolean mode, @Nonnull EntityPlayer playerIn) {
    if (!hasAlreadyJustSuccessfullyTransferedAStack) {
      this.slotClick(slotId, clickedButton, ClickType.QUICK_MOVE, playerIn);
    } else {
      hasAlreadyJustSuccessfullyTransferedAStack = false;
    }
  }

  protected int getProgressScaled(int scale) {
    if (getTileEntity() instanceof IProgressTile) {
      Util.getProgressScaled(scale, (IProgressTile) getTileEntity());
    }
    return 0;
  }

  private int guiID = -1;

  public void setGuiID(int id) {
    guiID = id;
  }

  public int getGuiID() {
    return guiID;
  }

}
