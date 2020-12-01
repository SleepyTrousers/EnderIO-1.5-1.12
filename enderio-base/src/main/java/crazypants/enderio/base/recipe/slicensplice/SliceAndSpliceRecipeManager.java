package crazypants.enderio.base.recipe.slicensplice;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.recipe.AbstractMachineRecipe;
import crazypants.enderio.base.recipe.BasicManyToOneRecipe;
import crazypants.enderio.base.recipe.IManyToOneRecipe;
import crazypants.enderio.base.recipe.IRecipe;
import crazypants.enderio.base.recipe.IRecipeInput;
import crazypants.enderio.base.recipe.MachineRecipeInput;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.Recipe;
import crazypants.enderio.base.recipe.RecipeLevel;
import crazypants.enderio.util.Prep;
import net.minecraft.item.ItemStack;

public class SliceAndSpliceRecipeManager extends AbstractMachineRecipe {

  private static final @Nonnull String NAME = "Slice'N'Splice";

  private static final @Nonnull SliceAndSpliceRecipeManager instance = new SliceAndSpliceRecipeManager();

  public static @Nonnull SliceAndSpliceRecipeManager getInstance() {
    return instance;
  }

  private final @Nonnull NNList<IManyToOneRecipe> recipes = new NNList<IManyToOneRecipe>();

  private SliceAndSpliceRecipeManager() {
  }

  public void create() {
    MachineRecipeRegistry.instance.registerRecipe(this);
  }

  @Nonnull
  public NNList<IManyToOneRecipe> getRecipes() {
    return recipes;
  }

  public void addRecipe(@Nonnull Recipe rec) {
    addRecipe(new BasicManyToOneRecipe(rec));
  }

  public void addRecipe(@Nonnull IManyToOneRecipe recipe) {
    IRecipe rec = getRecipeForStacks(recipe.getInputStacks());
    if (rec != null) {
      Log.warn("The supplied recipe " + recipe + " for " + NAME + " may be a duplicate to: " + rec);
    }
    addRecipeInternal(recipe);
  }

  protected void addRecipeInternal(IManyToOneRecipe recipe) {
    recipes.add(recipe);
  }

  private IRecipe getRecipeForStacks(@Nonnull NNList<ItemStack> inputs) {
    NNList<MachineRecipeInput> ins = new NNList<>();

    for (ItemStack stack : inputs) {
      ins.add(new MachineRecipeInput(-1, NullHelper.notnullM(stack, "NNList iterated with null")));
    }
    return getRecipeForInputs(RecipeLevel.IGNORE, ins);
  }

  @Override
  public IRecipe getRecipeForInputs(@Nonnull RecipeLevel machineLevel, @Nonnull NNList<MachineRecipeInput> inputs) {
    for (IManyToOneRecipe rec : recipes) {
      if (machineLevel.canMake(rec.getRecipeLevel()) && rec.isInputForRecipe(inputs)) {
        return rec;
      }
    }
    return null;
  }

  @Override
  public boolean isValidInput(@Nonnull RecipeLevel machineLevel, @Nonnull MachineRecipeInput input) {
    if (Prep.isInvalid(input.item)) {
      return false;
    }
    for (IManyToOneRecipe recipe : recipes) {
      if (machineLevel.canMake(recipe.getRecipeLevel())) {
        for (IRecipeInput ri : recipe.getInputs()) {
          if (ri.isInput(input.item) && (ri.getSlotNumber() == -1 || input.slotNumber == ri.getSlotNumber())) {
            return true;
          }
        }
      }
    }
    return false;
  }

  @Nonnull
  public List<IManyToOneRecipe> getRecipesThatHaveTheseAsValidRecipeComponents(@Nonnull NNList<ItemStack> input) {
    List<IManyToOneRecipe> result = new ArrayList<IManyToOneRecipe>();
    for (IManyToOneRecipe recipe : recipes) {
      if (recipe.isValidRecipeComponents(input)) {
        result.add(recipe);
      }
    }
    return result;
  }

  @Override
  public float getExperienceForOutput(@Nonnull ItemStack output) {
    for (IManyToOneRecipe recipe : recipes) {
      if (recipe.getOutput().getItem() == output.getItem() && recipe.getOutput().getItemDamage() == output.getItemDamage()) {
        return recipe.getOutputs()[0].getExperiance();
      }
    }
    return 0;
  }

  @Override
  public @Nonnull String getUid() {
    return "SpliceAndSpliceRecipe";
  }

  @Override
  public @Nonnull String getMachineName() {
    return MachineRecipeRegistry.SLICENSPLICE;
  }

}
