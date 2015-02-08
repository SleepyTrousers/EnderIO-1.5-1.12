package crazypants.enderio.machine.vacuum;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerVacuumChest extends Container {

  final Slot filterSlot;
  Runnable filterChangedCB;

  public ContainerVacuumChest(EntityPlayer player, InventoryPlayer inventory, final TileVacuumChest te) {
    int x = 8;
    int y = 18;
    int index = -1;
    for (int i = 0; i < TileVacuumChest.ITEM_ROWS; ++i) {
      for (int j = 0; j < 9; ++j) {
        addSlotToContainer(new Slot(te, ++index, x + j * 18, y + i * 18));
      }
    }

    y = 124;
    // add players inventory
    for (int i = 0; i < 3; ++i) {
      for (int j = 0; j < 9; ++j) {
        addSlotToContainer(new Slot(inventory, j + i * 9 + 9, x + j * 18, y + i * 18));
      }
    }
    for (int i = 0; i < 9; ++i) {
      addSlotToContainer(new Slot(inventory, i, x + i * 18, y + 58));
    }

    filterSlot = new FilterSlot(new InventoryFilterUpgrade(te));
    addSlotToContainer(filterSlot);
  }

  @Override
  public boolean canInteractWith(EntityPlayer var1) {
    return true;
  }

  @Override
  public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
    ItemStack itemstack = null;
    Slot slot = (Slot) this.inventorySlots.get(par2);

    if(slot != null && slot.getHasStack()) {
      ItemStack itemstack1 = slot.getStack();
      itemstack = itemstack1.copy();

      if(par2 < TileVacuumChest.ITEM_SLOTS) {
        if(!this.mergeItemStack(itemstack1, TileVacuumChest.ITEM_SLOTS, this.inventorySlots.size()-1, true)) {
          return null;
        }
      } else if(!this.mergeItemStack(itemstack1, 0, TileVacuumChest.ITEM_SLOTS, false)) {
        return null;
      }

      if(itemstack1.stackSize == 0) {
        slot.putStack((ItemStack) null);
      } else {
        slot.onSlotChanged();
      }
    }
    return itemstack;
  }

  void setFilterChangedCB(Runnable filterChangedCB) {
    this.filterChangedCB = filterChangedCB;
  }

  void filterChanged() {
    if(filterChangedCB != null) {
      filterChangedCB.run();
    }
  }

  class FilterSlot extends Slot {
    InventoryFilterUpgrade inv;

    FilterSlot(InventoryFilterUpgrade inv) {
      super(inv, 0, 8, 86);
      this.inv = inv;
    }

    @Override
    public void onSlotChanged() {
      filterChanged();
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
      return inv.isItemValidForSlot(0, stack);
    }
  }
}
