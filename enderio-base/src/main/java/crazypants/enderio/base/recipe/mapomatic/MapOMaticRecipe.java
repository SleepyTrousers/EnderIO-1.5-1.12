package crazypants.enderio.base.recipe.mapomatic;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.recipe.AbstractMachineRecipe;
import crazypants.enderio.base.recipe.IRecipe;
import crazypants.enderio.base.recipe.MachineRecipeInput;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class MapOMaticRecipe extends AbstractMachineRecipe {

  private static int id = 0;

  private final @Nonnull String structure;

  public MapOMaticRecipe(@Nonnull String structure) {
    this.structure = structure;
  }

  @Nonnull
  public String getStructure() {
    return structure;
  }

  @Override
  public IRecipe getRecipeForInputs(@Nonnull NNList<MachineRecipeInput> inputs) {
    return MapOMaticRecipeManager.getInstance().getRecipeForInputs(inputs);
  }

  @Nonnull
  @Override
  public String getUid() {
    return "MapOMaticRecipe";
  }

  @Override
  public boolean isValidInput(@Nonnull MachineRecipeInput input) {
    return MapOMaticRecipeManager.getInstance().isValidInput(input);
  }

  @Nonnull
  @Override
  public String getMachineName() {
    return MachineRecipeRegistry.MAPOMATIC;
  }

  @Nonnull
  @Override
  public ResultStack[] getCompletedResult(long nextSeed, float chanceMultiplier, @Nonnull NNList<MachineRecipeInput> inputs) {
    ItemStack output = new ItemStack(Items.FILLED_MAP, 1, id++);
    return new ResultStack[] { new ResultStack(output) };
  }
}
