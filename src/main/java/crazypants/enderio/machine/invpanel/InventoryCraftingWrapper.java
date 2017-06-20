package crazypants.enderio.machine.invpanel;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class InventoryCraftingWrapper extends InventoryCrafting {

  private final IInventory backing;
  private final Container eventHandler;

  public InventoryCraftingWrapper(IInventory backing, Container eventHandlerIn, int width, int height) {
    super(eventHandlerIn, width, height);
    this.backing = backing;
    this.eventHandler = eventHandlerIn;
  }

  @Override
  public ItemStack getStackInSlot(int index) {
    return index >= this.getSizeInventory() ? null : backing.getStackInSlot(index);
  }

  @Override
  public ItemStack removeStackFromSlot(int index) {
    return backing.removeStackFromSlot(index);
  }

  @Override
  public ItemStack decrStackSize(int index, int count) {
    final ItemStack result = backing.decrStackSize(index, count);
    if (result != null) {
      this.eventHandler.onCraftMatrixChanged(this);
    }
    return result;
  }

  @Override
  public void setInventorySlotContents(int index, @Nullable ItemStack stack) {
    backing.setInventorySlotContents(index, stack);
    this.eventHandler.onCraftMatrixChanged(this);
  }

  @Override
  public void clear() {
  }

}
