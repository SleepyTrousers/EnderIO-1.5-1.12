package crazypants.enderio.machine;

import crazypants.enderio.machine.IMachineRecipe.ResultStack;
import crazypants.enderio.machine.recipe.RecipeBonusType;
import net.minecraft.nbt.NBTTagCompound;

public class ContinuousTask implements IPoweredTask {

    float powerUserPerTick;

    public ContinuousTask(float powerUsePerTick) {
        this.powerUserPerTick = powerUsePerTick;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtRoot) {}

    @Override
    public void update(float availableEnergy) {}

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
