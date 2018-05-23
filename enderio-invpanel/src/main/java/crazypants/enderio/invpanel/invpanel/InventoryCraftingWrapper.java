package crazypants.enderio.invpanel.invpanel;

import javax.annotation.Nonnull;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

public class InventoryCraftingWrapper extends InventoryCrafting {

  private final IInventory backing;
  private final Container eventHandler;

  public InventoryCraftingWrapper(@Nonnull IInventory backing, @Nonnull Container eventHandlerIn, int width, int height) {
    super(eventHandlerIn, width, height);
    this.backing = backing;
    this.eventHandler = eventHandlerIn;
  }

  @Override
  @Nonnull
  public ItemStack getStackInSlot(int index) {
    return index >= this.getSizeInventory() ? ItemStack.EMPTY : backing.getStackInSlot(index);
  }

  @Override
  @Nonnull
  public ItemStack removeStackFromSlot(int index) {
    return backing.removeStackFromSlot(index);
  }

  @Override
  @Nonnull
  public ItemStack decrStackSize(int index, int count) {
    final ItemStack result = backing.decrStackSize(index, count);
    if (!result.isEmpty()) {
      this.eventHandler.onCraftMatrixChanged(this);
    }
    return result;
  }

  @Override
  public void setInventorySlotContents(int index, @Nonnull ItemStack stack) {
    backing.setInventorySlotContents(index, stack);
    this.eventHandler.onCraftMatrixChanged(this);
  }

  @Override
  public void clear() {
  }

}
