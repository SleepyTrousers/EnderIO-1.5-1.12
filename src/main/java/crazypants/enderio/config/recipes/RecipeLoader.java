package crazypants.enderio.config.recipes;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import crazypants.enderio.EnderIO;
import crazypants.enderio.Log;
import crazypants.enderio.config.recipes.xml.Recipes;

public class RecipeLoader {

  private static final String[] RECIPE_FILES = { "aliases", "materials", "items", "machines" };

  private RecipeLoader() {
  }

  public static void addRecipes() {
    for (String filename : RECIPE_FILES) {
      try {
        Recipes recipes = RecipeFactory.readFile(new Recipes(), "recipes", "recipe_" + filename);
        if (recipes.isValid()) {
          recipes.enforceValidity();
          recipes.register();
        } else {
          recipeError(filename, "File is empty or invalid");
        }
      } catch (InvalidRecipeConfigException e) {
        recipeError(filename, e.getMessage());
      } catch (IOException e) {
        Log.error("IO error while reading file:");
        e.printStackTrace();
        recipeError(filename, "IO error while reading file:" + e.getMessage());
      } catch (XMLStreamException e) {
        Log.error("File has malformed XML:");
        e.printStackTrace();
        recipeError(filename, "File has malformed XML:" + e.getMessage());
      }
    }
  }

  private static void recipeError(String filename, String message) {
    EnderIO.proxy.stopWithErrorScreen( //
        "=======================================================================", //
        "== ENDER IO FATAL ERROR ==", //
        "=======================================================================", //
        "Cannot register recipes as configured. This means that either", //
        "your custom config file has an error or another mod does bad", //
        "things to vanilla items or the Ore Dictionary.", //
        "=======================================================================", //
        "== Bad file ==", //
        "recipe_" + filename + "_core.xml or recipe_" + filename + "_user.xml", //
        "=======================================================================", //
        "== Error Message ==", //
        message, //
        "=======================================================================", //
        "", //
        "=======================================================================", //
        "Note: To start the game anyway, you can disable recipe loading in the", //
        "Ender IO config file. However, then all of Ender IO's crafting recipes", //
        "will be missing.", //
        "=======================================================================" //
    );
  }

}
