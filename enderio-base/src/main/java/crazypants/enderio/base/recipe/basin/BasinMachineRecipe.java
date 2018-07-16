package crazypants.enderio.base.recipe.basin;

import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.recipe.AbstractMachineRecipe;
import crazypants.enderio.base.recipe.IRecipe;
import crazypants.enderio.base.recipe.MachineRecipeInput;

public class BasinMachineRecipe extends AbstractMachineRecipe {

  @Override
  @Nonnull
  public String getUid() {
    return "Basin Combining";
  }

  @Override
  public boolean isValidInput(@Nonnull MachineRecipeInput input) {
    return BasinRecipeManager.getInstance().isValidInput(input);
  }

  @Override
  @Nonnull
  public String getMachineName() {
    return "Basin";
  }

  @Override
  public IRecipe getRecipeForInputs(@Nonnull NNList<MachineRecipeInput> inputs) {
    return BasinRecipeManager.getInstance().getRecipeMatchingInput(inputs.stream().map(i -> i.fluid).filter(Objects::nonNull).collect(Collectors.toCollection(NNList::new)));
  }

}
