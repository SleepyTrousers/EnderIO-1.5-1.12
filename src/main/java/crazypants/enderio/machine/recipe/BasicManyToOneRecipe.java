package crazypants.enderio.machine.recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import crazypants.enderio.machine.MachineRecipeInput;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class BasicManyToOneRecipe implements IManyToOneRecipe {

  private final int energyRequired;
  private final ItemStack output;

  private final RecipeBonusType bonusType;

  private final Recipe recipe;

  public BasicManyToOneRecipe(Recipe recipe) {
    this.recipe = recipe;
    this.output = recipe.getOutputs()[0].getOutput().copy();
    energyRequired = recipe.getEnergyRequired();
    bonusType = recipe.getBonusType();    
  }

  @Override
  public boolean isValidRecipeComponents(ItemStack... items) {

    List<RecipeInput> inputs = new ArrayList<RecipeInput>(Arrays.asList(recipe.getInputs()));
    for (ItemStack is : items) {
      if(is != null) {
        RecipeInput remove = null;
        for (RecipeInput ri : inputs) {
          if(ri.isInput(is)) {
            remove = ri;
            break;
          }
        }
        if(remove != null) {
          inputs.remove(remove);
        } else {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public ItemStack getOutput() {
    return output;
  }

  @Override
  public boolean isValidInput(int slot, ItemStack input) {
    if(input == null) {
      return false;
    }
    return getRecipeComponentFromInput(input) != null;
  }

  @Override
  public boolean isValidInput(FluidStack fluid) {
    return false;
  }

  @Override
  public boolean isValid() {
    return recipe != null && recipe.isValid();
  }

  @Override
  public int getEnergyRequired() {
    return energyRequired;
  }

  @Override
  public RecipeBonusType getBonusType() {
    return bonusType;
  }

  @Override
  public RecipeOutput[] getOutputs() {
    return recipe.getOutputs();
  }

  @Override
  public List<ItemStack> getInputStacks() {
    return recipe.getInputStacks();
  }

  @Override
  public boolean isInputForRecipe(MachineRecipeInput... inputs) {
    if(inputs == null) {
      return false;
    }
    return recipe.isInputForRecipe(inputs);
  }

  @Override
  public RecipeInput[] getInputs() {
    return recipe.getInputs();
  }

  @Override
  public List<FluidStack> getInputFluidStacks() {
    return Collections.emptyList();
  }
  
  private ItemStack getRecipeComponentFromInput(ItemStack input) {
    if(input == null) {
      return null;
    }
    for (RecipeInput ri : recipe.getInputs()) {
      if(ri.isInput(input)) {
        return ri.getInput();
      }
    }
    return null;
  }

  @Override
  public List<List<ItemStack>> getInputStackAlternatives() {
    return recipe.getInputStackAlternatives();
  }

  @Override
  public String toString() {
    return "BasicManyToOneRecipe [output=" + output + ", recipe=" + recipe + "]";
  }
  
}
