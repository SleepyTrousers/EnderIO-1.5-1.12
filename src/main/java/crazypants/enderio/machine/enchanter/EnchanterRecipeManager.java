package crazypants.enderio.machine.enchanter;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;

import crazypants.enderio.Log;
import crazypants.enderio.machine.alloy.AlloyRecipeManager;

public class EnchanterRecipeManager {

  private static EnchanterRecipeManager instance = new EnchanterRecipeManager();
  
  public static EnchanterRecipeManager getInstance() {
    return instance;
  }

  private final List<EnchanterRecipe> recipes = new ArrayList<EnchanterRecipe>();
    
  public void loadRecipesFromConfig() {
    List<EnchanterRecipe> res = EnchanterRecipeParser.loadRecipeConfig();
    if(res != null) {
      recipes.addAll(res);
    }
    Log.info("Loaded " + recipes.size() + " recipes for Enchanter");
  }

  public EnchanterRecipe getEnchantmentRecipeForInput(ItemStack itemStack) {
    if(itemStack == null) {
      return null;
    }
    for(EnchanterRecipe recipe : recipes) {
      if(recipe.isInput(itemStack)) {
        return recipe;
      }
    }
    return null;
  }

  public List<EnchanterRecipe> getRecipes() {
    return recipes;
    
  }

}
