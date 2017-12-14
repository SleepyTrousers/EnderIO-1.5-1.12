package crazypants.enderio.machines.machine.tank;

import javax.annotation.Nonnull;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class InventorySlot extends Slot {
  public InventorySlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
    super(inventoryIn, index, xPosition, yPosition);
  }

  @Override
  public boolean isItemValid(@Nonnull ItemStack itemStack) {
    return this.inventory.isItemValidForSlot(getSlotIndex(), itemStack);
  }
}