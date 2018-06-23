package crazypants.enderio.base.recipe.mapomatic;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.recipe.IRecipeInput;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.ManyToOneMachineRecipe;
import crazypants.enderio.base.recipe.ManyToOneRecipeManager;
import crazypants.enderio.base.recipe.Recipe;
import crazypants.enderio.base.recipe.RecipeBonusType;
import crazypants.enderio.base.recipe.RecipeOutput;
import net.minecraft.item.ItemStack;

public class MapOMaticRecipeManager {

  static final @Nonnull MapOMaticRecipeManager instance = new MapOMaticRecipeManager();

  @Nonnull
  public static MapOMaticRecipeManager getInstance() {
    return instance;
  }

  public MapOMaticRecipeManager() {
    super("Map'o'Matic");
  }

  public void create() {
    MachineRecipeRegistry.instance.registerRecipe(MachineRecipeRegistry.MAPOMATIC,
        new MapOMaticRecipe("MapOMatic", MachineRecipeRegistry.MAPOMATIC, this));
  }

  public void addRecipe(@Nonnull NNList<IRecipeInput> input, @Nonnull ItemStack output, int energyCost) {
    RecipeOutput recipeOutput = new RecipeOutput(output, 1);
    addRecipe(new Recipe(recipeOutput, energyCost, RecipeBonusType.NONE, input.toArray(new IRecipeInput[input.size()])));
  }


}
