package crazypants.enderio.base.machine.task;

import javax.annotation.Nonnull;

import crazypants.enderio.base.machine.interfaces.IPoweredTask;
import crazypants.enderio.base.recipe.IMachineRecipe;
import crazypants.enderio.base.recipe.MachineRecipeInput;
import crazypants.enderio.base.recipe.RecipeBonusType;
import crazypants.enderio.base.recipe.IMachineRecipe.ResultStack;
import net.minecraft.nbt.NBTTagCompound;

public class ContinuousTask implements IPoweredTask {

  float powerUserPerTick;

  public ContinuousTask(float powerUsePerTick) {
    this.powerUserPerTick = powerUsePerTick;
  }

  @Override
  public void writeToNBT(@Nonnull NBTTagCompound nbtRoot) {
    nbtRoot.setFloat("powerUserPerTick", powerUserPerTick);
  }

  public static IPoweredTask readFromNBT(NBTTagCompound nbtRoot) {
    return new ContinuousTask(nbtRoot.getFloat("powerUserPerTick"));
  }

  @Override
  public void update(float availableEnergy) {
  }

  @Override
  public boolean isComplete() {
    return false;
  }

  @Override
  public float getRequiredEnergy() {
    return powerUserPerTick;
  }

  @Override
  public IMachineRecipe getRecipe() {
    return null;
  }

  @Override
  public float getProgress() {
    return 0.5f;
  }

  @Override
  public @Nonnull ResultStack[] getCompletedResult() {
    return new ResultStack[0];
  }

  @Override
  public float getChance() {
    return 1;
  }

  @Override
  public @Nonnull RecipeBonusType getBonusType() {
    return RecipeBonusType.NONE;
  }

  @Override
  public @Nonnull MachineRecipeInput[] getInputs() {
    return new MachineRecipeInput[0];
  }

}
