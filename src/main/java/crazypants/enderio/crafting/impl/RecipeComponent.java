package crazypants.enderio.crafting.impl;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import crazypants.enderio.crafting.IRecipeComponent;

public abstract class RecipeComponent implements IRecipeComponent {

  protected final ItemStack itemPrototype;
  protected final FluidStack fluidPrototype;
  protected final int slot;

  public RecipeComponent(ItemStack itemPrototype) {
    this(itemPrototype, -1);
  }

  public RecipeComponent(ItemStack itemPrototype, int slot) {
    this.itemPrototype = itemPrototype;
    this.fluidPrototype = null;
    this.slot = slot;
  }

  public RecipeComponent(FluidStack fluidProto, int slot) {
    this.itemPrototype = null;
    this.fluidPrototype = fluidProto;
    this.slot = slot;
  }

  @Override
  public FluidStack getFluid() {
    if(fluidPrototype == null) {
      return null;
    }
    return fluidPrototype.copy();
  }

  @Override
  public ItemStack getItem() {
    if(itemPrototype == null) {
      return null;
    }
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
    if(left != null && left.isItemEqual(right)) {
      return left.stackTagCompound == null ? true : right.stackTagCompound != null && left.stackTagCompound.equals(right.stackTagCompound);
    }
    return false;
  }

  @Override
  public boolean isEquivalent(FluidStack output) {
    return fluidPrototype != null && output != null && fluidPrototype.isFluidEqual(output);
  }



}
