package crazypants.enderio.machine.crusher;

import crazypants.enderio.ModObject;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.recipe.AbstractMachineRecipe;
import crazypants.enderio.machine.recipe.IRecipe;

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
    return ModObject.blockSagMill.unlocalisedName;
  }

  //  @Override
  //  public List<IEnderIoRecipe> getAllRecipes() {
  //    List<IEnderIoRecipe> result = new ArrayList<IEnderIoRecipe>();
  //    List<Recipe> recipes = CrusherRecipeManager.getInstance().getRecipes();
  //    for (IRecipe cr : recipes) {
  //      List<IRecipeComponent> components = new ArrayList<IRecipeComponent>();
  //      for (crazypants.enderio.machine.recipe.RecipeInput ri : cr.getInputs()) {
  //        if(ri.getInput() != null) {
  //          IRecipeInput input = new RecipeInput(ri.getInput(), -1, ri.getEquivelentInputs());
  //          components.add(input);
  //        }
  //      }
  //      for (RecipeOutput co : cr.getOutputs()) {
  //        IRecipeOutput output = new crazypants.enderio.crafting.impl.RecipeOutput(co.getOutput(), co.getChance());
  //        components.add(output);
  //      }
  //      //    IRecipeInput input = new RecipeInput(CrusherRecipeManager.getInput(cr));
  //      //    List<IRecipeComponent> components = new ArrayList<IRecipeComponent>();
  //      //    components.add(input);
  //      result.add(new EnderIoRecipe(IEnderIoRecipe.SAG_MILL_ID, cr.getEnergyRequired(), components));
  //    }
  //    return result;
  //  }
}
