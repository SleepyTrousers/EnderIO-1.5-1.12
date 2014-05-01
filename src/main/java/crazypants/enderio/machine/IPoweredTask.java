package crazypants.enderio.machine;

import net.minecraft.nbt.NBTTagCompound;
import crazypants.enderio.machine.IMachineRecipe.ResultStack;

public interface IPoweredTask {

  void update(float availableEnergy);

  boolean isComplete();

  float getProgress();

  ResultStack[] getCompletedResult();

  float getRequiredEnergy();

  float getChance();

  void writeToNBT(NBTTagCompound nbtRoot);

  IMachineRecipe getRecipe();

}