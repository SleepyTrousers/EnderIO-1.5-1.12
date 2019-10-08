package crazypants.enderio.base.config.recipes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.addon.IEnderIOAddon;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.config.RecipeConfig;
import crazypants.enderio.base.config.recipes.IRecipeRoot.Overrides;
import crazypants.enderio.base.config.recipes.xml.AbstractConditional;
import crazypants.enderio.base.config.recipes.xml.Aliases;
import crazypants.enderio.base.config.recipes.xml.Recipes;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ProgressManager;

public final class RecipeLoader {

  private static final @Nonnull String EXT = ".xml";
  private static final @Nonnull String RECIPES_ROOT = "recipes";
  private static final @Nonnull String RECIPES_USER = RECIPES_ROOT + "/user";
  private static final @Nonnull String RECIPES_EXAMPLES = RECIPES_ROOT + "/examples";

  private enum IMCTYPE {
    XML,
    FILE;
  }

  private static List<Pair<String, Pair<IMCTYPE, String>>> imcRecipes = new ArrayList<>();

  private RecipeLoader() {
  }

  public static void addRecipes() {
    ProgressManager.ProgressBar bar = ProgressManager.push("XML Recipes", 11, true);
    final RecipeFactory recipeFactory = new RecipeFactory(EnderIO.getConfigHandler().getConfigDirectory(), EnderIO.DOMAIN);

    bar.step("Preparing Config Folder"); // 1
    recipeFactory.cleanFolder(RECIPES_ROOT);
    recipeFactory.createFolder(RECIPES_ROOT);
    recipeFactory.createFolder(RECIPES_USER);
    recipeFactory.createFolder(RECIPES_EXAMPLES);
    recipeFactory.placeXSD(RECIPES_ROOT);
    recipeFactory.placeXSD(RECIPES_USER);
    recipeFactory.placeXSD(RECIPES_EXAMPLES);

    recipeFactory.createFileUser("recipes/user/user_recipes.xml");

    NNList<Triple<Integer, RecipeFactory, String>> coreFiles = new NNList<>();

    bar.step("Collecting Sub-Mods/Addons"); // 2
    final List<ModContainer> modList = Loader.instance().getModList();
    ProgressManager.ProgressBar bar2 = ProgressManager.push("Mod", modList.size());
    for (ModContainer modContainer : modList) {
      bar2.step(modContainer.getName());
      Object mod = modContainer.getMod();
      if (mod instanceof IEnderIOAddon) {
        coreFiles.addAll(((IEnderIOAddon) mod).getRecipeFiles());
        for (String filename : ((IEnderIOAddon) mod).getExampleFiles()) {
          recipeFactory.copyCore(RECIPES_EXAMPLES + "/" + filename + EXT);
        }
      }
    }
    ProgressManager.pop(bar2);

    Collections.sort(coreFiles, new Comparator<Triple<Integer, RecipeFactory, String>>() {
      @Override
      public int compare(Triple<Integer, RecipeFactory, String> o1, Triple<Integer, RecipeFactory, String> o2) {
        return o1.getLeft().compareTo(o2.getLeft());
      }
    });

    bar.step("Collecting User Files"); // 3
    Set<File> userfiles = new HashSet<>(recipeFactory.listXMLFiles(RECIPES_USER));
    for (Triple<Integer, RecipeFactory, String> triple : coreFiles) {
      RecipeFactory factory = triple.getMiddle();
      if (factory != null) {
        userfiles.addAll(factory.listXMLFiles(RECIPES_USER));
      }
    }

    /*
     * Do a first pass just for aliases. Always load core aliases, even if core recipes are disabled.
     */

    bar.step("Core Aliases"); // 4
    bar2 = ProgressManager.push("File", coreFiles.size());
    for (Triple<Integer, RecipeFactory, String> triple : coreFiles) {
      bar2.step(triple.getRight());
      readCoreFile(new Aliases(), NullHelper.first(triple.getMiddle(), recipeFactory), RECIPES_ROOT + "/" + triple.getRight());
    }
    ProgressManager.pop(bar2);

    bar.step("IMC Aliases"); // 5
    if (imcRecipes != null) {
      handleIMCRecipes(Aliases.class, new Aliases());
    }

    bar.step("User Aliases"); // 6
    bar2 = ProgressManager.push("File", userfiles.size());
    for (File file : userfiles) {
      bar2.step(file.getName());
      readUserFile(new Aliases(), recipeFactory, file.getName(), file);
    }
    ProgressManager.pop(bar2);

    /*
     * Note that we load the recipes in core-imc-user order but merge them in reverse order. The loading order allows aliases to be added in the expected order,
     * while the reverse merging allows user recipes to replace imc recipes to replace core recipes.
     */

    bar.step("Core Recipes"); // 7
    Recipes config = new Recipes();
    if (RecipeConfig.loadCoreRecipes.get()) {
      try {
        bar2 = ProgressManager.push("File", coreFiles.size());
        for (Triple<Integer, RecipeFactory, String> triple : coreFiles) {
          bar2.step(triple.getRight());
          config = readCoreFile(new Recipes(), NullHelper.first(triple.getMiddle(), recipeFactory), RECIPES_ROOT + "/" + triple.getRight()).addRecipes(config,
              Overrides.DENY);
        }
        ProgressManager.pop(bar2);
      } catch (InvalidRecipeConfigException e) {
        recipeError(NullHelper.first(e.getFilename(), "Core Recipes"), e.getMessage());
      }
    } else {
      Log.warn("Ender IO core recipe loading has been disabled in the configuration.");
      Log.warn("This is valid, but do NOT report recipe errors to the Ender IO team!");
    }

    bar.step("IMC Recipes"); // 8
    if (imcRecipes != null) {
      config = handleIMCRecipes(Recipes.class, config);
      imcRecipes = null;
    }

    bar.step("User Recipes"); // 9
    bar2 = ProgressManager.push("File", userfiles.size());
    for (File file : userfiles) {
      bar2.step(file.getName());
      final IRecipeRoot userFile = readUserFile(new Recipes(), recipeFactory, file.getName(), file);
      if (userFile != null) {
        try {
          config = userFile.addRecipes(config, Overrides.ALLOW);
        } catch (InvalidRecipeConfigException e) {
          recipeError(NullHelper.first(e.getFilename(), file.getName()), e.getMessage());
        }
      }
    }
    ProgressManager.pop(bar2);

    bar.step("Registering Recipes"); // 10
    config.register("");

    bar.step("Post Registration"); // 11
    bar2 = ProgressManager.push("Mod", modList.size());
    for (ModContainer modContainer : modList) {
      bar2.step(modContainer.getName());
      Object mod = modContainer.getMod();
      if (mod instanceof IEnderIOAddon) {
        ((IEnderIOAddon) mod).postRecipeRegistration();
      }
    }
    ProgressManager.pop(bar2);

    ProgressManager.pop(bar);
  }

  private static @Nonnull <T extends IRecipeRoot> T handleIMCRecipes(@Nonnull Class<T> target, @Nonnull T config) {
    try {
      ProgressManager.ProgressBar bar = ProgressManager.push("IMC", imcRecipes.size());
      Map<String, T> targets = new HashMap<>();
      for (Pair<String, Pair<IMCTYPE, String>> recipe : imcRecipes) {
        bar.step(recipe.getKey());
        try {
          T recipes = targets.containsKey(recipe.getKey()) ? targets.get(recipe.getKey()) : target.newInstance();
          if (recipe.getValue().getKey() == IMCTYPE.XML) {
            try (InputStream is = IOUtils.toInputStream(recipe.getValue().getValue(), Charset.forName("UTF-8"))) {
              recipes = RecipeFactory.readStax(recipes, RECIPES_ROOT, is, "IMC from mod '" + recipe.getKey() + "'");
            }
          } else { // IMCTYPE.FILE
            recipes = RecipeFactory.readFileIMC(recipes, RECIPES_ROOT, recipe.getValue().getValue());
          }
          targets.put(recipe.getKey(), recipes);
        } catch (InvalidRecipeConfigException e) {
          Log.error("Invalid recipe while parsing IMC:");
          e.printStackTrace();
          Log.error("IMC message:\n" + recipe.getValue().getValue());
          recipeError(NullHelper.first(e.getFilename(), "IMC from the mod '" + recipe.getKey() + "'"), e.getMessage());
        } catch (IOException e) {
          Log.error("IO error while parsing IMC:");
          e.printStackTrace();
          Log.error("IMC message:\n" + recipe.getValue().getValue());
          recipeError("IMC from the mod '" + recipe.getKey() + "'", "IO error while parsing string: " + e.getMessage());
        } catch (XMLStreamException e) {
          Log.error("IMC has malformed XML:");
          e.printStackTrace();
          Log.error("IMC message:\n" + recipe.getValue().getValue());
          recipeError("IMC from the mod '" + recipe.getKey() + "'", "IMC has malformed XML: " + e.getMessage());
        }
      }
      ProgressManager.pop(bar);

      bar = ProgressManager.push("IMC", targets.size());
      @Nonnull
      T collector = NullHelper.notnullJ(target.newInstance(), "Class.newInstance()");
      for (Entry<String, T> entry : targets.entrySet()) {
        bar.step(entry.getKey());
        try {
          entry.getValue().enforceValidity();
          collector = entry.getValue().addRecipes(collector, Overrides.WARN);
        } catch (InvalidRecipeConfigException e) {
          Log.error("Invalid recipe while parsing IMC:");
          e.printStackTrace();
          recipeError(NullHelper.first(e.getFilename(), "IMC from the mod '" + entry.getKey() + "'"), e.getMessage());
        }
      }
      ProgressManager.pop(bar);

      List<AbstractConditional> list = collector.getRecipes();
      if (!list.isEmpty()) {
        Log.info("Valid IMC recipes to be processed:");
        for (AbstractConditional recipe : list) {
          Log.info(" * " + recipe.getName() + " from " + recipe.getSource());
        }
      }
      try {
        return collector.addRecipes(config, Overrides.ALLOW);
      } catch (InvalidRecipeConfigException e) {
        // no valid errors expected at this time
        throw new RuntimeException(e);
      }
    } catch (InstantiationException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  private static <T extends IRecipeRoot> T readUserFile(T target, final RecipeFactory recipeFactory, String filename, File file) {
    try {
      final T recipes = RecipeFactory.readFileUser(target, RECIPES_ROOT, filename, file);
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
      recipeError(filename, "IO error while reading file: " + e.getMessage());
    } catch (XMLStreamException e) {
      Log.error("File has malformed XML:");
      e.printStackTrace();
      recipeError(filename, "File has malformed XML: " + e.getMessage());
    }
    return null;
  }

  private static <T extends IRecipeRoot> T readCoreFile(T target, final RecipeFactory recipeFactory, String filename) {
    try {
      final T recipes = recipeFactory.readCoreFile(target, RECIPES_ROOT, filename + EXT);
      if (recipes.isValid()) {
        recipes.enforceValidity();
        return recipes;
      } else {
        recipeError(filename, "File is empty or invalid");
      }
    } catch (InvalidRecipeConfigException e) {
      recipeError(NullHelper.first(e.getFilename(), filename + EXT), e.getMessage());
    } catch (IOException e) {
      Log.error("IO error while reading file:");
      e.printStackTrace();
      recipeError(filename + EXT, "IO error while reading file: " + e.getMessage());
    } catch (XMLStreamException e) {
      Log.error("File has malformed XML:");
      e.printStackTrace();
      recipeError(filename + EXT, "File has malformed XML: " + e.getMessage());
    }
    return target;
  }

  public static void addIMCRecipe(String sender, boolean isFile, String recipe) throws XMLStreamException, IOException {
    if (imcRecipes != null) {
      imcRecipes.add(Pair.of(sender, Pair.of(isFile ? IMCTYPE.FILE : IMCTYPE.XML, recipe)));
    } else {
      try {
        IRecipeRoot recipes;
        if (!isFile) {
          try (InputStream is = IOUtils.toInputStream(recipe, Charset.forName("UTF-8"))) {
            recipes = RecipeFactory.readStax(new Recipes(), RECIPES_ROOT, is, "IMC from mod '" + sender + "'");
          }
        } else {
          recipes = RecipeFactory.readFileIMC(new Recipes(), RECIPES_ROOT, recipe);
        }
        recipes.enforceValidity();
        recipes.register("IMC recipes");
        return;
      } catch (InvalidRecipeConfigException e) {
        recipeError(recipe + " (IMC from other mod)", e.getMessage());
      } catch (IOException e) {
        Log.error("IO error while parsing string:");
        e.printStackTrace();
        recipeError("IMC from other mod", "IO error while parsing string: " + e.getMessage());
      } catch (XMLStreamException e) {
        Log.error("IMC has malformed XML:");
        e.printStackTrace();
        recipeError("IMC from other mod", "IMC has malformed XML: " + e.getMessage());
      }
    }
  }

  private static void recipeError(String filename, String message) {
    if (RecipeConfig.loadCoreRecipes.get()) {
      EnderIO.proxy.stopWithErrorScreen( //
          "=======================================================================", //
          "== ENDER IO FATAL RECIPE ERROR ==", //
          "=======================================================================", //
          "Cannot register recipes as configured. This means that either", //
          "your custom recipe files have an error or another mod does bad", //
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
          "Note: If this is a modpack, report to the modpack author, not to", //
          "the Ender IO team.", //
          "=======================================================================" //
      );
    } else {
      EnderIO.proxy.stopWithErrorScreen( //
          "=======================================================================", //
          "== ENDER IO FATAL RECIPE ERROR ==", //
          "=======================================================================", //
          "Cannot register recipes as configured. This means that your custom ", //
          "recipe files have an error.", //
          "=======================================================================", //
          "== Bad file ==", //
          filename, //
          "=======================================================================", //
          "== Error Message ==", //
          message, //
          "=======================================================================", //
          "", //
          "=======================================================================", //
          "Do NOT report this to the Ender IO team. If this is a modpack, report", //
          "to the modpack author. If not, YOU made a mistake.", //
          "=======================================================================" //
      );
    }
  }

}
