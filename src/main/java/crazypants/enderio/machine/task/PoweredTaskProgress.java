package crazypants.enderio.machine.task;

import javax.annotation.Nonnull;

import crazypants.enderio.machine.interfaces.IPoweredTask;
import crazypants.enderio.recipe.IMachineRecipe;
import crazypants.enderio.recipe.IMachineRecipe.ResultStack;
import crazypants.enderio.recipe.MachineRecipeInput;
import crazypants.enderio.recipe.RecipeBonusType;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Client side stub for reporting progress.
 */
public class PoweredTaskProgress implements IPoweredTask {

  private float progress;

  public PoweredTaskProgress(@Nonnull IPoweredTask task) {
    progress = task.getProgress();
  }

  public PoweredTaskProgress(float progress) {
    this.progress = progress;
  }

  @Override
  public void update(float availableEnergy) {
  }

  @Override
  public boolean isComplete() {
    return getProgress() >= 1;
  }

  @Override
  public float getProgress() {
    return progress;
  }

  @Override
  public @Nonnull ResultStack[] getCompletedResult() {
    return new ResultStack[0];
  }

  @Override
  public float getRequiredEnergy() {
    return 0;
  }

  @Override
  public float getChance() {
    return 0;
  }

  @Override
  public @Nonnull RecipeBonusType getBonusType() {
    return RecipeBonusType.NONE;
  }

  @Override
  public void writeToNBT(@Nonnull NBTTagCompound nbtRoot) {
  }

  @Override
  public IMachineRecipe getRecipe() {
    return null;
  }

  @Override
  public @Nonnull MachineRecipeInput[] getInputs() {
    return new MachineRecipeInput[0];
  }

}
