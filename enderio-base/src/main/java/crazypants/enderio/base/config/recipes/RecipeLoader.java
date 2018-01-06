package crazypants.enderio.base.config.recipes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.IOUtils;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.config.recipes.xml.Recipes;

public class RecipeLoader {

  private static final String[] RECIPE_FILES = { "aliases", "materials", "items", "base" };

  private RecipeLoader() {
  }

  public static void addRecipes() {
    addRecipes(new RecipeFactory(Config.getConfigDirectory(), EnderIO.DOMAIN), RECIPE_FILES);
  }

  public static void addIMCRecipe(String recipe) throws XMLStreamException, IOException {
    try (InputStream is = IOUtils.toInputStream(recipe, Charset.forName("UTF-8"))) {
      Recipes recipes = RecipeFactory.readStax(new Recipes(), "recipes", is);
      if (recipes.isValid()) {
        recipes.enforceValidity();
        recipes.register();
        return;
      }
      throw new InvalidRecipeConfigException("empty XML");
    }
  }

  public static void addRecipes(RecipeFactory recipeFactory, String... files) {
    for (String filename : files) {
      try {
        Recipes recipes = recipeFactory.readFile(new Recipes(), "recipes", "recipe_" + filename);
        if (recipes.isValid()) {
          recipes.enforceValidity();
          recipes.register();
        } else {
          recipeError(filename, "File is empty or invalid");
        }
      } catch (InvalidRecipeConfigException e) {
        recipeError(NullHelper.first(e.getFilename(), filename), e.getMessage());
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
    String fileref = filename.startsWith("recipe_") ? filename : "recipe_" + filename + "_core.xml or recipe_" + filename + "_user.xml";
    EnderIO.proxy.stopWithErrorScreen( //
        "=======================================================================", //
        "== ENDER IO FATAL ERROR ==", //
        "=======================================================================", //
        "Cannot register recipes as configured. This means that either", //
        "your custom config file has an error or another mod does bad", //
        "things to vanilla items or the Ore Dictionary.", //
        "=======================================================================", //
        "== Bad file ==", //
        fileref, //
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
