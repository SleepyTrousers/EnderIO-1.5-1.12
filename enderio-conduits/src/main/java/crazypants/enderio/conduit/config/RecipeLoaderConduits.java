package crazypants.enderio.conduit.config;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.recipes.RecipeFactory;
import crazypants.enderio.base.config.recipes.RecipeLoader;

public class RecipeLoaderConduits {

  private static final String[] RECIPE_FILES = { "conduits" };

  private RecipeLoaderConduits() {
  }

  public static void addRecipes() {
    RecipeLoader.addRecipes(new RecipeFactory(ConfigHandler.getConfigDirectory(), EnderIO.DOMAIN), RECIPE_FILES);
  }

}
