package crazypants.enderio.machine.sagmill;

import crazypants.enderio.machine.MachineObject;
import crazypants.enderio.recipe.AbstractMachineRecipe;
import crazypants.enderio.recipe.MachineRecipeInput;

public class SagMillMachineRecipe extends AbstractMachineRecipe {

  @Override
  public String getUid() {
    return "CrusherRecipe";
  }

  @Override
  public IRecipe getRecipeForInputs(MachineRecipeInput[] inputs) {
    return SagMillRecipeManager.instance.getRecipeForInput(inputs[0].item);
  }

  @Override
  public boolean isValidInput(MachineRecipeInput input) {
    if (input == null) {
      return false;
    }
    return SagMillRecipeManager.instance.isValidInput(input);
  }

  @Override
  public String getMachineName() {
    return MachineObject.blockSagMill.getUnlocalisedName();
  }

}
