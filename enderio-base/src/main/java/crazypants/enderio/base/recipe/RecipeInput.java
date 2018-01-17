package crazypants.enderio.base.recipe;

import javax.annotation.Nonnull;

import crazypants.enderio.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class RecipeInput implements IRecipeInput {

  private final int slot;
  private final @Nonnull ItemStack input;
  private final boolean useMeta;

  private final FluidStack fluid;

  private final float multiplier;

  public RecipeInput(@Nonnull ItemStack input) {
    this(input, true);
  }

  public RecipeInput(@Nonnull ItemStack input, boolean useMeta) {
    this(input, useMeta, null, 1, -1);
  }

  public RecipeInput(FluidStack fluid) {
    this(Prep.getEmpty(), false, fluid, 1f, -1);
  }

  public RecipeInput(FluidStack fluidStack, float mulitplier) {
    this(Prep.getEmpty(), true, fluidStack, mulitplier, -1);
  }

  public RecipeInput(@Nonnull ItemStack item, boolean useMeta, float multiplier, int slot) {
    this(item, useMeta, null, multiplier, slot);
  }

  protected RecipeInput(@Nonnull ItemStack input, boolean useMeta, FluidStack fluid, float mulitplier, int slot) {
    this.input = input.copy();
    this.useMeta = useMeta;
    this.fluid = fluid == null ? null : fluid.copy();
    this.multiplier = mulitplier;
    this.slot = slot;
  }

  public RecipeInput(@Nonnull RecipeInput copyFrom) {
    input = copyFrom.input.copy();
    fluid = copyFrom.fluid == null ? null : copyFrom.fluid.copy();
    useMeta = copyFrom.useMeta;
    multiplier = copyFrom.multiplier;
    slot = copyFrom.slot;
  }

  @Override
  public @Nonnull RecipeInput copy() {
    return new RecipeInput(this);
  }

  @Override
  public boolean isFluid() {
    return fluid != null;
  }

  @Override
  public @Nonnull ItemStack getInput() {
    return input;
  }

  @Override
  public FluidStack getFluidInput() {
    return fluid;
  }

  @Override
  public float getMulitplier() {
    return multiplier;
  }

  @Override
  public int getSlotNumber() {
    return slot;
  }

  @Override
  public boolean isInput(@Nonnull ItemStack test) {
    if (Prep.isInvalid(test) || Prep.isInvalid(input)) {
      return false;
    }

    if (useMeta) {
      return ItemStack.areItemsEqual(input, test) && (!input.hasTagCompound() || ItemStack.areItemStackTagsEqual(input, test));
    }
    return test.getItem() == input.getItem() && (!input.hasTagCompound() || ItemStack.areItemStackTagsEqual(input, test));
  }

  @Override
  public boolean isInput(FluidStack test) {
    if (test == null || fluid == null) {
      return false;
    }
    return test.isFluidEqual(fluid);
  }

  @Override
  public ItemStack[] getEquivelentInputs() {
    if (Prep.isInvalid(input)) {
      return null;
    } else if (useMeta) {
      return new ItemStack[] { input };
    } else {
      ItemStack result = input.copy();
      result.setItemDamage(OreDictionary.WILDCARD_VALUE);
      return new ItemStack[] { result };
    }
  }

  @Override
  public String toString() {
    if (isValid()) {
      return "RecipeInput [input=" + input + ", useMeta=" + useMeta + "]";
    }
    return "RecipeInput invalid";
  }

  @Override
  public boolean isValid() {
    if (isFluid()) {
      return fluid != null && fluid.getFluid() != null;
    } else {
      return Prep.isValid(input);
    }

  }

}
