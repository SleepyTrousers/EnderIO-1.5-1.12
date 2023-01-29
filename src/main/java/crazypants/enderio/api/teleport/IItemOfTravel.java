package crazypants.enderio.api.teleport;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import cofh.api.energy.IEnergyContainerItem;

public interface IItemOfTravel extends IEnergyContainerItem {

    boolean isActive(EntityPlayer ep, ItemStack equipped);

    void extractInternal(ItemStack equipped, int power);
}
