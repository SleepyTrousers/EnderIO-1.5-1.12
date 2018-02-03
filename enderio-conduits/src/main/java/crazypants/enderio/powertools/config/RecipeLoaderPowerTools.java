package crazypants.enderio.powertools.config;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.recipes.RecipeFactory;
import crazypants.enderio.base.config.recipes.RecipeLoader;

public class RecipeLoaderPowerTools {

  private static final String[] RECIPE_FILES = { "powertools" };

  private RecipeLoaderPowerTools() {
  }

  public static void addRecipes() {
    RecipeLoader.addRecipes(new RecipeFactory(ConfigHandler.getConfigDirectory(), EnderIO.DOMAIN), RECIPE_FILES);
  }

}
