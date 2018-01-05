package crazypants.enderio.base.recipe;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface IRecipeInput {

  @Nonnull
  IRecipeInput copy();

  boolean isFluid();

  @Nonnull
  ItemStack getInput();

  FluidStack getFluidInput();

  float getMulitplier();

  int getSlotNumber();

  boolean isInput(@Nonnull ItemStack test);

  boolean isInput(FluidStack test);

  ItemStack[] getEquivelentInputs();

  boolean isValid();

}