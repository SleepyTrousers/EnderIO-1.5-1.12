package crazypants.enderio.machine.interfaces;

import javax.annotation.Nullable;

import crazypants.enderio.recipe.IMachineRecipe;
import crazypants.enderio.recipe.MachineRecipeInput;
import crazypants.enderio.recipe.RecipeBonusType;
import crazypants.enderio.recipe.IMachineRecipe.ResultStack;
import net.minecraft.nbt.NBTTagCompound;

public interface IPoweredTask {

  void update(float availableEnergy);

  boolean isComplete();

  float getProgress();

  ResultStack[] getCompletedResult();

  float getRequiredEnergy();

  float getChance();

  RecipeBonusType getBonusType();

  void writeToNBT(NBTTagCompound nbtRoot);

  @Nullable
  IMachineRecipe getRecipe();

  public abstract MachineRecipeInput[] getInputs();

}
