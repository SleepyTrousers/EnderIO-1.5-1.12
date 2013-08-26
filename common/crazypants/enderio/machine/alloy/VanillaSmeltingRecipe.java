package crazypants.enderio.machine.alloy;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.RecipeInput;

public class VanillaSmeltingRecipe implements IMachineRecipe {

  // We will use the same energy as per a standard furnace.
  // To do the conversion between fuel burning and MJ, use the Stirling Gen
  // which produces one MJ per tick of burn time
  private static float MJ_PER_ITEM = TileEntityFurnace.getItemBurnTime(new ItemStack(Item.coal)) / 8;

  @Override
  public String getUid() {
    return "VanillaSmeltingRecipe";
  }

  @Override
  public float getEnergyRequired(RecipeInput... inputs) {
    int numInputs = getNumInputs(inputs);
    return numInputs * MJ_PER_ITEM;
  }

  private int getNumInputs(RecipeInput[] inputs) {
    int numInputs = 0;
    for (RecipeInput input : inputs) {
      if (input != null && isValidInput(input.slotNumber, input.item)) {
        numInputs += input.item.stackSize;
      }
    }
    return Math.min(numInputs, 3);
  }

  @Override
  public boolean isRecipe(RecipeInput... inputs) {
    ItemStack output = null;
    for (RecipeInput ri : inputs) {
      if (ri != null && ri.item != null) {
        if (output == null) {
          output = FurnaceRecipes.smelting().getSmeltingResult(ri.item);
          if(output == null) {
            return false;
          }
        } else {
          ItemStack newOutput = FurnaceRecipes.smelting().getSmeltingResult(ri.item);
          if (newOutput == null || !newOutput.isItemEqual(output)) {
            return false;
          }
        }
      }
    }
    return output != null;
  }

  @Override
  public ItemStack[] getCompletedResult(RecipeInput... inputs) {
    ItemStack output = null;
    int inputCount = 0;
    for (RecipeInput ri : inputs) {
      if (ri != null && ri.item != null && output == null) {
        output = FurnaceRecipes.smelting().getSmeltingResult(ri.item);
      }
    }
    if (output == null) {
      return new ItemStack[0];
    }
    ItemStack result = output.copy();
    result.stackSize = result.stackSize * getNumInputs(inputs);
    return new ItemStack[] { result };
  }

  @Override
  public boolean isValidInput(int slotNumber, ItemStack item) {
    ItemStack itemstack = FurnaceRecipes.smelting().getSmeltingResult(item);
    return itemstack != null;
  }

  @Override
  public String getMachineName() {
    return ModObject.blockAlloySmelter.unlocalisedName;
  }

  @Override
  public RecipeInput[] getQuantitiesConsumed(RecipeInput[] inputs) {
    int consumed = 0;
    List<RecipeInput> result = new ArrayList<RecipeInput>();
    for (RecipeInput ri : inputs) {
      if (isValidInput(ri.slotNumber, ri.item) && consumed < 3 && ri != null && ri.item != null) {
        int available = ri.item.stackSize;
        int canUse = 3 - consumed;
        int use = Math.min(canUse, available);
        if (use > 0) {
          ItemStack st = ri.item.copy();
          st.stackSize = use;
          result.add(new RecipeInput(ri.slotNumber, st));
          consumed += use;
        }
      }
    }
    return result.toArray(new RecipeInput[result.size()]);
  }

}
