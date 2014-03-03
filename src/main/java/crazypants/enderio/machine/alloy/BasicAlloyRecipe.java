package crazypants.enderio.machine.alloy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import crazypants.enderio.ModObject;
import crazypants.enderio.crafting.IEnderIoRecipe;
import crazypants.enderio.crafting.IRecipeComponent;
import crazypants.enderio.crafting.impl.EnderIoRecipe;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.recipe.Recipe;
import crazypants.enderio.machine.recipe.RecipeInput;
import crazypants.enderio.machine.recipe.RecipeOutput;

public class BasicAlloyRecipe implements IAlloyRecipe {

  public static final int DEFAULT_ENERGY_USE = 1200;

  private float energyRequired = DEFAULT_ENERGY_USE;
  private ItemStack output;

  private final List<IEnderIoRecipe> recipeEIO;

  private float expPerItem;

  private final Recipe recipe;

  public BasicAlloyRecipe(Recipe recipe) {
    this.recipe = recipe;
    this.output = recipe.getOutputs()[0].getOutput().copy();
    expPerItem = recipe.getOutputs()[0].getExperiance();
    energyRequired = recipe.getEnergyRequired();

    List<IRecipeComponent> reipceComps = new ArrayList<IRecipeComponent>(recipe.getInputs().length);
    for (RecipeInput input : recipe.getInputs()) {
      crazypants.enderio.crafting.impl.RecipeInput ri = new crazypants.enderio.crafting.impl.RecipeInput(input.getInput(), -1, input.getEquivelentInputs());
      reipceComps.add(ri);
    }
    reipceComps.add(new crazypants.enderio.crafting.impl.RecipeOutput(output));
    IEnderIoRecipe rec = new EnderIoRecipe(IEnderIoRecipe.ALLOY_SMELTER_ID, energyRequired, reipceComps);
    this.recipeEIO = Collections.singletonList(rec);
  }

  @Override
  public boolean isValidRecipeComponents(ItemStack... items) {

    List<RecipeInput> inputs = new ArrayList<RecipeInput>(Arrays.asList(recipe.getInputs()));
    for (ItemStack is : items) {
      if(is != null) {
        RecipeInput remove = null;
        for (RecipeInput ri : inputs) {
          if(ri.isInput(is)) {
            remove = ri;
            break;
          }
        }
        if(remove != null) {
          inputs.remove(remove);
        } else {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public ItemStack getOutput() {
    return output;
  }

  private ItemStack[] getNonNullInputStacks(ItemStack[] checking) {
    if(checking == null) {
      return new ItemStack[0];
    }
    List<ItemStack> result = new ArrayList<ItemStack>(checking.length);
    for (ItemStack st : checking) {
      if(st != null) {
        result.add(st);
      }
    }
    return result.toArray(new ItemStack[result.size()]);
  }

  private MachineRecipeInput[] getNonNullInputs(MachineRecipeInput[] checking) {
    int numNonNulls = 0;
    for (int i = 0; i < checking.length; i++) {
      if(checking[i] != null && checking[i].item != null) {
        numNonNulls++;
      }
    }
    MachineRecipeInput[] result = new MachineRecipeInput[numNonNulls];
    int index = 0;
    for (int i = 0; i < checking.length; i++) {
      if(checking[i] != null && checking[i].item != null) {
        result[index] = checking[i];
        index++;
      }
    }
    return result;
  }

  //@Override
  public ItemStack[] getCompletedResult(float chance, MachineRecipeInput... inputs) {
    return new ItemStack[] { output.copy() };
  }

  //@Override
  @Override
  public boolean isValidInput(MachineRecipeInput input) {
    if(input == null) {
      return false;
    }
    return getRecipeComponentFromInput(input.item) != null;
  }

  //@Override
  public String getMachineName() {
    return ModObject.blockAlloySmelter.unlocalisedName;
  }

  public int getQuantityConsumed(MachineRecipeInput input) {
    ItemStack ing = getRecipeComponentFromInput(input.item);
    return ing == null ? 0 : ing.stackSize;
  }

  private ItemStack getRecipeComponentFromInput(ItemStack input) {
    if(input == null) {
      return null;
    }
    for (RecipeInput ri : recipe.getInputs()) {
      if(ri.isInput(input)) {
        return ri.getInput();
      }
    }
    return null;
  }

  //@Override
  public MachineRecipeInput[] getQuantitiesConsumed(MachineRecipeInput[] inputs) {
    List<MachineRecipeInput> result = new ArrayList<MachineRecipeInput>();
    for (MachineRecipeInput input : inputs) {
      int numConsumed = getQuantityConsumed(input);
      if(numConsumed > 0) {
        ItemStack consumed = input.item.copy();
        consumed.stackSize = numConsumed;
        result.add(new MachineRecipeInput(input.slotNumber, consumed));
      }
    }
    if(result.isEmpty()) {
      return null;
    }
    return result.toArray(new MachineRecipeInput[result.size()]);
  }

  public List<IEnderIoRecipe> getAllRecipes() {
    return recipeEIO;
  }

  static class InputKey {

    int itemID;
    int damage;

    InputKey(int itemID, int damage) {
      this.itemID = itemID;
      this.damage = damage;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + damage;
      result = prime * result + itemID;
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if(this == obj) {
        return true;
      }
      if(obj == null) {
        return false;
      }
      if(getClass() != obj.getClass()) {
        return false;
      }
      InputKey other = (InputKey) obj;
      if(damage != other.damage) {
        return false;
      }
      if(itemID != other.itemID) {
        return false;
      }
      return true;
    }

  }

  @Override
  public boolean isValid() {
    return recipe != null && recipe.isValid();
  }

  @Override
  public float getEnergyRequired() {
    return energyRequired;
  }

  @Override
  public RecipeOutput[] getOutputs() {
    return recipe.getOutputs();
  }

  @Override
  public ItemStack[] getInputStacks() {
    return recipe.getInputStacks();
  }

  @Override
  public boolean isInputForRecipe(ItemStack[] test) {
    if(test == null) {
      return false;
    }
    return recipe.isInputForRecipe(test);
  }

  @Override
  public RecipeInput[] getInputs() {
    return recipe.getInputs();
  }
}
