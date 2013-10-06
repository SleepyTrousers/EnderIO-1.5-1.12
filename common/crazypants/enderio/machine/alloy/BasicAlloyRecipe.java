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
import crazypants.enderio.crafting.impl.RecipeInput;
import crazypants.enderio.crafting.impl.RecipeOutput;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.MachineRecipeInput;

public class BasicAlloyRecipe implements IMachineRecipe {

  public static final int DEFAULT_ENERGY_USE = 1600;

  private final String uid;

  private float energyRequired = DEFAULT_ENERGY_USE;

  private ItemStack[] inputs;

  private Set<InputKey> inputKeys;

  private ItemStack output;

  private final List<IEnderIoRecipe> recipe;

  public BasicAlloyRecipe(ItemStack output, String uid, ItemStack... recipeInputs) {
    this.output = output.copy();
    this.uid = uid;
    inputs = new ItemStack[recipeInputs.length];
    inputKeys = new HashSet<InputKey>();

    List<IRecipeComponent> reipceComps = new ArrayList<IRecipeComponent>(recipeInputs.length);
    for (int i = 0; i < inputs.length; i++) {
      if(recipeInputs[i] != null) {
        inputs[i] = recipeInputs[i].copy();
        inputKeys.add(new InputKey(inputs[i].itemID, inputs[i].getItemDamage()));
        reipceComps.add(new RecipeInput(inputs[i], false));
      } else {
        inputs[i] = null;
      }
    }

    reipceComps.add(new RecipeOutput(output));
    IEnderIoRecipe rec = new EnderIoRecipe(IEnderIoRecipe.ALLOY_SMELTER_ID, energyRequired, reipceComps);
    recipe = Collections.singletonList(rec);

  }

  public ItemStack[] getInputs() {
    return inputs;
  }

  public ItemStack getOutput() {
    return output;
  }

  @Override
  public String getUid() {
    return uid;
  }

  @Override
  public float getEnergyRequired(MachineRecipeInput... inputs) {
    return energyRequired;
  }

  @Override
  public boolean isRecipe(MachineRecipeInput... checking) {
    checking = getNonNullInputs(checking);
    if(inputs.length != checking.length) {
      return false;
    }

    Set<InputKey> keys = new HashSet<BasicAlloyRecipe.InputKey>(inputKeys);
    for (MachineRecipeInput input : checking) {
      ItemStack ing = getIngrediantForInput(input.item);
      if(ing == null || ing.stackSize > input.item.stackSize) {
        return false;
      }
      keys.remove(new InputKey(ing.itemID, ing.getItemDamage()));
    }
    return keys.isEmpty();
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

  @Override
  public ItemStack[] getCompletedResult(float chance, MachineRecipeInput... inputs) {
    return new ItemStack[] { output.copy() };
  }

  @Override
  public boolean isValidInput(MachineRecipeInput input) {
    if(input == null) {
      return false;
    }
    return getIngrediantForInput(input.item) != null;
  }

  @Override
  public String getMachineName() {
    return ModObject.blockAlloySmelter.unlocalisedName;
  }

  public int getQuantityConsumed(MachineRecipeInput input) {
    ItemStack ing = getIngrediantForInput(input.item);
    return ing == null ? 0 : ing.stackSize;
  }

  private ItemStack getIngrediantForInput(ItemStack input) {
    if(input == null) {
      return null;
    }
    for (ItemStack st : inputs) {
      if(st != null && st.itemID == input.itemID) {
        return st;
      }
    }
    return null;
  }

  @Override
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

  @Override
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
  public float getExperianceForOutput(ItemStack output) {
    return 0;
  }

}
