package crazypants.enderio.recipe;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class RecipeInput {

  private final int slot;
  private final ItemStack input;
  private final boolean useMeta;

  private final FluidStack fluid;

  private final float multiplier;

  public RecipeInput(ItemStack input) {
    this(input, true);
  }

  public RecipeInput(ItemStack input, boolean useMeta) {
    this(input, useMeta, null, 1, -1);
  }

  public RecipeInput(FluidStack fluid) {
    this(null, false, fluid, 1f, -1);
  }

  public RecipeInput(FluidStack fluidStack, float mulitplier) {
    this(null, true, fluidStack, mulitplier, -1);
  }

  public RecipeInput(ItemStack item, boolean useMeta, float multiplier, int slot) {
    this(item, useMeta, null, multiplier, slot);
  }

  protected RecipeInput(ItemStack input, boolean useMeta, FluidStack fluid, float mulitplier, int slot) {
    this.input = input == null ? null : input.copy();
    this.useMeta = useMeta;
    this.fluid = fluid == null ? null : fluid.copy();
    this.multiplier = mulitplier;
    this.slot = slot;
  }

  public RecipeInput(RecipeInput copyFrom) {
    input = copyFrom.input == null ? null : copyFrom.input.copy();
    fluid = copyFrom.fluid == null ? null : copyFrom.fluid.copy();
    useMeta = copyFrom.useMeta;
    multiplier = copyFrom.multiplier;
    slot = copyFrom.slot;
  }
  
  public RecipeInput copy() {
    return new RecipeInput(this);
  }

  public boolean isFluid() {
    return fluid != null;
  }

  public @Nonnull ItemStack getInput() {
    return input;
  }

  public FluidStack getFluidInput() {
    return fluid;
  }

  public float getMulitplier() {
    return multiplier;
  }

  public int getSlotNumber() {
    return slot;
  }

  public boolean isInput(ItemStack test) {
    if(test == null || input == null) {
      return false;
    }

    if(useMeta) {
      return test.getItem() == input.getItem() && test.getItemDamage() == input.getItemDamage();
    }
    return test.getItem() == input.getItem();
  }

  public boolean isInput(FluidStack test) {
    if(test == null || fluid == null) {
      return false;
    }
    return test.isFluidEqual(fluid);
  }

  public ItemStack[] getEquivelentInputs() {
    if (input == null) {
      return null;
    } else if (useMeta) {
      return new ItemStack[] {input};
    } else {
      ItemStack result = input.copy();
      result.setItemDamage(OreDictionary.WILDCARD_VALUE);
      return new ItemStack[] {result};
    }
  }

  @Override
  public String toString() {
    if(isValid()) {
      return "RecipeInput [input=" + input + ", useMeta=" + useMeta + "]";
    } 
    return "RecipeInput invalid";
  }

  public boolean isValid() {
    if(isFluid()) {
      return fluid != null && fluid.getFluid() != null;
    } else {
      return input != null && input.getItem() != null;
    }
    
  }

}
