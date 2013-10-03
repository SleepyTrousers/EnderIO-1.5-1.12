package crazypants.enderio.crafting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;

public final class RecipeReigistry {

  public static final RecipeReigistry instance = new RecipeReigistry();

  private final Map<String, List<IEnderIoRecipe>> crafterRecipes = new HashMap<String, List<IEnderIoRecipe>>();

  public void registerRecipe(IEnderIoRecipe recipe) {
    List<IEnderIoRecipe> recipes = getRecipesForCrafter(recipe.getCrafterId());
    recipes.add(recipe);
  }

  public void registerRecipes(List<IEnderIoRecipe> recipes) {
    for (IEnderIoRecipe recipe : recipes) {
      registerRecipe(recipe);
    }
  }

  public List<IEnderIoRecipe> getRecipesForOutput(String crafterId, ItemStack output) {
    return getRecipesForOutput(crafterId, output, null);
  }

  public List<IEnderIoRecipe> getRecipesForOutput(ItemStack output) {
    ArrayList<IEnderIoRecipe> result = new ArrayList<IEnderIoRecipe>();
    for (String crafter : crafterRecipes.keySet()) {
      getRecipesForOutput(crafter, output, result);
    }
    return result;
  }

  public List<IEnderIoRecipe> getRecipesForOutput(String crafterId, ItemStack output, List<IEnderIoRecipe> result) {
    if(result == null) {
      result = new ArrayList<IEnderIoRecipe>();
    }
    List<IEnderIoRecipe> recipes = getRecipesForCrafter(crafterId);
    for (IEnderIoRecipe recipe : recipes) {
      if(recipe.isOutput(output)) {
        result.add(recipe);
      }
    }
    return result;
  }

  public List<IEnderIoRecipe> getRecipesForCrafter(String crafterId) {
    List<IEnderIoRecipe> result = crafterRecipes.get(crafterId);
    if(result == null) {
      result = new ArrayList<IEnderIoRecipe>();
      crafterRecipes.put(crafterId, result);
    }
    return result;
  }

  private RecipeReigistry() {
  }

}
