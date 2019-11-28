package crazypants.enderio.zoo.spawn;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.recipe.RecipeConfig;
import crazypants.enderio.zoo.spawn.impl.SpawnEntry;

public class SpawnConfig {

  public static final @Nonnull String CONFIG_NAME_CORE = "SpawnConfig_Core.xml";
  public static final @Nonnull String CONFIG_NAME_USER = "SpawnConfig_User.xml";

  public static List<SpawnEntry> loadSpawnConfig() {
    File coreFile = new File(EnderIO.getConfigHandler().getConfigDirectory(), CONFIG_NAME_CORE);

    String defaultVals = null;
    try {
      defaultVals = RecipeConfig.readRecipes(coreFile, CONFIG_NAME_CORE, true);
    } catch (IOException e) {
      Log.error("Could not load core spawn config file " + CONFIG_NAME_CORE + " from jar: " + e.getMessage());
      e.printStackTrace();
      return null;
    }

    List<SpawnEntry> result;
    try {
      result = SpawnConfigParser.parseSpawnConfig(defaultVals);
    } catch (Exception e) {
      Log.error("Error parsing " + CONFIG_NAME_CORE + ":" + e);
      return Collections.emptyList();
    }
    Log.info("Loaded " + result.size() + " entries from core spawn config.");

    File userFile = new File(EnderIO.getConfigHandler().getConfigDirectory(), CONFIG_NAME_USER);
    try {
      String userText = RecipeConfig.readRecipes(userFile, CONFIG_NAME_USER, false);
      if (userText.trim().length() == 0) {
        Log.error("Empty user config file: " + userFile.getAbsolutePath());
      } else {
        List<SpawnEntry> userEntries = SpawnConfigParser.parseSpawnConfig(userText);
        Log.info("Loaded " + userEntries.size() + " entries from user spawn config.");
        merge(userEntries, result);
      }
    } catch (Exception e) {
      Log.error("Could not load user defined spawn entries from file: " + CONFIG_NAME_USER);
      e.printStackTrace();
    }

    return result;
  }

  private static void merge(List<SpawnEntry> userEntries, List<SpawnEntry> result) {
    for (SpawnEntry entry : userEntries) {
      removeFrom(entry, result);
      result.add(entry);
    }
  }

  private static void removeFrom(ISpawnEntry useEntry, List<SpawnEntry> result) {
    ISpawnEntry toRemove = null;
    for (ISpawnEntry entry : result) {
      if (useEntry.getId().equals(entry.getId())) {
        toRemove = entry;
        break;
      }
    }
    if (toRemove != null) {
      Log.info("Replace spawn config for " + toRemove.getId() + " with user supplied entry.");
      result.remove(toRemove);
    }
  }

}
