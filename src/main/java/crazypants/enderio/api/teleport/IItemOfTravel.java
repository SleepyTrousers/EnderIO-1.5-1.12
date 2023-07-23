package crazypants.enderio.api.teleport;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import cofh.api.energy.IEnergyContainerItem;
import crazypants.enderio.config.Config;

public interface IItemOfTravel extends IEnergyContainerItem {

    boolean isActive(EntityPlayer ep, ItemStack equipped);

    void extractInternal(ItemStack equipped, int power);

    /**
     * @param equipped player who is currently holding this item. caller will ensure the item held has your Item as item
     *                 type
     * @return -1 if method not supported, or max(can extract, power), e.g. 0 if item is empty.
     */
    default int canExtractInternal(ItemStack equipped, int power) {
        return Config.strictTPItemChecking ? 0 : -1;
    }
}
