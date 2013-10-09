package crazypants.enderio.machine.crusher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import crazypants.enderio.Log;

public class RecipeConfig {

  private boolean dumpItemRegistery = false;
  private boolean dumpOreDictionary = false;
  private boolean enabled = true;

  private Map<String, RecipeGroup> recipeGroups = new HashMap<String, RecipeConfig.RecipeGroup>();

  public RecipeConfig() {
  }

  public void merge(RecipeConfig userConfig) {
    for (RecipeGroup group : userConfig.getRecipeGroups().values()) {
      if(!group.enabled) {
        if(recipeGroups.remove(group.name) != null) {
          Log.info("Disabled core SAG Mill recipe group " + group.name + " due to user config.");
        }
      } else {
        RecipeGroup modifyGroup = recipeGroups.get(group.name);
        if(modifyGroup == null) {
          Log.info("Added user defined SAG Mill recipe group " + group.name);
          modifyGroup = new RecipeGroup(group.name);
          recipeGroups.put(group.name, modifyGroup);
        }
        for (Recipe recipe : group.recipes.values()) {
          if(recipe.isValid()) {
            if(modifyGroup.recipes.containsKey(recipe.name)) {
              Log.info("Replacing core SAG Mill recipe " + recipe.name + "  with user defined recipe.");
            } else {
              Log.info("Added user defined SAG Mill recipe " + recipe.name);
            }
            modifyGroup.addRecipe(recipe);
          } else {
            Log.info("Removed SAG Mill recipe " + recipe.name + " due to user config.");
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

  public List<CrusherRecipe> getRecipes() {
    List<CrusherRecipe> result = new ArrayList<CrusherRecipe>(32);
    for (RecipeGroup rg : recipeGroups.values()) {
      if(rg.isEnabled() && rg.isValid()) {
        result.addAll(rg.createRecipes());
      }
    }
    return result;
  }

  public List<CrusherRecipe> getRecipesForGroup(String group) {
    RecipeGroup grp = recipeGroups.get(group);
    if(grp == null) {
      return Collections.emptyList();
    }
    return grp.createRecipes();
  }

  public Map<String, RecipeGroup> getRecipeGroups() {
    return recipeGroups;
  }

  public static class RecipeGroup {

    private final String name;

    private Map<String, Recipe> recipes = new HashMap<String, Recipe>();

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

    public Recipe createRecipe(String name) {
      return new Recipe(name);
    }

    public void addRecipe(Recipe recipe) {
      recipes.put(recipe.name, recipe);
    }

    public String getName() {
      return name;
    }

    public List<CrusherRecipe> createRecipes() {
      List<CrusherRecipe> result = new ArrayList<CrusherRecipe>(recipes.size());
      for (Recipe recipe : recipes.values()) {
        if(recipe.isValid()) {
          result.addAll(recipe.createRecipes());
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

  public static class Recipe {

    private List<ItemStack> inputs = new ArrayList<ItemStack>();

    private List<CrusherOutput> outputs = new ArrayList<CrusherOutput>();

    private int energyRequired;

    private String name;

    private Recipe(String name) {
      this.name = name;
    }

    public void addInput(ItemStack stack) {
      inputs.add(stack);
    }

    public void addOutput(CrusherOutput output) {
      outputs.add(output);
    }

    public List<CrusherRecipe> createRecipes() {
      CrusherOutput[] output = outputs.toArray(new CrusherOutput[outputs.size()]);
      List<CrusherRecipe> result = new ArrayList<CrusherRecipe>();
      for (ItemStack input : inputs) {
        result.add(new CrusherRecipe(input, energyRequired, output));
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
