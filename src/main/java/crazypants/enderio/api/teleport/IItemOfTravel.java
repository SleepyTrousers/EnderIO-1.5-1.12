package crazypants.enderio.api.teleport;

import cofh.api.energy.IEnergyContainerItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IItemOfTravel extends IEnergyContainerItem {

    boolean isActive(EntityPlayer ep, ItemStack equipped);

    void extractInternal(ItemStack equipped, int power);
}
