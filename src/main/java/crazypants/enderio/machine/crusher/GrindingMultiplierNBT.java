package crazypants.enderio.machine.crusher;

import net.minecraft.nbt.NBTTagCompound;

public class GrindingMultiplierNBT implements IGrindingMultiplier {

    float chanceMultiplier = 1;

    float powerMultiplier = 1;

    float grindingMultiplier = 1;

    int durationMJ;

    private static String CM = "grindBall.chanceMultiplier";
    private static String PM = "grindBall.powerMultiplier";
    private static String GM = "grindBall.grindingMultiplier";
    private static String DMJ = "grindBall.durationMJ";

    public static GrindingMultiplierNBT readFromNBT(NBTTagCompound nbtRoot) {
        if (nbtRoot.hasKey(CM) && nbtRoot.hasKey(PM) && nbtRoot.hasKey(GM) && nbtRoot.hasKey(DMJ)) {
            return new GrindingMultiplierNBT(
                    nbtRoot.getFloat(CM), nbtRoot.getFloat(PM), nbtRoot.getFloat(GM), nbtRoot.getInteger(DMJ));
        }
        return null;
    }

    public static void writeToNBT(IGrindingMultiplier gm, NBTTagCompound nbtRoot) {
        if (gm != null) {
            nbtRoot.setFloat(CM, gm.getChanceMultiplier());
            nbtRoot.setFloat(PM, gm.getPowerMultiplier());
            nbtRoot.setFloat(GM, gm.getGrindingMultiplier());
            nbtRoot.setInteger(DMJ, gm.getDurationMJ());
        }
    }

    protected GrindingMultiplierNBT(
            float chanceMultiplier, float powerMultiplier, float grindingMultiplier, int durationMJ) {
        this.chanceMultiplier = chanceMultiplier;
        this.powerMultiplier = powerMultiplier;
        this.grindingMultiplier = grindingMultiplier;
        this.durationMJ = durationMJ;
    }

    @Override
    public float getGrindingMultiplier() {
        return grindingMultiplier;
    }

    @Override
    public float getChanceMultiplier() {
        return chanceMultiplier;
    }

    @Override
    public float getPowerMultiplier() {
        return powerMultiplier;
    }

    @Override
    public void setChanceMultiplier(float chanceMultiplier) {
        this.chanceMultiplier = chanceMultiplier;
    }

    @Override
    public void setPowerMultiplier(float powerMultiplier) {
        this.powerMultiplier = powerMultiplier;
    }

    @Override
    public void setGrindingMultiplier(float grindingMultiplier) {
        this.grindingMultiplier = grindingMultiplier;
    }

    @Override
    public int getDurationMJ() {
        return durationMJ;
    }

    @Override
    public void setDurationMJ(int durationMJ) {
        this.durationMJ = durationMJ;
    }
}
