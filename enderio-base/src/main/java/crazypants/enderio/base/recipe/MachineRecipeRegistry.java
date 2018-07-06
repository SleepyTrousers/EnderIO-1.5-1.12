package crazypants.enderio.base.recipe;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

public class MachineRecipeRegistry {

  public static final @Nonnull String PAINTER = "painter";
  public static final @Nonnull String SAGMILL = "sagmill";
  public static final @Nonnull String ALLOYSMELTER = "alloysmelter";
  public static final @Nonnull String SLICENSPLICE = "slicensplice";
  public static final @Nonnull String SOULBINDER = "soulbinder";
  public static final @Nonnull String VAT = "vat";
  public static final @Nonnull String SPAWNER = "spawner";
  public static final @Nonnull String FARM = "farmingstation";
  public static final @Nonnull String TRANSCEIVER = "transceiver";
  public static final @Nonnull String ENCHANTER = "enchanter";
  public static final @Nonnull String BASIN = "basin";

  public static final @Nonnull MachineRecipeRegistry instance = new MachineRecipeRegistry();

  private final Map<String, Map<String, IMachineRecipe>> machineRecipes = new HashMap<String, Map<String, IMachineRecipe>>();

  public void registerRecipe(@Nonnull String machine, @Nonnull IMachineRecipe recipe) {
    getRecipesForMachine(machine).put(recipe.getUid(), recipe);
  }

  public @Nonnull Map<String, IMachineRecipe> getRecipesForMachine(@Nonnull String machineName) {
    Map<String, IMachineRecipe> res = machineRecipes.get(machineName);
    if (res == null) {
      res = new LinkedHashMap<String, IMachineRecipe>();
      machineRecipes.put(machineName, res);
    }
    return res;
  }

  public void enableRecipeSorting(@Nonnull String machineName) {
    Map<String, IMachineRecipe> res = machineRecipes.get(machineName);
    if (res == null) {
      res = new TreeMap<String, IMachineRecipe>();
      machineRecipes.put(machineName, res);
    } else if (!(res instanceof TreeMap)) {
      res = new TreeMap<String, IMachineRecipe>(res);
      machineRecipes.put(machineName, res);
    }
  }

  public IMachineRecipe getRecipeForUid(@Nonnull String uid) {
    for (Map<String, IMachineRecipe> recipes : machineRecipes.values()) {
      for (IMachineRecipe recipe : recipes.values()) {
        if (uid.equals(recipe.getUid())) {
          return recipe;
        }
      }
    }
    return null;
  }

  public IMachineRecipe getRecipeForInputs(@Nonnull String machineName, @Nonnull NNList<MachineRecipeInput> inputs) {
    for (IMachineRecipe recipe : getRecipesForMachine(machineName).values()) {
      if (recipe.isRecipe(inputs)) {
        return recipe;
      }
    }
    return null;
  }

  public @Nonnull NNList<IMachineRecipe> getRecipesForInput(@Nonnull String machineName, @Nonnull MachineRecipeInput input) {
    NNList<IMachineRecipe> result = new NNList<IMachineRecipe>();
    Map<String, IMachineRecipe> recipes = getRecipesForMachine(machineName);
    for (IMachineRecipe recipe : recipes.values()) {
      if (recipe.isValidInput(input)) {
        result.add(recipe);
      }
    }
    return result;
  }

}
