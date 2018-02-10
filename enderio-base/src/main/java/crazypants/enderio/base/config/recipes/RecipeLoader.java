package crazypants.enderio.base.config.recipes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Comparator;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Triple;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.addon.IEnderIOAddon;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.config.recipes.xml.Recipes;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

public class RecipeLoader {

  private RecipeLoader() {
  }

  public static void addRecipes() {
    final RecipeFactory recipeFactory = new RecipeFactory(Config.getConfigDirectory(), EnderIO.DOMAIN);
    NNList<Triple<Integer, RecipeFactory, String>> recipeFiles = new NNList<>();

    recipeFiles.add(Triple.of(0, recipeFactory, "aliases"));
    recipeFiles.add(Triple.of(1, recipeFactory, "materials"));
    recipeFiles.add(Triple.of(1, recipeFactory, "items"));
    recipeFiles.add(Triple.of(1, recipeFactory, "base"));

    for (ModContainer modContainer : Loader.instance().getModList()) {
      Object mod = modContainer.getMod();
      if (mod instanceof IEnderIOAddon) {
        recipeFiles.addAll(((IEnderIOAddon) mod).getRecipeFiles());
      }
    }

    Collections.sort(recipeFiles, new Comparator<Triple<Integer, RecipeFactory, String>>() {
      @Override
      public int compare(Triple<Integer, RecipeFactory, String> o1, Triple<Integer, RecipeFactory, String> o2) {
        return o1.getLeft().compareTo(o2.getLeft());
      }
    });

    for (Triple<Integer, RecipeFactory, String> triple : recipeFiles) {
      addRecipes(NullHelper.first(triple.getMiddle(), recipeFactory), triple.getRight());
    }

    for (ModContainer modContainer : Loader.instance().getModList()) {
      Object mod = modContainer.getMod();
      if (mod instanceof IEnderIOAddon) {
        ((IEnderIOAddon) mod).postRecipeRegistration();
      }
    }
  }

  public static void addIMCRecipe(String recipe) throws XMLStreamException, IOException {
    try (InputStream is = IOUtils.toInputStream(recipe, Charset.forName("UTF-8"))) {
      Recipes recipes = RecipeFactory.readStax(new Recipes(), "recipes", is);
      if (recipes.isValid()) {
        recipes.enforceValidity();
        recipes.register("IMC recipes");
        return;
      }
      throw new InvalidRecipeConfigException("empty XML");
    }
  }

  private static void addRecipes(RecipeFactory recipeFactory, String filename) {
    try {
      Recipes recipes = recipeFactory.readFile(new Recipes(), "recipes", "recipe_" + filename);
      if (recipes.isValid()) {
        recipes.enforceValidity();
        recipes.register(NullHelper.first(filename, "(unnamed)"));
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
