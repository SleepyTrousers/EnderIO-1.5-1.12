package crazypants.enderio.base.recipe.alloysmelter;

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

public class AlloyRecipeManager extends ManyToOneRecipeManager {

  static final @Nonnull AlloyRecipeManager instance = new AlloyRecipeManager();

  public static AlloyRecipeManager getInstance() {
    return instance;
  }

  @Nonnull
  VanillaSmeltingRecipe vanillaRecipe = new VanillaSmeltingRecipe();

  public AlloyRecipeManager() {
    super("Alloy Smelter");
  }

  public @Nonnull VanillaSmeltingRecipe getVanillaRecipe() {
    return vanillaRecipe;
  }

  public void loadRecipesFromConfig() {
    MachineRecipeRegistry.instance.registerRecipe(MachineRecipeRegistry.ALLOYSMELTER,
        new ManyToOneMachineRecipe("AlloySmelterRecipe", MachineRecipeRegistry.ALLOYSMELTER, this));
    // vanilla alloy furnace recipes
    MachineRecipeRegistry.instance.registerRecipe(MachineRecipeRegistry.ALLOYSMELTER, vanillaRecipe);
  }

  public void addRecipe(@Nonnull NNList<IRecipeInput> input, @Nonnull ItemStack output, int energyCost, float xpChance) {
    RecipeOutput recipeOutput = new RecipeOutput(output, 1, xpChance);
    addRecipe(new Recipe(recipeOutput, energyCost, RecipeBonusType.NONE, input.toArray(new IRecipeInput[input.size()])));
  }

}
