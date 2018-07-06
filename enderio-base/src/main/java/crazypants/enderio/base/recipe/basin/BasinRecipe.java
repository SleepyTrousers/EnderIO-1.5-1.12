package crazypants.enderio.base.recipe.basin;

import crazypants.enderio.base.recipe.IRecipeInput;
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
}
