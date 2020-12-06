package crazypants.enderio.base.recipe;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

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

  private final Map<String, RecipeGroupHolder<?>> machineRecipes = new HashMap<>();

  public void registerRecipe(@Nonnull IMachineRecipe recipe) {
    getRecipeHolderssForMachine(recipe.getMachineName()).addRecipe(recipe);
  }

  /**
   * @deprecated use {@link #registerRecipe(IMachineRecipe)}
   */
  @Deprecated
  public void registerRecipe(@Nonnull String machine, @Nonnull IMachineRecipe recipe) {
    getRecipeHolderssForMachine(machine).addRecipe(recipe);
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
    return getRecipeHolderssForMachine(recipe.getMachineName()).removeRecipe(recipe);
  }

  public @Nonnull Map<String, ? extends IMachineRecipe> getRecipesForMachine(@Nonnull String machineName) {
    return getRecipeHolderssForMachine(machineName).getRecipesForMachine();
  }

  public @Nonnull RecipeGroupHolder<?> getRecipeHolderssForMachine(@Nonnull String machineName) {
    return NullHelper.notnullJ(machineRecipes.computeIfAbsent(machineName, unused -> new SimpleRecipeGroupHolder(false)), "map.computeIfAbsent");
  }

  public void enableRecipeSorting(@Nonnull String machineName) {
    RecipeGroupHolder<?> res = machineRecipes.get(machineName);
    if (res instanceof SimpleRecipeGroupHolder) {
      machineRecipes.put(machineName, ((SimpleRecipeGroupHolder) res).asSorted());
      return;
    }
    throw new RuntimeException("Cannot force recipe sorting for " + res + " of machine " + machineName);
  }

  public IMachineRecipe getRecipeForUid(@Nonnull String uid) {
    for (RecipeGroupHolder<?> holder : machineRecipes.values()) {
      IMachineRecipe recipe = holder.getRecipeForUid(uid);
      if (recipe != null) {
        return recipe;
      }
    }
    return null;
  }

  public IMachineRecipe getRecipeForInputs(@Nonnull RecipeLevel machineLevel, @Nonnull String machineName, @Nonnull NNList<MachineRecipeInput> inputs) {
    return getRecipeHolderssForMachine(machineName).getRecipeForInputs(machineLevel, machineName, inputs);
  }

  public IMachineRecipe getRecipeForInput(@Nonnull RecipeLevel machineLevel, @Nonnull String machineName, @Nonnull MachineRecipeInput input) {
    return getRecipeHolderssForMachine(machineName).getRecipeForInput(machineLevel, machineName, input);
  }

  interface RecipeGroupHolder<REC extends IMachineRecipe> {

    // not REC here, our caller doesn't know what kind of recipes we accept, so the compiler would be very unhappy
    void addRecipe(@Nonnull IMachineRecipe recipe);

    boolean removeRecipe(@Nonnull IMachineRecipe recipe); // same

    @Nonnull
    Map<String, REC> getRecipesForMachine();

    REC getRecipeForUid(@Nonnull String uid);

    REC getRecipeForInputs(@Nonnull RecipeLevel machineLevel, @Nonnull String machineName, @Nonnull NNList<MachineRecipeInput> inputs);

    REC getRecipeForInput(@Nonnull RecipeLevel machineLevel, @Nonnull String machineName, @Nonnull MachineRecipeInput input);

  }

  private static class SimpleRecipeGroupHolder implements RecipeGroupHolder<IMachineRecipe> {

    private final @Nonnull Map<String, IMachineRecipe> recipes;

    public SimpleRecipeGroupHolder(boolean sorted) {
      if (sorted) {
        recipes = new TreeMap<String, IMachineRecipe>();
      } else {
        recipes = new LinkedHashMap<String, IMachineRecipe>();
      }
    }

    public SimpleRecipeGroupHolder asSorted() {
      SimpleRecipeGroupHolder result = new SimpleRecipeGroupHolder(true);
      result.recipes.putAll(recipes);
      return result;
    }

    @Override
    public void addRecipe(@Nonnull IMachineRecipe recipe) {
      recipes.put(recipe.getUid(), recipe);
    }

    @Override
    public boolean removeRecipe(@Nonnull IMachineRecipe recipe) {
      return recipes.remove(recipe.getUid(), recipe);
    }

    @Override
    public IMachineRecipe getRecipeForUid(@Nonnull String uid) {
      return recipes.get(uid);
    }

    @Override
    public IMachineRecipe getRecipeForInputs(@Nonnull RecipeLevel machineLevel, @Nonnull String machineName, @Nonnull NNList<MachineRecipeInput> inputs) {
      for (IMachineRecipe recipe : recipes.values()) {
        if (recipe.isRecipe(machineLevel, inputs)) {
          return recipe;
        }
      }
      return null;
    }

    @Override
    public IMachineRecipe getRecipeForInput(@Nonnull RecipeLevel machineLevel, @Nonnull String machineName, @Nonnull MachineRecipeInput input) {
      for (IMachineRecipe recipe : recipes.values()) {
        if (recipe.isValidInput(machineLevel, input)) {
          return recipe;
        }
      }
      return null;
    }

    @Override
    @Nonnull
    public Map<String, IMachineRecipe> getRecipesForMachine() {
      return NullHelper.notnullJ(Collections.unmodifiableMap(recipes), "Collections.unmodifiableMap");
    }

  }

}
