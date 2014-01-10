package crazypants.enderio.machine.recipe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;

import org.apache.commons.io.IOUtils;

import crazypants.enderio.Config;
import crazypants.enderio.Log;

public class RecipeConfig {

  //---------------------------------------------- Loading ------------

  public static RecipeConfig loadRecipeConfig(String coreFileName, String customFileName, CustomTagHandler customHandler) {
    File coreFile = new File(Config.configDirectory, coreFileName);

    String defaultVals = null;
    try {
      defaultVals = readRecipes(coreFile, coreFileName, true);
    } catch (IOException e) {
      Log.error("Could not load default recipes file " + coreFile + " from EnderIO jar: " + e.getMessage());
      e.printStackTrace();
      return null;
    }

    if(!coreFile.exists()) {
      Log.error("Could not load default recipes from " + coreFile + " as the file does not exist.");
      return null;
    }

    RecipeConfig config;
    try {
      config = RecipeConfigParser.parse(defaultVals, customHandler);
    } catch (Exception e) {
      Log.error("Error parsing " + coreFileName);
      return null;
    }

    File userFile = new File(Config.configDirectory, customFileName);
    String userConfigStr = null;
    try {
      userConfigStr = readRecipes(userFile, customFileName, false);
      if(userConfigStr == null || userConfigStr.trim().length() == 0) {
        Log.error("Empty user config file: " + userFile.getAbsolutePath());
      } else {
        RecipeConfig userConfig = RecipeConfigParser.parse(userConfigStr, customHandler);
        config.merge(userConfig);
      }
    } catch (Exception e) {
      Log.error("Could not load user defined recipes from file: " + customFileName);
      e.printStackTrace();
    }
    return config;
  }

  private static String readRecipes(File copyTo, String fileName, boolean replaceIfExists) throws IOException {
    if(!replaceIfExists && copyTo.exists()) {
      return readStream(new FileInputStream(copyTo));
    }

    InputStream in = RecipeConfig.class.getResourceAsStream("/assets/enderio/config/" + fileName);
    if(in == null) {
      Log.error("Could load default AlloySmelter recipes.");
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

  private static String readStream(InputStream in) throws IOException {
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

  //---------------------------------------------- Class ------------

  private boolean dumpItemRegistery = false;
  private boolean dumpOreDictionary = false;
  private boolean enabled = true;

  private Map<String, RecipeGroup> recipeGroups = new HashMap<String, RecipeConfig.RecipeGroup>();

  public RecipeConfig() {
  }

  public void merge(RecipeConfig userConfig) {

    if(userConfig.dumpItemRegistery) {
      dumpItemRegistery = true;
    }
    if(userConfig.dumpOreDictionary) {
      dumpOreDictionary = true;
    }

    for (RecipeGroup group : userConfig.getRecipeGroups().values()) {
      if(!group.enabled) {
        if(recipeGroups.remove(group.name) != null) {
          Log.info("Disabled core recipe group " + group.name + " due to user config.");
        }
      } else {
        RecipeGroup modifyGroup = recipeGroups.get(group.name);
        if(modifyGroup == null) {
          Log.info("Added user defined recipe group " + group.name);
          modifyGroup = new RecipeGroup(group.name);
          recipeGroups.put(group.name, modifyGroup);
        }
        for (RecipeElement recipe : group.recipes.values()) {
          if(recipe.isValid()) {
            if(modifyGroup.recipes.containsKey(recipe.name)) {
              Log.info("Replacing core recipe " + recipe.name + "  with user defined recipe.");
            } else {
              Log.info("Added user defined recipe " + recipe.name);
            }
            modifyGroup.addRecipe(recipe);
          } else {
            Log.info("Removed recipe " + recipe.name + " due to user config.");
            modifyGroup.recipes.remove(recipe.name);
          }
        }
      }
    }
  }

  public RecipeGroup createRecipeGroup(String name) {
    return new RecipeGroup(name);
  }

  public void addRecipeGroup(RecipeGroup group) {
    if(group.isNameValid()) {
      recipeGroups.put(group.getName(), group);
    }
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public void setRecipeGroups(Map<String, RecipeGroup> recipeGroups) {
    this.recipeGroups = recipeGroups;
  }

  public boolean isDumpItemRegistery() {
    return dumpItemRegistery;
  }

  public void setDumpItemRegistery(boolean dumpItemRegistery) {
    this.dumpItemRegistery = dumpItemRegistery;
  }

  public boolean isDumpOreDictionary() {
    return dumpOreDictionary;
  }

  public void setDumpOreDictionary(boolean dumpOreDictionary) {
    this.dumpOreDictionary = dumpOreDictionary;
  }

  public List<Recipe> getRecipes(boolean isRecipePerInput) {
    List<Recipe> result = new ArrayList<Recipe>(32);
    for (RecipeGroup rg : recipeGroups.values()) {
      if(rg.isEnabled() && rg.isValid()) {
        result.addAll(rg.createRecipes(isRecipePerInput));
      }
    }
    return result;
  }

  public List<Recipe> getRecipesForGroup(String group, boolean isRecipePerInput) {
    RecipeGroup grp = recipeGroups.get(group);
    if(grp == null) {
      return Collections.emptyList();
    }
    return grp.createRecipes(isRecipePerInput);
  }

  public Map<String, RecipeGroup> getRecipeGroups() {
    return recipeGroups;
  }

  public static class RecipeGroup {

    private final String name;

    private Map<String, RecipeElement> recipes = new HashMap<String, RecipeElement>();

    private boolean enabled = true;

    public RecipeGroup(String name) {
      if(name != null) {
        name = name.trim();
      }
      if(name.length() <= 0) {
        name = null;
      }
      this.name = name;
    }

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }

    public RecipeElement createRecipe(String name) {
      return new RecipeElement(name);
    }

    public void addRecipe(RecipeElement recipe) {
      recipes.put(recipe.name, recipe);
    }

    public String getName() {
      return name;
    }

    public List<Recipe> createRecipes(boolean isRecipePerInput) {
      List<Recipe> result = new ArrayList<Recipe>(recipes.size());
      for (RecipeElement recipe : recipes.values()) {
        if(recipe.isValid()) {
          result.addAll(recipe.createRecipes(isRecipePerInput));
        }
      }
      return result;
    }

    public boolean isValid() {
      return isNameValid() && !recipes.isEmpty();
    }

    public boolean isNameValid() {
      return name != null;
    }

    @Override
    public String toString() {
      return "RecipeGroup [name=" + name + ", recipes=" + recipes + ", enabled=" + enabled + "]";
    }

  }

  public static class RecipeElement {

    private List<RecipeInput> inputs = new ArrayList<RecipeInput>();

    private List<RecipeOutput> outputs = new ArrayList<RecipeOutput>();

    private int energyRequired;

    private String name;

    private RecipeElement(String name) {
      this.name = name;
    }

    public void addInput(RecipeInput input) {
      inputs.add(input);
    }

    public void addInput(ItemStack stack, boolean useMetadata) {
      inputs.add(new RecipeInput(stack, useMetadata));
    }

    public void addOutput(RecipeOutput output) {
      outputs.add(output);
    }

    public List<Recipe> createRecipes(boolean isRecipePerInput) {

      RecipeOutput[] outputArr = outputs.toArray(new RecipeOutput[outputs.size()]);
      RecipeInput[] inputArr = inputs.toArray(new RecipeInput[inputs.size()]);
      List<Recipe> result = new ArrayList<Recipe>();
      if(isRecipePerInput) {
        for (RecipeInput input : inputs) {
          result.add(new Recipe(input, energyRequired, outputArr));
        }
      } else {
        for (RecipeOutput output : outputs) {
          result.add(new Recipe(output, energyRequired, inputArr));
        }
      }
      return result;
    }

    public boolean isValid() {
      return !inputs.isEmpty() && !outputs.isEmpty();
    }

    public float getEnergyRequired() {
      return energyRequired;
    }

    public void setEnergyRequired(int energyRequired) {
      this.energyRequired = energyRequired;
    }

    @Override
    public String toString() {
      return "Recipe [input=" + inputs + ", outputs=" + outputs + ", energyRequired=" + energyRequired + "]";
    }

  }

}
