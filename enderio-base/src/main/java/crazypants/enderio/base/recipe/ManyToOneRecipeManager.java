package crazypants.enderio.base.recipe;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.integration.tic.TicProxy;
import crazypants.enderio.util.Prep;
import net.minecraft.item.ItemStack;

public class ManyToOneRecipeManager {

  private final @Nonnull NNList<IManyToOneRecipe> recipes = new NNList<IManyToOneRecipe>();

  private final @Nonnull String managerName;

  public ManyToOneRecipeManager(@Nonnull String managerName) {
    this.managerName = managerName;
  }

  public @Nonnull NNList<IManyToOneRecipe> getRecipes() {
    return recipes;
  }

  public void addRecipe(Recipe rec) {
    if (rec == null) {
      Log.warn("Invalid null recipe found for " + managerName);
    } else if (Config.createSyntheticRecipes //
        && rec.getInputs().length == 1 && !rec.getInputs()[0].isFluid()
        && rec.getInputs()[0].getInput().getCount() <= (rec.getInputs()[0].getInput().getMaxStackSize() / 3) && rec.getOutputs().length == 1
        && !rec.getOutputs()[0].isFluid() //
        && rec.getOutputs()[0].getOutput().getCount() <= (rec.getOutputs()[0].getOutput().getMaxStackSize() / 3)) {

      IRecipe dupe = getRecipeForStacks(rec.getInputStacks());
      if (dupe != null) {
        // do it here because we will add "dupes" and the check need to be
        // done on the supplied recipe---which is added last
        Log.warn("The supplied recipe " + rec + " for " + managerName + " may be a duplicate to: " + dupe);
      }

      int er = rec.getEnergyRequired();
      RecipeBonusType bns = rec.getBonusType();
      RecipeOutput out = rec.getOutputs()[0];
      IRecipeInput in = rec.getInputs()[0];

      IRecipeInput in2 = in.copy();
      in2.shrinkStack(-in.getInput().getCount());
      RecipeOutput out2 = new RecipeOutput(out.getOutput(), out.getChance(), out.getExperiance());
      out2.getOutput().grow(out.getOutput().getCount());

      IRecipeInput in3 = in.copy();
      in3.shrinkStack(-in.getInput().getCount());
      in3.shrinkStack(-in.getInput().getCount());
      RecipeOutput out3 = new RecipeOutput(out.getOutput(), out.getChance(), out.getExperiance());
      out3.getOutput().grow(out.getOutput().getCount());
      out3.getOutput().grow(out.getOutput().getCount());

      recipes.add(new BasicManyToOneRecipe(new Recipe(out3, er * 3, bns, new IRecipeInput[] { in.copy(), in.copy(), in.copy() })).setSynthetic());
      recipes.add(new BasicManyToOneRecipe(new Recipe(out3, er * 3, bns, new IRecipeInput[] { in.copy(), in2.copy() })).setSynthetic());
      recipes.add(new BasicManyToOneRecipe(new Recipe(out3, er * 3, bns, new IRecipeInput[] { in2.copy(), in.copy() })).setSynthetic());
      recipes.add(new BasicManyToOneRecipe(new Recipe(out2, er * 2, bns, new IRecipeInput[] { in.copy(), in.copy() })).setSynthetic());
      recipes.add(new BasicManyToOneRecipe(new Recipe(out3, er * 3, bns, new IRecipeInput[] { in3.copy() })).setSynthetic());
      recipes.add(new BasicManyToOneRecipe(new Recipe(out2, er * 2, bns, new IRecipeInput[] { in2.copy() })).setSynthetic());
      recipes.add(new BasicManyToOneRecipe(rec));
      Log.info("Created 6 synthetic recipes for " + in.getInput() + " => " + out.getOutput());
    } else {
      addRecipe(new BasicManyToOneRecipe(rec));
      if (managerName.equals("Alloy Smelter") && rec.getInputs().length >= 2) {
        NNList<Things> inputs = new NNList<>();
        for (int i = 0; i < rec.getInputs().length; i++) {
          ItemStack input = rec.getInputs()[i].getInput().copy();
          Things inputThing = new Things();
          inputThing.add(input);
          inputThing.setSize(input.getCount());
          inputThing.setNbt(input.getTagCompound());
          inputs.add(inputThing);
        }

        ItemStack output = rec.getOutputs()[0].getOutput().copy();
        Things outputThing = new Things();
        outputThing.add(output);
        outputThing.setSize(output.getCount());
        outputThing.setNbt(output.getTagCompound());

        TicProxy.registerAlloyRecipe(outputThing, inputs);
      }
    }
  }

  public void addRecipe(@Nonnull IManyToOneRecipe recipe) {
    IRecipe rec = getRecipeForStacks(recipe.getInputStacks());
    if (rec != null) {
      Log.warn("The supplied recipe " + recipe + " for " + managerName + " may be a duplicate to: " + rec);
    }
    recipes.add(recipe);
  }

  private IRecipe getRecipeForStacks(@Nonnull NNList<ItemStack> inputs) {
    NNList<MachineRecipeInput> ins = new NNList<>();

    for (ItemStack stack : inputs) {
      ins.add(new MachineRecipeInput(-1, NullHelper.notnullM(stack, "NNList iterated with null")));
    }
    return getRecipeForInputs(ins);
  }

  public IRecipe getRecipeForInputs(@Nonnull NNList<MachineRecipeInput> inputs) {

    for (IManyToOneRecipe rec : recipes) {
      if (rec.isInputForRecipe(inputs)) {
        return rec;
      }
    }
    return null;
  }

  public boolean isValidInput(@Nonnull MachineRecipeInput input) {
    if (Prep.isInvalid(input.item)) {
      return false;
    }
    for (IManyToOneRecipe recipe : recipes) {
      for (IRecipeInput ri : recipe.getInputs()) {
        if (ri.isInput(input.item) && (ri.getSlotNumber() == -1 || input.slotNumber == ri.getSlotNumber())) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean isValidRecipeComponents(@Nonnull ItemStack[] inputs) {
    if (inputs.length == 0) {
      return false;
    }
    for (IManyToOneRecipe recipe : recipes) {
      if (recipe.isValidRecipeComponents(inputs)) {
        return true;
      }
    }
    return false;
  }

  @Nonnull
  public List<IManyToOneRecipe> getRecipesThatHaveTheseAsValidRecipeComponents(@Nonnull ItemStack[] inputs) {
    List<IManyToOneRecipe> result = new ArrayList<IManyToOneRecipe>();
    if (inputs.length > 0) {
      for (IManyToOneRecipe recipe : recipes) {
        if (recipe.isValidRecipeComponents(inputs)) {
          result.add(recipe);
        }
      }
    }
    return result;
  }

  public float getExperianceForOutput(@Nonnull ItemStack output) {
    for (IManyToOneRecipe recipe : recipes) {
      if (recipe.getOutput().getItem() == output.getItem() && recipe.getOutput().getItemDamage() == output.getItemDamage()) {
        return recipe.getOutputs()[0].getExperiance();
      }
    }
    return 0;
  }

}
