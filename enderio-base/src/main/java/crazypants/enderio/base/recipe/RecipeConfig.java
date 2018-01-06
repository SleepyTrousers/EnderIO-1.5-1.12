package crazypants.enderio.base.recipe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.io.IOUtils;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.Config;
import net.minecraft.item.ItemStack;

public class RecipeConfig {

  // ---------------------------------------------- Loading ------------

  public static RecipeConfig loadRecipeConfig(@Nonnull String coreFileName, @Nonnull String customFileName, @Nullable CustomTagHandler customHandler) {
    File coreFile = new File(Config.configDirectory, coreFileName);

    String defaultVals = null;
    try {
      defaultVals = readRecipes(coreFile, coreFileName, true);
    } catch (IOException e) {
      Log.error("Could not load default recipes file " + coreFile + " from EnderIO jar: " + e.getMessage());
      e.printStackTrace();
      return null;
    }

    if (!coreFile.exists()) {
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
      if (userConfigStr.trim().isEmpty()) {
        Log.error("Empty user config file: " + userFile.getAbsolutePath());
      } else {
        RecipeConfig userConfig = RecipeConfigParser.parse(userConfigStr, customHandler);
        if (userConfig != null) {
          config.merge(userConfig);
        } else {
          Log.error("Empty user config file: " + userFile.getAbsolutePath());
        }
      }
    } catch (Exception e) {
      Log.error("Could not load user defined recipes from file: " + customFileName);
      e.printStackTrace();
    }
    return config;
  }

  public static @Nonnull String readRecipes(@Nonnull File copyTo, @Nonnull String fileName, boolean replaceIfExists) throws IOException {
    if (!replaceIfExists && copyTo.exists()) {
      final FileInputStream in = new FileInputStream(copyTo);
      try {
        return readStream(in);
      } finally {
        IOUtils.closeQuietly(in);
      }
    }

    InputStream in = RecipeConfig.class.getResourceAsStream("/assets/enderio/config/" + fileName);
    try {
      if (in == null) {
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
      return output;
    } finally {
      IOUtils.closeQuietly(in);
    }
  }

  private static @Nonnull String readStream(@Nonnull InputStream in) throws IOException {
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

  // ---------------------------------------------- Class ------------

  private boolean dumpItemRegistery = false;
  private boolean dumpOreDictionary = false;
  private boolean enabled = true;

  private @Nonnull Map<String, RecipeGroup> recipeGroups = new HashMap<String, RecipeConfig.RecipeGroup>();

  public RecipeConfig() {
  }

  public void merge(@Nonnull RecipeConfig userConfig) {

    if (userConfig.dumpItemRegistery) {
      dumpItemRegistery = true;
    }
    if (userConfig.dumpOreDictionary) {
      dumpOreDictionary = true;
    }

    for (RecipeGroup group : userConfig.getRecipeGroups().values()) {
      if (!group.enabled) {
        if (recipeGroups.remove(group.name) != null) {
          Log.info("Disabled core recipe group " + group.name + " due to user config.");
        }
      } else {
        RecipeGroup modifyGroup = recipeGroups.get(group.name);
        if (modifyGroup == null) {
          Log.info("Added user defined recipe group " + group.name);
          modifyGroup = new RecipeGroup(group.name);
          recipeGroups.put(group.name, modifyGroup);
        }
        for (RecipeElement recipe : group.recipes.values()) {
          if (recipe.isValid()) {
            if (modifyGroup.recipes.containsKey(recipe.name)) {
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

  public @Nonnull RecipeGroup createRecipeGroup(@Nonnull String name) {
    return new RecipeGroup(name);
  }

  public void addRecipeGroup(@Nonnull RecipeGroup group) {
    if (group.isNameValid()) {
      recipeGroups.put(group.getName(), group);
    }
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public void setRecipeGroups(@Nonnull Map<String, RecipeGroup> recipeGroups) {
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

  public NNList<Recipe> getRecipes(boolean isRecipePerInput) {
    NNList<Recipe> result = new NNList<Recipe>();
    for (RecipeGroup rg : recipeGroups.values()) {
      if (rg.isEnabled() && rg.isValid()) {
        result.addAll(rg.createRecipes(isRecipePerInput));
      }
    }
    return result;
  }

  public NNList<Recipe> getRecipesForGroup(String group, boolean isRecipePerInput) {
    RecipeGroup grp = recipeGroups.get(group);
    if (grp == null) {
      return NNList.emptyList();
    }
    return grp.createRecipes(isRecipePerInput);
  }

  public @Nonnull Map<String, RecipeGroup> getRecipeGroups() {
    return recipeGroups;
  }

  public static class RecipeGroup {

    private final @Nonnull String name;

    private @Nonnull Map<String, RecipeElement> recipes = new LinkedHashMap<String, RecipeElement>();

    private boolean enabled = true;

    public RecipeGroup(@Nonnull String name) {
      this.name = name.trim();
    }

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }

    public RecipeElement createRecipe(@Nonnull String nameIn) {
      return new RecipeElement(nameIn);
    }

    public void addRecipe(@Nonnull RecipeElement recipe) {
      recipes.put(recipe.name, recipe);
    }

    public @Nonnull String getName() {
      return name;
    }

    public @Nonnull NNList<Recipe> createRecipes(boolean isRecipePerInput) {
      NNList<Recipe> result = new NNList<Recipe>();
      for (RecipeElement recipe : recipes.values()) {
        if (recipe.isValid()) {
          result.addAll(recipe.createRecipes(isRecipePerInput));
        }
      }
      return result;
    }

    public boolean isValid() {
      return isNameValid() && !recipes.isEmpty();
    }

    public boolean isNameValid() {
      return !name.isEmpty();
    }

    @Override
    public String toString() {
      return "RecipeGroup [name=" + name + ", recipes=" + recipes + ", enabled=" + enabled + "]";
    }

  }

  public static class RecipeElement {

    private @Nonnull NNList<RecipeInput> inputs = new NNList<RecipeInput>();

    private @Nonnull NNList<RecipeOutput> outputs = new NNList<RecipeOutput>();

    private int energyRequired;

    private @Nonnull RecipeBonusType bonusType = RecipeBonusType.MULTIPLY_OUTPUT;

    private @Nonnull String name;

    private boolean allowMissing = false;
    private boolean invalidated = false;

    private RecipeElement(@Nonnull String name) {
      this.name = name;
    }

    public void addInput(@Nonnull RecipeInput input) {
      inputs.add(input);
    }

    public void addInput(@Nonnull ItemStack stack, boolean useMetadata) {
      inputs.add(new RecipeInput(stack, useMetadata));
    }

    public void addOutput(@Nonnull RecipeOutput output) {
      outputs.add(output);
    }

    public @Nonnull NNList<Recipe> createRecipes(boolean isRecipePerInput) {

      RecipeOutput[] outputArr = outputs.toArray(new RecipeOutput[0]);
      IRecipeInput[] inputArr = inputs.toArray(new IRecipeInput[0]);
      NNList<Recipe> result = new NNList<Recipe>();
      if (isRecipePerInput) {
        for (RecipeInput input : inputs) {
          result.add(new Recipe(input, energyRequired, bonusType, outputArr));
        }
      } else {
        for (RecipeOutput output : outputs) {
          result.add(new Recipe(output, energyRequired, bonusType, inputArr));
        }
      }
      return result;
    }

    public boolean isValid() {
      return !invalidated && !inputs.isEmpty() && !outputs.isEmpty();
    }

    public float getEnergyRequired() {
      return energyRequired;
    }

    public void setEnergyRequired(int energyRequired) {
      this.energyRequired = energyRequired;
    }

    public RecipeBonusType getBonusType() {
      return bonusType;
    }

    public void setBonusType(@Nonnull RecipeBonusType bonusType) {
      this.bonusType = bonusType;
    }

    public void setAllowMissing(boolean allowMissing) {
      this.allowMissing = allowMissing;
    }

    public boolean allowMissing() {
      return allowMissing;
    }

    public void invalidate() {
      invalidated = true;
    }

    @Override
    public String toString() {
      return "Recipe [" + (invalidated ? "INVALID " : "") + "input=" + inputs + ", outputs=" + outputs + ", energyRequired=" + energyRequired + ", bonusType="
          + bonusType + "]";
    }

  }

}
