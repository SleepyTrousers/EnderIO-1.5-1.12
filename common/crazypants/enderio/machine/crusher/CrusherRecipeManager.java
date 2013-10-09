package crazypants.enderio.machine.crusher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import org.apache.commons.io.IOUtils;

import crazypants.enderio.Config;
import crazypants.enderio.Log;
import crazypants.util.Util;

public class CrusherRecipeManager {

  public static final int ORE_ENERGY_COST = 400;

  public static final int INGOT_ENERGY_COST = 240;

  private static final String CORE_FILE_NAME = "SAGMillRecipes_Core.xml";
  private static final String CUSTOM_FILE_NAME = "SAGMillRecipes_User.xml";

  static final CrusherRecipeManager instance = new CrusherRecipeManager();

  public static CrusherRecipeManager getInstance() {
    return instance;
  }

  private final List<CrusherRecipe> recipes = new ArrayList<CrusherRecipe>();

  public CrusherRecipeManager() {
  }

  public void loadRecipesFromConfig() {
    File coreFile = new File(Config.configDirectory, CORE_FILE_NAME);

    String defaultVals = null;
    try {
      defaultVals = readRecipes(coreFile, CORE_FILE_NAME, true);
    } catch (IOException e) {
      Log.error("Could load default SAG Mill from EnderIO jar: " + e.getMessage());
      e.printStackTrace();
      return;
    }

    if(!coreFile.exists()) {
      Log.error("Could load default SAG Mill recipes from " + coreFile + " as the file does not exist.");
      return;
    }

    RecipeConfig config;
    try {
      config = RecipeConfigParser.parse(defaultVals);
    } catch (Exception e) {
      Log.error("Error parsing " + CORE_FILE_NAME);
      return;
    }

    File userFile = new File(Config.configDirectory, CUSTOM_FILE_NAME);
    String userConfigStr = null;
    try {
      userConfigStr = readRecipes(userFile, CUSTOM_FILE_NAME, false);
      RecipeConfig userConfig = RecipeConfigParser.parse(userConfigStr);
      config.merge(userConfig);
    } catch (Exception e) {
      Log.error("Could load use definaed SAG Mill recipes.");
      e.printStackTrace();
    }

    processConfig(config);

  }

  public void addCustumRecipes(String xmlDef) {
    RecipeConfig config;
    try {
      config = RecipeConfigParser.parse(xmlDef);
    } catch (Exception e) {
      Log.error("Error parsing custom xml");
      return;
    }

    if(config == null) {
      Log.error("Could process custom XML");
      return;
    }
    processConfig(config);
  }

  public CrusherRecipe getRecipeForInput(ItemStack input) {
    if(input == null) {
      return null;
    }
    for (CrusherRecipe recipe : recipes) {
      if(recipe.isInput(input)) {
        return recipe;
      }
    }
    return null;
  }

  private void processConfig(RecipeConfig config) {
    if(config.isDumpItemRegistery()) {
      Util.dumpModObjects(new File(Config.configDirectory, "modObjectsRegistery.txt"));
    }
    if(config.isDumpOreDictionary()) {
      Util.dumpOreNames(new File(Config.configDirectory, "oreDictionaryRegistery.txt"));
    }

    List<CrusherRecipe> newRecipes = config.getRecipes();
    Log.info("Added " + newRecipes.size() + " SAG Mill recipes from config.");
    for (CrusherRecipe rec : newRecipes) {
      addRecipe(rec);
    }

  }

  private String readRecipes(File copyTo, String fileName, boolean replaceIfExists) throws IOException {
    if(!replaceIfExists && copyTo.exists()) {
      return readStream(new FileInputStream(copyTo));
    }

    InputStream in = getClass().getResourceAsStream("/assets/enderio/config/" + fileName);
    if(in == null) {
      Log.error("Could load default SAG Mill recipes.");
      throw new IOException("Could not resource /assets/enderio/config/" + fileName + " form classpath. ");
    }
    String output = readStream(in);
    BufferedWriter writer = null;
    try {
      writer = new BufferedWriter(new FileWriter(copyTo, false));
      writer.write(output.toString());
    } finally {
      IOUtils.closeQuietly(writer);
    }
    return output.toString();

  }

  private String readStream(InputStream in) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    StringBuilder output = new StringBuilder();
    try {
      String line = reader.readLine();
      while (line != null) {
        output.append(line);
        output.append("\n");
        line = reader.readLine();
      }
    } finally {
      IOUtils.closeQuietly(reader);
    }
    return output.toString();
  }

  public void addRecipe(ItemStack input, float energyCost, ItemStack output) {
    addRecipe(input, energyCost, new CrusherOutput(output, 1));
  }

  public void addRecipe(ItemStack input, float energyCost, CrusherOutput... output) {
    if(input == null || output == null) {
      return;
    }
    addRecipe(new CrusherRecipe(input, energyCost, output));
  }

  public void addRecipe(CrusherRecipe recipe) {
    if(recipe == null || !recipe.isValid()) {
      Log.debug("Could not add invalid recipe: " + recipe);
      return;
    }
    CrusherRecipe rec = getRecipeForInput(recipe.getInput());
    if(rec != null) {
      Log.warn("Not adding supplied recipe as a recipe already exists for the input: " + recipe.getInput());
      return;
    }
    recipes.add(recipe);
  }

  public List<CrusherRecipe> getRecipes() {
    return recipes;
  }

}
