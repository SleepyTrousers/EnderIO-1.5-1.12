package crazypants.enderio.machine.gui;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.util.IProgressTile;
import crazypants.util.Util;

public abstract class AbstractMachineContainer extends Container {

  protected final AbstractMachineEntity tileEntity;
  
  protected Map<Slot, Point> playerSlotLocations = new HashMap<Slot, Point>();

  protected Slot upgradeSlot;
  
  protected final int startPlayerSlot;
  protected final int endPlayerSlot;
  protected final int startHotBarSlot;
  protected final int endHotBarSlot;

  @SuppressWarnings("OverridableMethodCallInConstructor")
  public AbstractMachineContainer(InventoryPlayer playerInv, AbstractMachineEntity te) {
    this.tileEntity = te;

    addMachineSlots(playerInv);

    if(te.getSlotDefinition().getNumUpgradeSlots() == 1) {
      upgradeSlot = new Slot(te, te.getSlotDefinition().getMinUpgradeSlot(), getUpgradeOffset().x, getUpgradeOffset().y) {

        @Override
        public int getSlotStackLimit() {
          return 1;
        }

        @Override
        public boolean isItemValid(ItemStack itemStack) {
          return tileEntity.isItemValidForSlot(tileEntity.getSlotDefinition().getMinUpgradeSlot(), itemStack);
        }
      };
      addSlotToContainer(upgradeSlot);
    }

    int x = getPlayerInventoryOffset().x;
    int y = getPlayerInventoryOffset().y;        
    
    // add players inventory
    startPlayerSlot = inventorySlots.size();
    for (int i = 0; i < 3; ++i) {
      for (int j = 0; j < 9; ++j) {
        Point loc = new Point(x + j * 18, y + i * 18);
        Slot slot = new Slot(playerInv, j + i * 9 + 9, loc.x, loc.y);
        addSlotToContainer(slot);
        playerSlotLocations.put(slot, loc);
      }
    }
    endPlayerSlot = inventorySlots.size();

    startHotBarSlot = inventorySlots.size();
    for (int i = 0; i < 9; ++i) {
      Point loc = new Point(x + i * 18, y + 58);
      Slot slot = new Slot(playerInv, i, loc.x, loc.y);
      addSlotToContainer(slot);      
      playerSlotLocations.put(slot, loc);
    }
    endHotBarSlot = inventorySlots.size();
  }

  @Override
  public boolean canInteractWith(EntityPlayer entityplayer) {
    return tileEntity.isUseableByPlayer(entityplayer);
  }

  public Point getPlayerInventoryOffset() {
    return new Point(8,84);
  }
  
  public Point getUpgradeOffset() {
    return new Point(12,60);
  }

  public Slot getUpgradeSlot() {
    return upgradeSlot;
  }

  public AbstractMachineEntity getTileEntity() {
    return tileEntity;
  }

  protected abstract void addMachineSlots(InventoryPlayer playerInv);

  @Override
  public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotIndex) {
    SlotDefinition slotDef = tileEntity.getSlotDefinition();

    ItemStack copystack = null;
    Slot slot = (Slot) inventorySlots.get(slotIndex);
    if(slot != null && slot.getHasStack()) {
      ItemStack origStack = slot.getStack();
      copystack = origStack.copy();

      boolean merged = false;
      for(SlotRange range : getTargetSlotsForTransfer(slotIndex, slot)) {
        if(mergeItemStack(origStack, range.start, range.end, range.reverse)) {
          merged = true;
          break;
        }
      }

      if(!merged) {
        return null;
      }

      if(slotDef.isOutputSlot(slotIndex)) {
        slot.onSlotChange(origStack, copystack);
      }

      if(origStack.stackSize == 0) {
        slot.putStack((ItemStack) null);
      } else {
        slot.onSlotChanged();
      }

      if(origStack.stackSize == copystack.stackSize) {
        return null;
      }

      slot.onPickupFromSlot(entityPlayer, origStack);
    }

    return copystack;
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

  protected void addInputSlotRanges(List<SlotRange> res) {
    SlotDefinition slotDef = tileEntity.getSlotDefinition();
    if(slotDef.getNumInputSlots() > 0) {
      res.add(new SlotRange(slotDef.getMinInputSlot(), slotDef.getMaxInputSlot() + 1, false));
    }
  }

  protected void addUpgradeSlotRanges(List<SlotRange> res) {
    SlotDefinition slotDef = tileEntity.getSlotDefinition();
    if(slotDef.getNumUpgradeSlots() > 0) {
      res.add(new SlotRange(slotDef.getMinUpgradeSlot(), slotDef.getMaxUpgradeSlot() + 1, false));
    }
  }

  protected void addPlayerSlotRanges(List<SlotRange> res, int slotIndex) {
    if(slotIndex <= endPlayerSlot) {
      res.add(getPlayerHotbarSlotRange());
    }
    if(slotIndex >= startHotBarSlot && slotIndex <= endHotBarSlot) {
      res.add(getPlayerInventoryWithoutHotbarSlotRange());
    }
  }

  protected List<SlotRange> getTargetSlotsForTransfer(int slotIndex, Slot slot) {
    SlotDefinition slotDef = tileEntity.getSlotDefinition();
    if(slotDef.isInputSlot(slotIndex) || slotDef.isUpgradeSlot(slotIndex)) {
      return Collections.singletonList(getPlayerInventorySlotRange(false));
    }
    if(slotDef.isOutputSlot(slotIndex)) {
      return Collections.singletonList(getPlayerInventorySlotRange(true));
    }
    if(slotIndex >= startPlayerSlot) {
      ArrayList<SlotRange> res = new ArrayList<SlotRange>();
      addInputSlotRanges(res);
      addUpgradeSlotRanges(res);
      addPlayerSlotRanges(res, slotIndex);
      return res;
    }
    return Collections.emptyList();
  }

  /**
   * Added validation of slot input
   */
  @Override
  protected boolean mergeItemStack(ItemStack par1ItemStack, int fromIndex, int toIndex, boolean reversOrder) {

    boolean result = false;
    int checkIndex = fromIndex;

    if(reversOrder) {
      checkIndex = toIndex - 1;
    }

    Slot slot;
    ItemStack itemstack1;

    if(par1ItemStack.isStackable()) {

      while (par1ItemStack.stackSize > 0 && (!reversOrder && checkIndex < toIndex || reversOrder && checkIndex >= fromIndex)) {
        slot = (Slot) this.inventorySlots.get(checkIndex);
        itemstack1 = slot.getStack();

        if(itemstack1 != null && itemstack1.getItem() == par1ItemStack.getItem()
            && (!par1ItemStack.getHasSubtypes() || par1ItemStack.getItemDamage() == itemstack1.getItemDamage())
            && ItemStack.areItemStackTagsEqual(par1ItemStack, itemstack1)
            && slot.isItemValid(par1ItemStack)) {

          int mergedSize = itemstack1.stackSize + par1ItemStack.stackSize;
          int maxStackSize =  Math.min(par1ItemStack.getMaxStackSize(), slot.getSlotStackLimit());
          if(mergedSize <= maxStackSize) {
            par1ItemStack.stackSize = 0;
            itemstack1.stackSize = mergedSize;
            slot.onSlotChanged();
            result = true;
          } else if(itemstack1.stackSize < maxStackSize) {
            par1ItemStack.stackSize -= maxStackSize - itemstack1.stackSize;
            itemstack1.stackSize = maxStackSize;
            slot.onSlotChanged();
            result = true;
          }
        }

        if(reversOrder) {
          --checkIndex;
        } else {
          ++checkIndex;
        }
      }
    }

    if(par1ItemStack.stackSize > 0) {
      if(reversOrder) {
        checkIndex = toIndex - 1;
      } else {
        checkIndex = fromIndex;
      }

      while (!reversOrder && checkIndex < toIndex || reversOrder && checkIndex >= fromIndex) {
        slot = (Slot) this.inventorySlots.get(checkIndex);
        itemstack1 = slot.getStack();

        if(itemstack1 == null && slot.isItemValid(par1ItemStack)) {
          ItemStack in = par1ItemStack.copy();
          in.stackSize = Math.min(in.stackSize, slot.getSlotStackLimit());

          slot.putStack(in);
          slot.onSlotChanged();
          if(in.stackSize >= par1ItemStack.stackSize) {
            par1ItemStack.stackSize = 0;
          } else {
            par1ItemStack.stackSize -= in.stackSize;
          }
          result = true;
          break;
        }

        if(reversOrder) {
          --checkIndex;
        } else {
          ++checkIndex;
        }
      }
    }

    return result;
  }

  protected int getProgressScaled(int scale) {
    if(getTileEntity() instanceof IProgressTile) {
      Util.getProgressScaled(scale, (IProgressTile) getTileEntity());
    }
    return 0;
  }

  public static class SlotRange {
    final int start;
    final int end;
    final boolean reverse;

    public SlotRange(int start, int end, boolean reverse) {
      this.start = start;
      this.end = end;
      this.reverse = reverse;
    }
  }
}
