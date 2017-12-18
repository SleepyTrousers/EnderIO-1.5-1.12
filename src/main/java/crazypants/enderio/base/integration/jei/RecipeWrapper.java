package crazypants.enderio.base.integration.jei;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import crazypants.enderio.base.recipe.IRecipe;
import crazypants.enderio.base.recipe.RecipeBonusType;
import crazypants.enderio.base.recipe.RecipeOutput;
import crazypants.enderio.util.Prep;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class RecipeWrapper extends BlankRecipeWrapper {

  protected final IRecipe recipe;

  public RecipeWrapper(IRecipe recipe) {
    this.recipe = recipe;
  }

  @Override
  public void getIngredients(@Nonnull IIngredients ingredients) {
    List<List<ItemStack>> inputStacks = recipe.getInputStackAlternatives();
    ingredients.setInputLists(ItemStack.class, inputStacks);

    List<ItemStack> outputs = new ArrayList<ItemStack>();
    for (RecipeOutput out : recipe.getOutputs()) {
      if (Prep.isValid(out.getOutput())) {
        outputs.add(out.getOutput());
      }
    }
    ingredients.setOutputs(ItemStack.class, outputs);

    List<FluidStack> inputFluidStacks = recipe.getInputFluidStacks();
    if (inputFluidStacks != null) {
      ingredients.setInputs(FluidStack.class, inputFluidStacks);
    }

    List<FluidStack> fluidOutputs = new ArrayList<FluidStack>();
    for (RecipeOutput out : recipe.getOutputs()) {
      if (out.getFluidOutput() != null) {
        fluidOutputs.add(out.getFluidOutput());
      }
    }
    ingredients.setOutputs(FluidStack.class, fluidOutputs);
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

  public RecipeBonusType getBonusType() {
    return recipe.getBonusType();
  }
}