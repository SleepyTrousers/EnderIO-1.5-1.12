package crazypants.enderio.machine.crusher;

import java.util.ArrayList;
import java.util.List;

import crazypants.enderio.ModObject;
import crazypants.enderio.crafting.IEnderIoRecipe;
import crazypants.enderio.crafting.IRecipeComponent;
import crazypants.enderio.crafting.IRecipeInput;
import crazypants.enderio.crafting.IRecipeOutput;
import crazypants.enderio.crafting.impl.EnderIoRecipe;
import crazypants.enderio.crafting.impl.RecipeInput;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.recipe.AbstractMachineRecipe;
import crazypants.enderio.machine.recipe.IRecipe;
import crazypants.enderio.machine.recipe.Recipe;
import crazypants.enderio.machine.recipe.RecipeOutput;

public class CrusherMachineRecipe extends AbstractMachineRecipe {

  @Override
  public String getUid() {
    return "CrusherRecipe";
  }

  @Override
  public IRecipe getRecipeForInputs(MachineRecipeInput[] inputs) {
    return CrusherRecipeManager.instance.getRecipeForInput(inputs[0].item);
  }

  @Override
  public boolean isValidInput(MachineRecipeInput input) {
    if(input == null) {
      return false;
    }
    return CrusherRecipeManager.instance.getRecipeForInput(input.item) != null;
  }

  @Override
  public String getMachineName() {
    return ModObject.blockCrusher.unlocalisedName;
  }

  @Override
  public List<IEnderIoRecipe> getAllRecipes() {
    List<IEnderIoRecipe> result = new ArrayList<IEnderIoRecipe>();
    List<Recipe> recipes = CrusherRecipeManager.getInstance().getRecipes();
    for (IRecipe cr : recipes) {
      List<IRecipeComponent> components = new ArrayList<IRecipeComponent>();
      for (crazypants.enderio.machine.recipe.RecipeInput ri : cr.getInputs()) {
        if(ri.getInput() != null) {
          IRecipeInput input = new RecipeInput(ri.getInput(), -1, ri.getEquivelentInputs());
          components.add(input);
        }
      }
      for (RecipeOutput co : cr.getOutputs()) {
        IRecipeOutput output = new crazypants.enderio.crafting.impl.RecipeOutput(co.getOutput(), co.getChance());
        components.add(output);
      }
      //    IRecipeInput input = new RecipeInput(CrusherRecipeManager.getInput(cr));
      //    List<IRecipeComponent> components = new ArrayList<IRecipeComponent>();
      //    components.add(input);
      result.add(new EnderIoRecipe(IEnderIoRecipe.SAG_MILL_ID, cr.getEnergyRequired(), components));
    }
    return result;
  }
}
