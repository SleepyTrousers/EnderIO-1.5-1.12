package crazypants.enderio.machine;

import net.minecraft.nbt.NBTTagCompound;
import crazypants.enderio.machine.IMachineRecipe.ResultStack;
import crazypants.enderio.machine.recipe.RecipeBonusType;

public interface IPoweredTask {

  void update(float availableEnergy);

  boolean isComplete();

  float getProgress();

  ResultStack[] getCompletedResult();

  float getRequiredEnergy();

  float getChance();

  RecipeBonusType getBonusType();

  void writeToNBT(NBTTagCompound nbtRoot);

  IMachineRecipe getRecipe();

  public abstract MachineRecipeInput[] getInputs();

}
