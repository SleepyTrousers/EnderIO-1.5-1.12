package crazypants.enderio.base.config.recipes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Triple;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.addon.IEnderIOAddon;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.config.config.RecipeConfig;
import crazypants.enderio.base.config.recipes.xml.Recipes;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

public class RecipeLoader {

  private static NNList<String> imcRecipes = new NNList<>();

  private RecipeLoader() {
  }

  public static void addRecipes() {
    final RecipeFactory recipeFactory = new RecipeFactory(Config.getConfigDirectory(), EnderIO.DOMAIN);

    recipeFactory.createFolder("recipes");
    recipeFactory.createFolder("recipes/user");
    recipeFactory.createFolder("recipes/examples");
    recipeFactory.placeXSD("recipes");
    recipeFactory.placeXSD("recipes/user");
    recipeFactory.placeXSD("recipes/examples");

    recipeFactory.createFileUser("recipes/user/user_recipes.xml");

    NNList<Triple<Integer, RecipeFactory, String>> recipeFiles = new NNList<>();

    for (ModContainer modContainer : Loader.instance().getModList()) {
      Object mod = modContainer.getMod();
      if (mod instanceof IEnderIOAddon) {
        recipeFiles.addAll(((IEnderIOAddon) mod).getRecipeFiles());
        for (String filename : ((IEnderIOAddon) mod).getExampleFiles()) {
          recipeFactory.copyCore("recipes/examples/" + filename + ".xml");
        }
      }
    }

    Collections.sort(recipeFiles, new Comparator<Triple<Integer, RecipeFactory, String>>() {
      @Override
      public int compare(Triple<Integer, RecipeFactory, String> o1, Triple<Integer, RecipeFactory, String> o2) {
        return o1.getLeft().compareTo(o2.getLeft());
      }
    });

    Set<File> userfiles = new HashSet<>(recipeFactory.listXMLFiles("recipes/user"));
    for (Triple<Integer, RecipeFactory, String> triple : recipeFiles) {
      RecipeFactory factory = triple.getMiddle();
      if (factory != null) {
        userfiles.addAll(factory.listXMLFiles("recipes/user"));
      }
    }

    /*
     * Note that we load the recipes in core-imc-user order but merge them in reverse order. The loading order allows aliases to be added in the expected order,
     * while the reverse merging allows user recipes to replace imc recipes to replace core recipes.
     */

    Recipes config = new Recipes();
    if (RecipeConfig.loadCoreRecipes.get()) {
      try {
        for (Triple<Integer, RecipeFactory, String> triple : recipeFiles) {
          config = readCoreFile(NullHelper.first(triple.getMiddle(), recipeFactory), "recipes/" + triple.getRight()).addRecipes(config, false);
        }
      } catch (InvalidRecipeConfigException e) {
        recipeError(NullHelper.first(e.getFilename(), "Core Recipes"), e.getMessage());
      }
    }

    if (imcRecipes != null) {
      config = handleIMCRecipes(config);
      imcRecipes = null;
    }

    for (File file : userfiles) {
      final Recipes userFile = readUserFile(recipeFactory, file.getName(), file);
      if (userFile != null) {
        try {
          config = userFile.addRecipes(config, true);
        } catch (InvalidRecipeConfigException e) {
          recipeError(NullHelper.first(e.getFilename(), file.getName()), e.getMessage());
        }
      }
    }

    config.register("");

    for (ModContainer modContainer : Loader.instance().getModList()) {
      Object mod = modContainer.getMod();
      if (mod instanceof IEnderIOAddon) {
        ((IEnderIOAddon) mod).postRecipeRegistration();
      }
    }
  }

  private static Recipes handleIMCRecipes(Recipes config) {
    for (String recipe : imcRecipes) {
      try (InputStream is = IOUtils.toInputStream(recipe, Charset.forName("UTF-8"))) {
        Recipes recipes = RecipeFactory.readStax(new Recipes(), "recipes", is);
        recipes.enforceValidity();
        config = recipes.addRecipes(config, true);
      } catch (InvalidRecipeConfigException e) {
        recipeError(NullHelper.first(e.getFilename(), "IMC from other mod"), e.getMessage());
      } catch (IOException e) {
        Log.error("IO error while parsing string:");
        e.printStackTrace();
        recipeError("IMC from other mod", "IO error while parsing string:" + e.getMessage());
      } catch (XMLStreamException e) {
        Log.error("IMC has malformed XML:");
        e.printStackTrace();
        recipeError("IMC from other mod", "IMC has malformed XML:" + e.getMessage());
      }
    }
    return config;
  }

  private static Recipes readUserFile(final RecipeFactory recipeFactory, String filename, File file) {
    try {
      final Recipes recipes = RecipeFactory.readFileUser(new Recipes(), "recipes", filename, file);
      if (recipes.isValid()) {
        recipes.enforceValidity();
        return recipes;
      } else {
        // empty user files are not really an issue, especially as the default file we create is empty...
        // recipeError(filename, "File is empty or invalid");
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
    return null;
  }

  private static Recipes readCoreFile(final RecipeFactory recipeFactory, String filename) {
    try {
      final Recipes recipes = recipeFactory.readCoreFile(new Recipes(), "recipes", filename + ".xml");
      if (recipes.isValid()) {
        recipes.enforceValidity();
        return recipes;
      } else {
        recipeError(filename, "File is empty or invalid");
      }
    } catch (InvalidRecipeConfigException e) {
      recipeError(NullHelper.first(e.getFilename(), filename + ".xml"), e.getMessage());
    } catch (IOException e) {
      Log.error("IO error while reading file:");
      e.printStackTrace();
      recipeError(filename + ".xml", "IO error while reading file:" + e.getMessage());
    } catch (XMLStreamException e) {
      Log.error("File has malformed XML:");
      e.printStackTrace();
      recipeError(filename + ".xml", "File has malformed XML:" + e.getMessage());
    }
    return new Recipes();
  }

  public static void addIMCRecipe(String recipe) throws XMLStreamException, IOException {
    if (imcRecipes != null) {
      imcRecipes.add(recipe);
    } else {
      try (InputStream is = IOUtils.toInputStream(recipe, Charset.forName("UTF-8"))) {
        Recipes recipes = RecipeFactory.readStax(new Recipes(), "recipes", is);
        recipes.enforceValidity();
        recipes.register("IMC recipes");
        return;
      } catch (InvalidRecipeConfigException e) {
        recipeError(recipe + " (IMC from other mod)", e.getMessage());
      } catch (IOException e) {
        Log.error("IO error while parsing string:");
        e.printStackTrace();
        recipeError("IMC from other mod", "IO error while parsing string:" + e.getMessage());
      } catch (XMLStreamException e) {
        Log.error("IMC has malformed XML:");
        e.printStackTrace();
        recipeError("IMC from other mod", "IMC has malformed XML:" + e.getMessage());
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
        filename, //
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
