package crazypants.enderio.base.recipe.sagmill;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.recipe.AbstractMachineRecipe;
import crazypants.enderio.base.recipe.IRecipe;
import crazypants.enderio.base.recipe.MachineRecipeInput;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.RecipeBonusType;

public class SagMillMachineRecipe extends AbstractMachineRecipe {

  @Override
  public @Nonnull String getUid() {
    return "CrusherRecipe";
  }

  @Override
  public IRecipe getRecipeForInputs(@Nonnull NNList<MachineRecipeInput> inputs) {
    return SagMillRecipeManager.instance.getRecipeForInput(inputs.get(0).item);
  }

  @Override
  public boolean isValidInput(@Nonnull MachineRecipeInput input) {
    return SagMillRecipeManager.instance.isValidInput(input);
  }

  @Override
  public @Nonnull String getMachineName() {
    return MachineRecipeRegistry.SAGMILL;
  }

  @Override
  public @Nonnull RecipeBonusType getBonusType(@Nonnull NNList<MachineRecipeInput> inputs) {
    if (inputs.size() <= 0) {
      return RecipeBonusType.NONE;
    }
    IRecipe recipe = getRecipeForInputs(inputs);
    if (recipe == null) {
      return RecipeBonusType.NONE;
    } else {
      return recipe.getBonusType().withoutMultiply(SagMillRecipeManager.getInstance().isExcludedFromBallBonus(inputs));
    }
  }

}
