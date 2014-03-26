package crazypants.enderio.machine.recipe;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface IRecipe {

  boolean isValid();

  float getEnergyRequired();

  RecipeOutput[] getOutputs();

  RecipeInput[] getInputs();

  ItemStack[] getInputStacks();

  boolean isInputForRecipe(List<ItemStack> test);

  boolean isInputForRecipe(List<ItemStack> test, List<FluidStack> testFluids);

  boolean isValidInput(ItemStack item);

  boolean isValidInput(FluidStack fluid);

}
