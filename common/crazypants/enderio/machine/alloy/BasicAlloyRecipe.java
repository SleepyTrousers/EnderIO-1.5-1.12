package crazypants.enderio.machine.alloy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

  private ItemStack[] inputs;

  private Set<InputKey> inputKeys;

  private ItemStack output;

  private final List<IEnderIoRecipe> recipe;

  private float expPerItem;

  private RecipeOutput[] outputs;

  public BasicAlloyRecipe(Recipe recipe) {
    this.output = recipe.getOutputs()[0].getOutput().copy();
    expPerItem = recipe.getOutputs()[0].getExperiance();
    outputs = new crazypants.enderio.machine.recipe.RecipeOutput[] { new crazypants.enderio.machine.recipe.RecipeOutput(output, 1, expPerItem) };

    ItemStack[] recipeInputs = recipe.getInputStacks();

    inputs = new ItemStack[recipeInputs.length];
    inputKeys = new HashSet<InputKey>();

    List<IRecipeComponent> reipceComps = new ArrayList<IRecipeComponent>(recipeInputs.length);
    for (int i = 0; i < inputs.length; i++) {
      if(recipeInputs[i] != null) {
        inputs[i] = recipeInputs[i].copy();
        inputKeys.add(new InputKey(inputs[i].itemID, inputs[i].getItemDamage()));
        reipceComps.add(new crazypants.enderio.crafting.impl.RecipeInput(inputs[i], false));
      } else {
        inputs[i] = null;
      }
    }

    energyRequired = recipe.getEnergyRequired();

    reipceComps.add(new crazypants.enderio.crafting.impl.RecipeOutput(output));
    IEnderIoRecipe rec = new EnderIoRecipe(IEnderIoRecipe.ALLOY_SMELTER_ID, energyRequired, reipceComps);
    this.recipe = Collections.singletonList(rec);

  }

  @Override
  public boolean isValidRecipeComponents(ItemStack... items) {
    Set<InputKey> remainingInputs = new HashSet<InputKey>(inputKeys);
    for (ItemStack item : items) {
      if(item != null) {
        InputKey key = new InputKey(item.itemID, item.getItemDamage());
        if(!remainingInputs.contains(key)) {
          return false;
        }
        remainingInputs.remove(key);
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
    for (ItemStack st : inputs) {
      if(st != null && st.isItemEqual(input)) {
        return st;
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

  //@Override
  public List<IEnderIoRecipe> getAllRecipes() {
    return recipe;
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
    return inputs != null && inputs.length > 0 && output != null;
  }

  @Override
  public float getEnergyRequired() {
    return energyRequired;
  }

  @Override
  public crazypants.enderio.machine.recipe.RecipeOutput[] getOutputs() {
    return outputs;
  }

  @Override
  public ItemStack[] getInputStacks() {
    return inputs;
  }

  @Override
  public boolean isInputForRecipe(ItemStack[] test) {
    if(test == null) {
      return false;
    }
    test = getNonNullInputStacks(test);
    if(inputs.length != test.length) {
      return false;
    }

    Set<InputKey> keys = new HashSet<BasicAlloyRecipe.InputKey>(inputKeys);
    for (ItemStack input : test) {
      ItemStack ing = getRecipeComponentFromInput(input);
      if(ing == null || ing.stackSize > input.stackSize) {
        return false;
      }
      keys.remove(new InputKey(ing.itemID, ing.getItemDamage()));
    }
    return keys.isEmpty();
  }

  @Override
  public RecipeInput[] getInputs() {
    ItemStack[] inStacks = getNonNullInputStacks(inputs);
    RecipeInput[] result = new RecipeInput[inStacks.length];
    for (int i = 0; i < result.length; i++) {
      result[i] = new RecipeInput(inStacks[i], true);
    }
    return result;
  }
}
