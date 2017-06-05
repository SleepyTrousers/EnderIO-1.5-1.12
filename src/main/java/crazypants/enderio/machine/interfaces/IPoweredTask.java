package crazypants.enderio.machine.interfaces;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.recipe.IMachineRecipe;
import crazypants.enderio.recipe.IMachineRecipe.ResultStack;
import crazypants.enderio.recipe.MachineRecipeInput;
import crazypants.enderio.recipe.RecipeBonusType;
import net.minecraft.nbt.NBTTagCompound;

public interface IPoweredTask {

  void update(float availableEnergy);

  boolean isComplete();

  float getProgress();

  @Nonnull
  ResultStack[] getCompletedResult();

  float getRequiredEnergy();

  float getChance();

  @Nonnull
  RecipeBonusType getBonusType();

  void writeToNBT(@Nonnull NBTTagCompound nbtRoot);

  @Nullable
  IMachineRecipe getRecipe();

  public abstract @Nonnull MachineRecipeInput[] getInputs();

}
