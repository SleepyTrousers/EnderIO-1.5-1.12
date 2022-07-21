package crazypants.enderio.item.darksteel;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class EnergyContainer {

    private int capacityRF;
    private int maxReceiveRF;
    private int maxExtractRF;

    public EnergyContainer(int capacityRF, int maxReceiveRF, int maxExtractRF) {
        this.capacityRF = capacityRF;
        this.maxReceiveRF = maxReceiveRF;
        this.maxExtractRF = maxExtractRF;
    }

    public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {

        if (container.stackTagCompound == null) {
            container.stackTagCompound = new NBTTagCompound();
        }
        int energy = container.stackTagCompound.getInteger("Energy");
        int energyReceived = Math.min(capacityRF - energy, Math.min(this.maxReceiveRF, maxReceive));

        if (!simulate) {
            energy += energyReceived;
            container.stackTagCompound.setInteger("Energy", energy);
        }
        return energyReceived;
    }

    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {

        if (container == null || container.stackTagCompound == null || !container.stackTagCompound.hasKey("Energy")) {
            return 0;
        }
        int energy = container.stackTagCompound.getInteger("Energy");
        int energyExtracted = Math.min(energy, Math.min(this.maxExtractRF, maxExtract));

        if (!simulate) {
            energy -= energyExtracted;
            container.stackTagCompound.setInteger("Energy", energy);
        }
        return energyExtracted;
    }

    public static int getEnergyStored(ItemStack container) {
        if (container == null || container.stackTagCompound == null || !container.stackTagCompound.hasKey("Energy")) {
            return 0;
        }
        return container.stackTagCompound.getInteger("Energy");
    }

    public int getMaxEnergyStored(ItemStack container) {
        return capacityRF;
    }

    public static void setEnergy(ItemStack container, int energy) {
        if (container.stackTagCompound == null) {
            container.stackTagCompound = new NBTTagCompound();
        }
        container.stackTagCompound.setInteger("Energy", energy);
    }

    public void setFull(ItemStack container) {
        setEnergy(container, capacityRF);
    }

    public boolean isAbsorbDamageWithPower(ItemStack is) {
        NBTTagCompound root = is.getTagCompound();
        if (root == null) {
            return false;
        }
        return root.getBoolean("absorbWithPower");
    }

    public void setAbsorbDamageWithPower(ItemStack is, boolean val) {
        NBTTagCompound root = is.getTagCompound();
        if (root == null) {
            root = new NBTTagCompound();
            is.setTagCompound(root);
        }
        root.setBoolean("absorbWithPower", val);
    }
}
