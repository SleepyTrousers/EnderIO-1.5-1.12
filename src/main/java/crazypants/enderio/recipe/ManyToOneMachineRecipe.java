package crazypants.enderio.recipe;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

public class ManyToOneMachineRecipe extends AbstractMachineRecipe {

  private final @Nonnull String uid;
  private final @Nonnull String machineName;
  private final @Nonnull ManyToOneRecipeManager recipeManager;

  public ManyToOneMachineRecipe(@Nonnull String uid, @Nonnull String machineName, @Nonnull ManyToOneRecipeManager recipeManager) {
    this.uid = uid;
    this.machineName = machineName;
    this.recipeManager = recipeManager;
  }

  @Override
  public @Nonnull String getUid() {
    return uid;
  }

  @Override
  public IRecipe getRecipeForInputs(@Nonnull MachineRecipeInput[] inputs) {
    return recipeManager.getRecipeForInputs(inputs);
  }

  @Override
  public boolean isValidInput(@Nonnull MachineRecipeInput input) {
    return recipeManager.isValidInput(input);
  }

  @Override
  public @Nonnull String getMachineName() {
    return machineName;
  }

  @Override
  public float getExperienceForOutput(@Nonnull ItemStack output) {
    return recipeManager.getExperianceForOutput(output);
  }

  public boolean isValidRecipeComponents(@Nonnull ItemStack[] resultInv) {
    return recipeManager.isValidRecipeComponents(resultInv);
  }

  @Nonnull
  public List<IManyToOneRecipe> getRecipesThatHaveTheseAsValidRecipeComponents(@Nonnull ItemStack[] resultInv) {
    return recipeManager.getRecipesThatHaveTheseAsValidRecipeComponents(resultInv);
  }

}
