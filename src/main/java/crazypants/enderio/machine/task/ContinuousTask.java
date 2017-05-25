package crazypants.enderio.machine.task;

import net.minecraft.nbt.NBTTagCompound;
import crazypants.enderio.machine.interfaces.IPoweredTask;
import crazypants.enderio.recipe.IMachineRecipe;
import crazypants.enderio.recipe.MachineRecipeInput;
import crazypants.enderio.recipe.RecipeBonusType;
import crazypants.enderio.recipe.IMachineRecipe.ResultStack;

public class ContinuousTask implements IPoweredTask {
  
  float powerUserPerTick;
  
  public ContinuousTask(float powerUsePerTick) {
    this.powerUserPerTick = powerUsePerTick;
  }
  
  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {
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
  public ResultStack[] getCompletedResult() {
    return new ResultStack[0];
  }

  @Override
  public float getChance() {
    return 1;
  }

  @Override
  public RecipeBonusType getBonusType() {
    return RecipeBonusType.NONE;
  }

  @Override
  public MachineRecipeInput[] getInputs() {
    return new MachineRecipeInput[0];
  }
}
