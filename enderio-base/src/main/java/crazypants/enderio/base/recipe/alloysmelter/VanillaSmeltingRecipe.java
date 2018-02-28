package crazypants.enderio.base.recipe.alloysmelter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.recipe.IMachineRecipe;
import crazypants.enderio.base.recipe.IRecipe;
import crazypants.enderio.base.recipe.MachineRecipeInput;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.Recipe;
import crazypants.enderio.base.recipe.RecipeBonusType;
import crazypants.enderio.base.recipe.RecipeInput;
import crazypants.enderio.base.recipe.RecipeOutput;
import crazypants.enderio.util.Prep;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;

public class VanillaSmeltingRecipe implements IMachineRecipe {

  // We will use the same energy as per a standard furnace.
  // To do the conversion between fuel burning and RF, use the Stirling Gen
  // which produces ten RF per tick of burn time
  private static final int RF_PER_ITEM = TileEntityFurnace.getItemBurnTime(new ItemStack(Items.COAL, 1, 0)) * 10 / 8;

  @Override
  public @Nonnull String getUid() {
    return "VanillaSmeltingRecipe";
  }

  @Override
  public int getEnergyRequired(@Nonnull NNList<MachineRecipeInput> inputs) {
    int numInputs = getNumInputs(inputs);
    return numInputs * RF_PER_ITEM;
  }

  @Override
  public @Nonnull RecipeBonusType getBonusType(@Nonnull NNList<MachineRecipeInput> inputs) {
    return RecipeBonusType.NONE;
  }

  private int getNumInputs(@Nonnull NNList<MachineRecipeInput> inputs) {
    int numInputs = 0;
    for (MachineRecipeInput input : inputs) {
      if (input != null && isValidInput(input)) {
        numInputs += input.item.getCount();
      }
    }
    return Math.min(numInputs, 3);
  }

  @Override
  public boolean isRecipe(@Nonnull NNList<MachineRecipeInput> inputs) {
    ItemStack output = Prep.getEmpty();
    for (MachineRecipeInput ri : inputs) {
      if (ri != null && Prep.isValid(ri.item)) {
        if (Prep.isInvalid(output)) {
          output = FurnaceRecipes.instance().getSmeltingResult(ri.item);
          if (Prep.isInvalid(output)) {
            return false;
          }
        } else {
          ItemStack newOutput = FurnaceRecipes.instance().getSmeltingResult(ri.item);
          if (!newOutput.isItemEqual(output)) {
            return false;
          }
        }
      }
    }
    return Prep.isValid(output);
  }

  @Override
  public @Nonnull ResultStack[] getCompletedResult(long nextSeed, float chanceMultiplier, @Nonnull NNList<MachineRecipeInput> inputs) {
    ItemStack output = null;
    for (MachineRecipeInput ri : inputs) {
      if (ri != null && Prep.isValid(ri.item) && output == null) {
        output = FurnaceRecipes.instance().getSmeltingResult(ri.item);
      }
    }
    if (output == null) {
      return new ResultStack[0];
    }
    int stackSize = output.getCount();
    ItemStack result = output.copy();
    result.setCount(stackSize * getNumInputs(inputs));
    return new ResultStack[] { new ResultStack(result) };
  }

  @Override
  public float getExperienceForOutput(@Nonnull ItemStack output) {
    float result = FurnaceRecipes.instance().getSmeltingExperience(output);
    if (result > 1.0f) {
      // see net.minecraft.inventory.SlotFurnace.onCrafting(ItemStack)
      result = 1.0f;
    }
    return result * output.getCount();
  }

  @Override
  public boolean isValidInput(@Nonnull MachineRecipeInput input) {
    ItemStack itemstack = FurnaceRecipes.instance().getSmeltingResult(input.item);
    return Prep.isValid(itemstack);
  }

  @Override
  public @Nonnull String getMachineName() {
    return MachineRecipeRegistry.ALLOYSMELTER;
  }

  @Override
  public @Nonnull List<MachineRecipeInput> getQuantitiesConsumed(@Nonnull NNList<MachineRecipeInput> inputs) {
    int consumed = 0;
    List<MachineRecipeInput> result = new ArrayList<MachineRecipeInput>();
    for (MachineRecipeInput ri : inputs) {
      if (ri != null && Prep.isValid(ri.item) && isValidInput(new MachineRecipeInput(ri.slotNumber, ri.item)) && consumed < 3) {
        int available = ri.item.getCount();
        int canUse = 3 - consumed;
        int use = Math.min(canUse, available);
        if (use > 0) {
          ItemStack st = ri.item.copy();
          st.setCount(use);
          result.add(new MachineRecipeInput(ri.slotNumber, st));
          consumed += use;
        }
      }
    }
    return result;
  }

  public List<IRecipe> getAllRecipes() {
    List<IRecipe> result = new ArrayList<IRecipe>();
    Map<ItemStack, ItemStack> metaList = FurnaceRecipes.instance().getSmeltingList();
    for (Entry<ItemStack, ItemStack> entry : metaList.entrySet()) {
      ItemStack output = entry.getValue();
      int stackSize = output.getCount();
      output.setCount(stackSize);
      final ItemStack key = NullHelper.notnullM(entry.getKey(), "null item stack in furnace recipes");
      result.add(new Recipe(new RecipeInput(key), RF_PER_ITEM, RecipeBonusType.NONE, new RecipeOutput(output)));
    }
    return result;
  }

}
