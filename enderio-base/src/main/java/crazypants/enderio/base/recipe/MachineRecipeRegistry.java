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
  public static final @Nonnull String TANK_FILLING = "tankfill";
  public static final @Nonnull String TANK_EMPTYING = "tankempty";

  public static final @Nonnull MachineRecipeRegistry instance = new MachineRecipeRegistry();

  private final Map<String, Map<String, IMachineRecipe>> machineRecipes = new HashMap<>();

  public void registerRecipe(@Nonnull IMachineRecipe recipe) {
    getRecipesForMachine(recipe.getMachineName()).put(recipe.getUid(), recipe);
  }

  /**
   * @deprecated use {@link #registerRecipe(IMachineRecipe)}
   */
  @Deprecated
  public void registerRecipe(@Nonnull String machine, @Nonnull IMachineRecipe recipe) {
    getRecipesForMachine(machine).put(recipe.getUid(), recipe);
  }

  /**
   * Removes the recipe only if it is currently mapped by its UID.
   *
   * @param recipe
   * @return {@code true} if the value was removed
   */
  public boolean removeRecipe(@Nonnull IMachineRecipe recipe) {
    // TODO 1.12: If we start using this: add a JEI hook to remove the recipe there, too
    // TODO 1.16: Check JEI API, do we need to actively add/remove recipes or do we link in a registry?
    return getRecipesForMachine(recipe.getMachineName()).remove(recipe.getUid(), recipe);
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

  public IMachineRecipe getRecipeForInputs(@Nonnull RecipeLevel machineLevel, @Nonnull String machineName, @Nonnull NNList<MachineRecipeInput> inputs) {
    for (IMachineRecipe recipe : getRecipesForMachine(machineName).values()) {
      if (recipe.isRecipe(machineLevel, inputs)) {
        return recipe;
      }
    }
    return null;
  }

  public IMachineRecipe getRecipeForInput(@Nonnull RecipeLevel machineLevel, @Nonnull String machineName, @Nonnull MachineRecipeInput input) {
    for (IMachineRecipe recipe : getRecipesForMachine(machineName).values()) {
      if (recipe.isValidInput(machineLevel, input)) {
        return recipe;
      }
    }
    return null;
  }

}
