package crazypants.enderio.recipe.vat;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

import com.enderio.core.common.util.FluidUtil;

import crazypants.enderio.Log;
import crazypants.enderio.init.ModObject;
import crazypants.enderio.recipe.IRecipe;
import crazypants.enderio.recipe.MachineRecipeInput;
import crazypants.enderio.recipe.MachineRecipeRegistry;
import crazypants.enderio.recipe.Recipe;
import crazypants.enderio.recipe.RecipeConfig;
import crazypants.enderio.recipe.RecipeConfigParser;
import crazypants.enderio.recipe.RecipeInput;
import crazypants.enderio.recipe.RecipeOutput;

public class VatRecipeManager {

  private static final String CORE_FILE_NAME = "VatRecipes_Core.xml";
  private static final String CUSTOM_FILE_NAME = "VatRecipes_User.xml";

  static final VatRecipeManager instance = new VatRecipeManager();

  public static VatRecipeManager getInstance() {
    return instance;
  }

  private final List<IRecipe> recipes = new ArrayList<IRecipe>();

  public VatRecipeManager() {
  }

  public void loadRecipesFromConfig() {
    RecipeConfig config = RecipeConfig.loadRecipeConfig(CORE_FILE_NAME, CUSTOM_FILE_NAME, null);
    if (config != null) {
      processConfig(config);
    } else {
      Log.error("Could not load recipes for Vat.");
    }

    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockVat.getUnlocalisedName(), new VatMachineRecipe());
  }

  public void addCustomRecipes(String xmlDef) {
    RecipeConfig config;
    try {
      config = RecipeConfigParser.parse(xmlDef, null);
    } catch (Exception e) {
      Log.error("Error parsing custom xml");
      return;
    }

    if (config == null) {
      Log.error("Could process custom XML");
      return;
    }
    processConfig(config);
  }

  public IRecipe getRecipeForInput(MachineRecipeInput[] inputs) {
    if (inputs == null || inputs.length == 0) {
      return null;
    }
    for (IRecipe recipe : recipes) {
      if (recipe.isInputForRecipe(inputs)) {
        return recipe;
      }
    }
    return null;
  }

  private void processConfig(RecipeConfig config) {
    List<Recipe> newRecipes = config.getRecipes(false);
    Log.info("Found " + newRecipes.size() + " valid Vat recipes in config.");
    for (Recipe rec : newRecipes) {
      addRecipe(rec);
    }
    Log.info("Finished processing Vat recipes. " + recipes.size() + " recipes avaliable.");
  }

  public void addRecipe(IRecipe recipe) {
    if (recipe == null || !recipe.isValid()) {
      Log.debug("Could not add invalid Vat recipe: " + recipe);
      return;
    }
    recipes.add(new VatRecipe(recipe));
  }

  public List<IRecipe> getRecipes() {
    return recipes;
  }

  public boolean isValidInput(MachineRecipeInput input) {
    for (IRecipe recipe : recipes) {
      if (input.item != null && recipe.isValidInput(input.slotNumber, input.item)) {
        return true;
      } else if (input.fluid != null && recipe.isValidInput(input.fluid)) {
        return true;
      }
    }
    return false;
  }

  public boolean isValidInput(MachineRecipeInput[] inputs) {
    for (IRecipe recipe : recipes) {
      boolean allValid = true;
      for (MachineRecipeInput input : inputs) {
        if (input.item != null) {
          allValid = recipe.isValidInput(input.slotNumber, input.item);
        } else if (input.fluid != null) {
          allValid = recipe.isValidInput(input.fluid);
        }
        if (!allValid) {
          break;
        }
      }
      if (allValid) {
        return true;
      }
    }
    return false;
  }

  public float getMultiplierForInput(Fluid inputFluid, ItemStack input, Fluid output) {
    if (input != null || output != null) {
      for (IRecipe recipe : recipes) {
        RecipeOutput out = recipe.getOutputs()[0];
        RecipeInput in = recipe.getInputs()[recipe.getInputs().length - 1];

        if ((inputFluid == null || FluidUtil.areFluidsTheSame(in.getFluidInput().getFluid(), inputFluid)
            && (output == null || FluidUtil.areFluidsTheSame(out.getFluidOutput().getFluid(), output)))) {
          for (RecipeInput ri : recipe.getInputs()) {
            if (ri.isInput(input)) {
              return ri.getMulitplier();
            }
          }
        }
      }
    }
    // no fluid or not an input for this fluid: best guess
    // (after all, the item IS in the input slot)
    float found = -1f;
    for (IRecipe recipe : recipes) {
      for (RecipeInput ri : recipe.getInputs()) {
        if (ri.isInput(input)) {
          if (found < 0f || found > ri.getMulitplier()) {
            found = ri.getMulitplier();
          }
        }
      }
    }
    return found > 0 ? found : 0;
  }

}
