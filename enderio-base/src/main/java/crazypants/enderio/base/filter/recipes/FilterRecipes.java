package crazypants.enderio.base.filter.recipes;

import net.minecraftforge.fml.common.registry.GameRegistry;

public class FilterRecipes {

  public static void addRecipes() {
    GameRegistry.addRecipe(new ClearFilterRecipe());
    GameRegistry.addRecipe(new CopyFilterRecipe());
  }

}
