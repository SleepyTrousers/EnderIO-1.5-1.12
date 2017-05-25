package crazypants.enderio.recipe.alloysmelter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import crazypants.enderio.init.ModObject;
import crazypants.enderio.material.OreDictionaryPreferences;
import crazypants.enderio.recipe.IMachineRecipe;
import crazypants.enderio.recipe.IRecipe;
import crazypants.enderio.recipe.MachineRecipeInput;
import crazypants.enderio.recipe.Recipe;
import crazypants.enderio.recipe.RecipeBonusType;
import crazypants.enderio.recipe.RecipeInput;
import crazypants.enderio.recipe.RecipeOutput;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;

public class VanillaSmeltingRecipe implements IMachineRecipe {

  // We will use the same energy as per a standard furnace.
  // To do the conversion between fuel burning and RF, use the Stirling Gen
  // which produces ten RF per tick of burn time
  private static int RF_PER_ITEM = TileEntityFurnace.getItemBurnTime(new ItemStack(Items.COAL, 1, 0)) * 10 / 8;

  private boolean enabled = true;

  private List<RecipeInput> excludes = new ArrayList<RecipeInput>();

  @Override
  public String getUid() {
    return "VanillaSmeltingRecipe";
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public void addExclude(RecipeInput ri) {
    excludes.add(ri);
  }

  @Override
  public int getEnergyRequired(MachineRecipeInput... inputs) {
    int numInputs = getNumInputs(inputs);
    return numInputs * RF_PER_ITEM;
  }

  @Override
  public RecipeBonusType getBonusType(MachineRecipeInput... inputs) {
    return RecipeBonusType.NONE;
  }

  private int getNumInputs(MachineRecipeInput[] inputs) {
    int numInputs = 0;
    for (MachineRecipeInput input : inputs) {
      if (input != null && isValidInput(input)) {
        numInputs += input.item.stackSize;
      }
    }
    return Math.min(numInputs, 3);
  }

  @Override
  public boolean isRecipe(MachineRecipeInput... inputs) {
    if (!enabled) {
      return false;
    }
    ItemStack output = null;
    for (MachineRecipeInput ri : inputs) {
      if (ri != null && ri.item != null && !isExcluded(ri.item)) {
        if (output == null) {
          output = FurnaceRecipes.instance().getSmeltingResult(ri.item);
          if (output == null) {
            return false;
          }
        } else {
          ItemStack newOutput = FurnaceRecipes.instance().getSmeltingResult(ri.item);
          if (newOutput == null || !newOutput.isItemEqual(output)) {
            return false;
          }
        }
      }
    }
    return output != null;
  }

  private boolean isExcluded(ItemStack item) {
    for (RecipeInput ri : excludes) {
      if (ri != null && ri.isInput(item)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public ResultStack[] getCompletedResult(float chance, MachineRecipeInput... inputs) {
    ItemStack output = null;
    for (MachineRecipeInput ri : inputs) {
      if (ri != null && ri.item != null && output == null) {
        output = FurnaceRecipes.instance().getSmeltingResult(ri.item);
      }
    }
    if (output == null) {
      return new ResultStack[0];
    }
    int stackSize = output.stackSize;
    output = OreDictionaryPreferences.instance.getPreferred(output);
    ItemStack result = output.copy();
    result.stackSize = stackSize;
    result.stackSize = result.stackSize * getNumInputs(inputs);
    return new ResultStack[] { new ResultStack(result) };
  }

  @Override
  public float getExperienceForOutput(ItemStack output) {
    if (output == null) {
      return 0;
    }
    float result = FurnaceRecipes.instance().getSmeltingExperience(output);
    if (result > 1.0f) {
      // see net.minecraft.inventory.SlotFurnace.onCrafting(ItemStack)
      result = 1.0f;
    }
    return result * output.stackSize;
  }

  @Override
  public boolean isValidInput(MachineRecipeInput input) {
    if (!enabled) {
      return false;
    }
    if (input == null || input.item == null) {
      return false;
    }
    if (isExcluded(input.item)) {
      return false;
    }
    ItemStack itemstack = FurnaceRecipes.instance().getSmeltingResult(input.item);
    return itemstack != null;
  }

  @Override
  public String getMachineName() {
    return ModObject.blockAlloySmelter.getUnlocalisedName();
  }

  @Override
  public List<MachineRecipeInput> getQuantitiesConsumed(MachineRecipeInput[] inputs) {
    int consumed = 0;
    List<MachineRecipeInput> result = new ArrayList<MachineRecipeInput>();
    for (MachineRecipeInput ri : inputs) {
      if (ri != null && ri.item != null && isValidInput(new MachineRecipeInput(ri.slotNumber, ri.item)) && consumed < 3) {
        int available = ri.item.stackSize;
        int canUse = 3 - consumed;
        int use = Math.min(canUse, available);
        if (use > 0) {
          ItemStack st = ri.item.copy();
          st.stackSize = use;
          result.add(new MachineRecipeInput(ri.slotNumber, st));
          consumed += use;
        }
      }
    }
    return result;
  }

  public List<IRecipe> getAllRecipes() {
    if (!enabled) {
      return Collections.emptyList();
    }
    List<IRecipe> result = new ArrayList<IRecipe>();
    Map<ItemStack, ItemStack> metaList = FurnaceRecipes.instance().getSmeltingList();
    for (Entry<ItemStack, ItemStack> entry : metaList.entrySet()) {
      ItemStack output = entry.getValue();
      int stackSize = output.stackSize;
      output = OreDictionaryPreferences.instance.getPreferred(output).copy();
      output.stackSize = stackSize;
      result.add(new Recipe(new RecipeInput(entry.getKey()), RF_PER_ITEM, RecipeBonusType.NONE, new RecipeOutput(output)));
    }
    return result;
  }

}
