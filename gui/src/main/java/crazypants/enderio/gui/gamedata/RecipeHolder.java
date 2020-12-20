package crazypants.enderio.gui.gamedata;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import crazypants.enderio.gui.xml.Recipes;
import crazypants.enderio.gui.xml.ResourceLocation;
import crazypants.enderio.gui.xml.reader.RecipeReader;

public class RecipeHolder {

  public static final RecipeHolder CORE = new RecipeHolder();

  private Recipes core = new Recipes();

  public static List<String> readCore() {
    CORE.core = new Recipes();
    AliasRepository.startDoingCore();
    RecipeReader recipeReader = new RecipeReader();
    for (ResourceLocation corefile : ValueRepository.COREFILES.getAllResourceLocations()) {
      recipeReader.readCoreFile(CORE.core, "recipes", corefile);
    }
    CORE.fireCallbacks();
    return recipeReader.getErrors();
  }

  public Recipes getRecipes() {
    return core;
  }

  private List<Runnable> callbacks = new ArrayList<>();

  private void fireCallbacks() {
    callbacks.forEach(r -> EventQueue.invokeLater(r));
  }

  public void registerCallback(Runnable callback) {
    callbacks.add(callback);
  }

}
