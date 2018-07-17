package crazypants.enderio.base.recipe.basin;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.recipe.IRecipeInput;
import crazypants.enderio.base.recipe.MachineRecipeInput;
import crazypants.enderio.base.recipe.Recipe;
import crazypants.enderio.base.recipe.RecipeBonusType;
import crazypants.enderio.base.recipe.RecipeOutput;
import net.minecraft.util.EnumFacing.Plane;

public class BasinRecipe extends Recipe {
  
  private final Plane orientation;

  public BasinRecipe(IRecipeInput inputA, IRecipeInput inputB, RecipeOutput output, Plane orientation, int energy) {
    super(output, energy, RecipeBonusType.NONE, inputA, inputB);
    this.orientation = orientation;
  }
  
  public Plane getOrientation() {
    return orientation;
  }
  
  @Override
  public boolean isInputForRecipe(NNList<MachineRecipeInput> machineInputs) {
    if (machineInputs.size() != 4) {
      return false;
    }
    NNList<MachineRecipeInput> inputs = new NNList<>();
    if (orientation == Plane.VERTICAL) {
      inputs.add(machineInputs.get(0));
      inputs.add(machineInputs.get(1));
    } else {
      inputs.add(machineInputs.get(2));
      inputs.add(machineInputs.get(3));
    }
    return super.isInputForRecipe(inputs);
  }
  
  @Override
  protected boolean needsExactMatch() {
    return true;
  }
}
