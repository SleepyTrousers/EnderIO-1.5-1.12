package crazypants.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class TemplateSlot extends Slot {

  int slotIndex;

  public TemplateSlot(IInventory inventory, int slotIndex, int x, int y) {
    super(inventory, slotIndex, x, y);
    this.slotIndex = slotIndex;
  }

  @Override
  public boolean canTakeStack(EntityPlayer player) {
    return true;
  }

  @Override
  public ItemStack decrStackSize(int par1) {
    return null;
  }

  @Override
  public boolean isItemValid(ItemStack stack) {
    return true;
  }

  @Override
  public void putStack(ItemStack par1ItemStack) {
    if(par1ItemStack != null) {
      par1ItemStack.stackSize = 0;
    }
    inventory.setInventorySlotContents(slotIndex, par1ItemStack);
    onSlotChanged();
  }

}
