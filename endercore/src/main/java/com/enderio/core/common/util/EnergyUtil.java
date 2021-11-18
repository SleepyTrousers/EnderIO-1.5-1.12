package com.enderio.core.common.util;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyUtil {

    public static int getMaxEnergyStored(ItemStack stack) {
        return stack.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getMaxEnergyStored).orElse(0);
    }

    public static int getEnergyStored(ItemStack stack) {
        return stack.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
    }

    public static boolean hasEnergy(ItemStack stack, int amount) {
        return stack.getCapability(CapabilityEnergy.ENERGY).map(storage -> storage.getEnergyStored() >= amount).orElse(false);
    }

    public static void setFull(ItemStack stack) {
        stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(energyStorage -> energyStorage.receiveEnergy(energyStorage.getMaxEnergyStored(), false));
    }

    public static void setEmpty(ItemStack stack) {
        stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(energyStorage -> energyStorage.extractEnergy(energyStorage.getEnergyStored(), false));
    }

    public static void set(ItemStack stack, int energy) {
        stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(energyStorage -> {
            int delta = energy - energyStorage.getEnergyStored();
            if (delta < 0) {
                energyStorage.extractEnergy(-delta, false);
            } else {
                energyStorage.receiveEnergy(delta, false);
            }
        });
    }

    /**
     * Adds energy to the storage. Returns quantity of energy that was accepted.
     *
     * @param maxReceive Maximum amount of energy to be inserted.
     * @param simulate   If TRUE, the insertion will only be simulated.
     * @return Amount of energy that was (or would have been, if simulated) accepted by the storage.
     */
    public static int receiveEnergy(ItemStack stack, int maxReceive, boolean simulate) {
        return stack.getCapability(CapabilityEnergy.ENERGY).map(energyStorage -> energyStorage.receiveEnergy(maxReceive, simulate)).orElse(0);
    }

    /**
     * Removes energy from the storage. Returns quantity of energy that was removed.
     *
     * @param maxExtract Maximum amount of energy to be extracted.
     * @param simulate   If TRUE, the extraction will only be simulated.
     * @return Amount of energy that was (or would have been, if simulated) extracted from the storage.
     */
    public static int extractEnergy(ItemStack stack, int maxExtract, boolean simulate) {
        return stack.getCapability(CapabilityEnergy.ENERGY).map(energyStorage -> energyStorage.extractEnergy(maxExtract, simulate)).orElse(0);
    }
}
