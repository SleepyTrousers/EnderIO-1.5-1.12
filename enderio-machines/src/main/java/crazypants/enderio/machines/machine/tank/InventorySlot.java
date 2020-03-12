package crazypants.enderio.machines.machine.tank;

import javax.annotation.Nonnull;

import com.enderio.core.common.ContainerEnder;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class InventorySlot extends ContainerEnder.BaseSlot {
  public InventorySlot(@Nonnull IInventory inventoryIn, int index, int xPosition, int yPosition) {
    super(inventoryIn, index, xPosition, yPosition);
  }

  @Override
  public boolean isItemValid(@Nonnull ItemStack itemStack) {
    return this.inventory.isItemValidForSlot(getSlotIndex(), itemStack);
  }

  @Override
  public void putStack(@Nonnull ItemStack stack) {
    if (stack.getCount() <= getItemStackLimit(stack)) {
      super.putStack(stack);
    } else {
      throw new RuntimeException("Invalid stacksize. " + stack.getCount() + " is more than the allowed limit of " + getItemStackLimit(stack)
          + ". THIS IS NOT AN ERROR IN ENDER IO BUT THE CALLING MOD!");
    }
  }

}
