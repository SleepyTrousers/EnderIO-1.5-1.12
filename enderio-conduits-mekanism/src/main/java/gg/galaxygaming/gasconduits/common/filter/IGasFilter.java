package gg.galaxygaming.gasconduits.common.filter;

import crazypants.enderio.base.filter.IFilter;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.gas.GasStack;
import net.minecraft.item.ItemStack;

public interface IGasFilter extends IFilter {

    /**
     * Checks if the filter has no gas stacks
     *
     * @return true if the filter has no gases
     */
    @Override
    boolean isEmpty();

    /**
     * Gets the number of gases in the filter
     *
     * @return The number of gases in the filter
     */
    int size();

    /**
     * Gets the gas stack at the given index
     *
     * @param index The index of the gas
     *
     * @return GasStack at the given index, null if there is none
     */
    @Nullable
    GasStack getGasStackAt(int index);

    /**
     * Sets the gas in the given slot
     *
     * @param index Index of the slot
     * @param gas   Gas to insert. Gas can be null to make the slot empty
     *
     * @return true if the gas was successfully set
     */
    boolean setGas(int index, @Nullable GasStack gas);

    /**
     * Sets the gas from the ItemStack
     *
     * @param index Index of the gas filter
     * @param stack The ItemStack to get the gas from
     *
     * @return true if the gas is successfully set
     */
    boolean setGas(int index, @Nonnull ItemStack stack);

    /**
     * Checks the whitelist/blacklist setting of the filter
     *
     * @return true if the blacklist is active
     */
    boolean isBlacklist();

    /**
     * Sets the blacklist/whitelist button
     *
     * @param isBlacklist true if it should be a blacklist, false for whitelist
     */
    void setBlacklist(boolean isBlacklist);

    /**
     * Checks if the filter matches the default filter setting
     *
     * @return true if the filter has no different settings to a freshly made one
     */
    boolean isDefault();

    /**
     * Checks if the gas matches the filter
     *
     * @param drained Gas to check
     *
     * @return true if it matches the filter settings
     */
    boolean matchesFilter(GasStack drained);

    /**
     * Gets the number of slots in the gas filter
     *
     * @return The number of slots for the filter
     */
    int getSlotCount();
}