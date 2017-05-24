package crazypants.enderio.recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MachineRecipeRegistry {

  public static final MachineRecipeRegistry instance = new MachineRecipeRegistry();

  private final Map<String, Map<String, IMachineRecipe>> machineRecipes = new HashMap<String, Map<String, IMachineRecipe>>();

  public void registerRecipe(String machine, IMachineRecipe recipe) {
    getRecipesForMachine(machine).put(recipe.getUid(), recipe);    
  }

  public Map<String, IMachineRecipe> getRecipesForMachine(String machineName) {
    Map<String, IMachineRecipe> res = machineRecipes.get(machineName);
    if(res == null) {
      res = new LinkedHashMap<String, IMachineRecipe>();
      machineRecipes.put(machineName, res);
    }
    return res;
  }

  public void enableRecipeSorting(String machineName) {
    Map<String, IMachineRecipe> res = machineRecipes.get(machineName);
    if (res == null) {
      res = new TreeMap<String, IMachineRecipe>();
      machineRecipes.put(machineName, res);
    } else if (!(res instanceof TreeMap)) {
      res = new TreeMap<String, IMachineRecipe>(res);
      machineRecipes.put(machineName, res);
    }
  }

  public IMachineRecipe getRecipeForUid(String uid) {
    if(uid == null) {
      return null;
    }
    for (Map<String, IMachineRecipe> recipes : machineRecipes.values()) {
      for (IMachineRecipe recipe : recipes.values()) {
        if(uid.equals(recipe.getUid())) {
          return recipe;
        }
      }
    }
    return null;
  }

  public IMachineRecipe getRecipeForInputs(String machineName, MachineRecipeInput... inputs) {
    Map<String, IMachineRecipe> recipes = getRecipesForMachine(machineName);
    if(recipes == null) {
      return null;
    }
    for (IMachineRecipe recipe : recipes.values()) {
      if(recipe.isRecipe(inputs)) {
        return recipe;
      }
    }
    return null;
  }

  public List<IMachineRecipe> getRecipesForInput(String machineName, MachineRecipeInput input) {
    if(input == null) {
      return Collections.emptyList();
    }
    List<IMachineRecipe> result = new ArrayList<IMachineRecipe>();
    Map<String, IMachineRecipe> recipes = getRecipesForMachine(machineName);
    for (IMachineRecipe recipe : recipes.values()) {
      if(recipe.isValidInput(input)) {
        result.add(recipe);
      }
    }
    return result;
  }

}
