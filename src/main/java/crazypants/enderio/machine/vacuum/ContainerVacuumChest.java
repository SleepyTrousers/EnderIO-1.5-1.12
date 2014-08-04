package crazypants.enderio.machine.vacuum;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerVacuumChest extends Container {

  private int numRows = 3;

  public ContainerVacuumChest(EntityPlayer player, InventoryPlayer inventory, TileVacuumChest te) {

    int x = 8;
    int y = 17;
    int index = -1;
    for (int i = 0; i < 3; ++i) {
      for (int j = 0; j < 9; ++j) {
        addSlotToContainer(new Slot(te, ++index, x + j * 18, y + i * 18));
      }
    }

    y = 84;
    // add players inventory
    for (int i = 0; i < 3; ++i) {
      for (int j = 0; j < 9; ++j) {
        addSlotToContainer(new Slot(inventory, j + i * 9 + 9, x + j * 18, y + i * 18));
      }
    }
    for (int i = 0; i < 9; ++i) {
      addSlotToContainer(new Slot(inventory, i, x + i * 18, y + 58));
    }
  }

  @Override
  public boolean canInteractWith(EntityPlayer var1) {
    return true;
  }

  public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
    ItemStack itemstack = null;
    Slot slot = (Slot) this.inventorySlots.get(par2);

    if(slot != null && slot.getHasStack()) {
      ItemStack itemstack1 = slot.getStack();
      itemstack = itemstack1.copy();

      if(par2 < this.numRows * 9) {
        if(!this.mergeItemStack(itemstack1, this.numRows * 9, this.inventorySlots.size(), true)) {
          return null;
        }
      } else if(!this.mergeItemStack(itemstack1, 0, this.numRows * 9, false)) {
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

}
