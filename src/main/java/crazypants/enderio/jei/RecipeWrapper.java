package crazypants.enderio.jei;

import java.util.ArrayList;
import java.util.List;

import crazypants.enderio.machine.recipe.IRecipe;
import crazypants.enderio.machine.recipe.RecipeOutput;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class RecipeWrapper extends BlankRecipeWrapper {

  private final IRecipe recipe;
  
  public RecipeWrapper(IRecipe recipe) {    
    this.recipe = recipe;
  }

  @Override
  public List<?> getInputs() {
    return recipe.getInputStacks();
  }

  @Override
  public List<?> getOutputs() {
    List<ItemStack> outputs = new ArrayList<ItemStack>();
    for(RecipeOutput out : recipe.getOutputs()) {
      if(out.getOutput() != null) {
        outputs.add(out.getOutput());
      }
    }
    return outputs;
  }

  @Override
  public List<FluidStack> getFluidInputs() {
    return recipe.getInputFluidStacks();
  }

  @Override
  public List<FluidStack> getFluidOutputs() {
    List<FluidStack> outputs = new ArrayList<FluidStack>();
    for(RecipeOutput out : recipe.getOutputs()) {
      if(out.getFluidOutput() != null) {
        outputs.add(out.getFluidOutput());
      }
    }
    return outputs;
  }  
  
  public boolean isValid() {
    return recipe != null && recipe.isValid();
  }
  
  public int getEnergyRequired() {
    return recipe.getEnergyRequired();
  }

  public IRecipe getRecipe() {
    return recipe;
  }
  
  
}