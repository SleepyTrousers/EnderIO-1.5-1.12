package crazypants.enderio.machine.recipe;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class RecipeInput {

  private final ItemStack input;
  private final boolean useMeta;

  private final FluidStack fluid;

  public RecipeInput(ItemStack input) {
    this(input, true);
  }

  public RecipeInput(ItemStack input, boolean useMeta) {
    this.input = input;
    this.useMeta = useMeta;
    this.fluid = null;
  }

  public RecipeInput(FluidStack fluid) {
    this.input = null;
    this.useMeta = false;
    this.fluid = fluid;
  }

  public boolean isFluid() {
    return fluid != null;
  }

  public ItemStack getInput() {
    return input;
  }

  public FluidStack getFluidInput() {
    return fluid;
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
    return null;
  }

  @Override
  public String toString() {
    return "RecipeInput [input=" + input + ", useMeta=" + useMeta + "]";
  }

}
