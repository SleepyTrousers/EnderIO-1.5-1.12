package crazypants.enderio.base.machine.interfaces;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.recipe.IMachineRecipe;
import crazypants.enderio.base.recipe.IMachineRecipe.ResultStack;
import crazypants.enderio.base.recipe.MachineRecipeInput;
import crazypants.enderio.base.recipe.RecipeBonusType;
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

  public abstract @Nonnull NNList<MachineRecipeInput> getInputs();

}
