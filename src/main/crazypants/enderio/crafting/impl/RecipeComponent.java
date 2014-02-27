package crazypants.enderio.crafting.impl;

import net.minecraft.item.ItemStack;
import crazypants.enderio.crafting.IRecipeComponent;

public abstract class RecipeComponent implements IRecipeComponent {

  protected final ItemStack itemPrototype;
  protected final int slot;

  public RecipeComponent(ItemStack itemPrototype) {
    this(itemPrototype, -1);
  }

  public RecipeComponent(ItemStack itemPrototype, int slot) {
    this.itemPrototype = itemPrototype;
    this.slot = slot;
  }

  @Override
  public ItemStack getItem() {
    return itemPrototype.copy();
  }

  @Override
  public int getQuantity() {
    return itemPrototype.stackSize;
  }

  @Override
  public int getSlot() {
    return slot;
  }

  @Override
  public boolean isValidSlot(int slot) {
    return this.slot < 0 || this.slot == slot;
  }

  protected boolean isEqual(ItemStack left, ItemStack right) {
    if(left.isItemEqual(right)) {
      return left.stackTagCompound == null ? true : left.stackTagCompound.equals(right.stackTagCompound);
    }
    return false;
  }

}
