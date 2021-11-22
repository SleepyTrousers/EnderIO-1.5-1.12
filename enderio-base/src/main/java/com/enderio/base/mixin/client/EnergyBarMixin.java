package com.enderio.base.mixin.client;

import com.enderio.base.common.item.util.IEnergyBar;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeItem;
import net.minecraftforge.energy.CapabilityEnergy;
import org.spongepowered.asm.mixin.Mixin;

import static com.enderio.base.client.renderer.DarkSteelDurabilityRenderer.ENERGY_BAR_RGB;

/**
 * This mixin attaches to any item implementing {@link IEnergyBar}.
 *
 * Overrides three methods in {@link IForgeItem} to display a purple durability bar, and uses an attached {@link net.minecraftforge.energy.IEnergyStorage} capability.
 *
 * Future TODO: HOUSEKEEPING: Add tooltips for energy here too? Rename IEnergyBar to IEnergyDisplay in doing so?
 */
@Mixin(IEnergyBar.class)
public interface EnergyBarMixin extends IForgeItem {
    @Override
    default boolean showDurabilityBar(ItemStack stack) {
        // We always display a durability bar so that players know the tool is empty or full all the time.
        return true;
    }

    @Override
    default double getDurabilityForDisplay(ItemStack stack) {
        return stack
            .getCapability(CapabilityEnergy.ENERGY)
            .map(energyStorage -> 1.0d - (double) energyStorage.getEnergyStored() / (double) energyStorage.getMaxEnergyStored())
            .orElse(0d);
    }

    @Override
    default int getRGBDurabilityForDisplay(ItemStack stack) {
        return ENERGY_BAR_RGB; // A nice purple
    }
}
