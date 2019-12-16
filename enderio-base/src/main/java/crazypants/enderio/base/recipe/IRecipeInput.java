package crazypants.enderio.base.recipe;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface IRecipeInput {

  @Nonnull
  IRecipeInput copy();

  boolean isFluid();

  /**
   * 
   * @return A copy of the {@link ItemStack} that primarily represents this input. This may not be a stack of the only item that this input accepts.
   */
  @Nonnull
  ItemStack getInput();

  FluidStack getFluidInput();

  float getMulitplier();

  int getSlotNumber();

  boolean isInput(@Nonnull ItemStack test);

  boolean isInput(FluidStack test);

  ItemStack[] getEquivelentInputs();

  boolean isValid();

  int getStackSize();

  void shrinkStack(int count);

}
