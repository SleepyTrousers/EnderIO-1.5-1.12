package crazypants.enderio.machine.recipe;

import net.minecraft.item.ItemStack;

public interface IRecipe {

  public abstract boolean isValid();

  public abstract float getEnergyRequired();

  public abstract RecipeOutput[] getOutputs();

  public abstract RecipeInput[] getInputs();

  public abstract ItemStack[] getInputStacks();

  public abstract boolean isInputForRecipe(ItemStack[] test);

}
