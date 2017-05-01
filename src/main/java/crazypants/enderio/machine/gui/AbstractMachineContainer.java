package crazypants.enderio.machine.gui;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.enderio.core.api.common.util.IProgressTile;
import com.enderio.core.common.ContainerEnder;
import com.enderio.core.common.util.Util;

import crazypants.enderio.machine.AbstractInventoryMachineEntity;
import crazypants.enderio.machine.SlotDefinition;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public abstract class AbstractMachineContainer<T extends AbstractInventoryMachineEntity> extends ContainerEnder<T> {
 
  protected Slot upgradeSlot;

  public AbstractMachineContainer(InventoryPlayer playerInv, T te) {
    super(playerInv, te);
  }
  
  @Override
  protected void addSlots(InventoryPlayer playerInv) {
    addMachineSlots(playerInv);
    
    final T te = getInv();
    if(te.getSlotDefinition().getNumUpgradeSlots() == 1) {
      upgradeSlot = new Slot(te, te.getSlotDefinition().getMinUpgradeSlot(), getUpgradeOffset().x, getUpgradeOffset().y) {

        @Override
        public int getSlotStackLimit() {
          return 1;
        }

        @Override
        public boolean isItemValid(@Nullable ItemStack itemStack) {
          return te.isItemValidForSlot(te.getSlotDefinition().getMinUpgradeSlot(), itemStack);
        }
      };
      addSlotToContainer(upgradeSlot);
    }
  }

  @Override
  public Point getPlayerInventoryOffset() {
    return new Point(8,84);
  }
  
  @Override
  public Point getUpgradeOffset() {
    return new Point(12,60);
  }

  public Slot getUpgradeSlot() {
    return upgradeSlot;
  }

  /**
   * ATTN: Do not access any non-static field from this method. Your object has
   * not yet been constructed when it is called!
   */
  protected abstract void addMachineSlots(InventoryPlayer playerInv);

  @Override
  public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotNumber) {
    hasAlreadyJustSuccessfullyTransferedAStack = false;
    SlotDefinition slotDef = getInv().getSlotDefinition();

    ItemStack copystack = null;
    Slot slot = inventorySlots.get(slotNumber);
    if(slot != null && slot.getHasStack()) {
      ItemStack origStack = slot.getStack();
      if (origStack != null) {
        copystack = origStack.copy();

        boolean merged = false;
        for (SlotRange range : getTargetSlotsForTransfer(slotNumber, slot)) {
          if (mergeItemStack(origStack, range.getStart(), range.getEnd(), range.reverse)) {
            while (mergeItemStack(origStack, range.getStart(), range.getEnd(), range.reverse)) {
            }
            merged = true;
            break;
          }
        }

        if (!merged) {
          return null;
        }

        if (slotDef.isOutputSlot(slot.getSlotIndex())) {
          slot.onSlotChange(origStack, copystack);
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
    }

    hasAlreadyJustSuccessfullyTransferedAStack = true;
    return copystack;
  }

  private boolean hasAlreadyJustSuccessfullyTransferedAStack = false;

  @Override
  protected void retrySlotClick(int slotId, int clickedButton, boolean mode, EntityPlayer playerIn) {
    if (!hasAlreadyJustSuccessfullyTransferedAStack) {
      this.slotClick(slotId, clickedButton, ClickType.QUICK_MOVE, playerIn);
    } else {
      hasAlreadyJustSuccessfullyTransferedAStack = false;
    }
  }

  protected int getIndexOfFirstPlayerInvSlot(SlotDefinition slotDef) {
    return slotDef.getNumSlots();
  }

  protected SlotRange getPlayerInventorySlotRange(boolean reverse) {
    return new SlotRange(startPlayerSlot, endHotBarSlot, reverse);
  }

  protected SlotRange getPlayerInventoryWithoutHotbarSlotRange() {
    return new SlotRange(startPlayerSlot, endPlayerSlot, false);
  }

  protected SlotRange getPlayerHotbarSlotRange() {
    return new SlotRange(startHotBarSlot, endHotBarSlot, false);
  }

  protected void addInventorySlotRange(List<SlotRange> res, int start, int end) {
    SlotRange range = null;
    for (int i = start; i < end; i++) {
      Slot slotFromInventory = getSlotFromInventory(getInv(), i);
      if (slotFromInventory != null) {
        int slotNumber = slotFromInventory.slotNumber;
        if (range == null) {
          range = new SlotRange(slotNumber, slotNumber + 1, false);
        } else if (range.getEnd() == slotNumber) {
          range = new SlotRange(range.getStart(), slotNumber + 1, false);
        } else {
          res.add(range);
          range = new SlotRange(slotNumber, slotNumber + 1, false);
        }
      }
    }
    if (range != null) {
      res.add(range);
    }
  }

  protected void addInputSlotRanges(List<SlotRange> res) {
    SlotDefinition slotDef = getInv().getSlotDefinition();
    if(slotDef.getNumInputSlots() > 0) {
      addInventorySlotRange(res, slotDef.getMinInputSlot(), slotDef.getMaxInputSlot() + 1);
    }
  }

  protected void addUpgradeSlotRanges(List<SlotRange> res) {
    SlotDefinition slotDef = getInv().getSlotDefinition();
    if(slotDef.getNumUpgradeSlots() > 0) {
      addInventorySlotRange(res, slotDef.getMinUpgradeSlot(), slotDef.getMaxUpgradeSlot() + 1);
    }
  }

  protected void addPlayerSlotRanges(List<SlotRange> res, int slotIndex) {
    if (slotIndex < endPlayerSlot) {
      res.add(getPlayerHotbarSlotRange());
    }
    if (slotIndex >= startHotBarSlot && slotIndex < endHotBarSlot) {
      res.add(getPlayerInventoryWithoutHotbarSlotRange());
    }
  }

  protected List<SlotRange> getTargetSlotsForTransfer(int slotNumber, Slot slot) {
    if (slot.inventory == getInv()) {
      SlotDefinition slotDef = getInv().getSlotDefinition();
      if (slotDef.isInputSlot(slot.getSlotIndex()) || slotDef.isUpgradeSlot(slot.getSlotIndex())) {
        return Collections.singletonList(getPlayerInventorySlotRange(false));
      }
      if (slotDef.isOutputSlot(slot.getSlotIndex())) {
        return Collections.singletonList(getPlayerInventorySlotRange(true));
      }
    } else if (slotNumber >= startPlayerSlot) {
      List<SlotRange> res = new ArrayList<SlotRange>();
      addInputSlotRanges(res);
      addUpgradeSlotRanges(res);
      addPlayerSlotRanges(res, slotNumber);
      return res;
    }
    return Collections.emptyList();
  }

  protected int getProgressScaled(int scale) {
    if(getInv() instanceof IProgressTile) {
      Util.getProgressScaled(scale, (IProgressTile) getInv());
    }
    return 0;
  }

  public static class SlotRange {
    private final int start;
    private final int end;
    final boolean reverse;

    public SlotRange(int start, int end, boolean reverse) {
      this.start = start;
      this.end = end;
      this.reverse = reverse;
    }

    public int getStart() {
      return start;
    }

    public int getEnd() {
      return end;
    }
  }

  private int guiID = -1;

  public void setGuiID(int id) {
    guiID = id;
  }

  public int getGuiID() {
    return guiID;
  }

}
