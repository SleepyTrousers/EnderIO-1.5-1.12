package crazypants.enderio.api.teleport;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IItemOfTravel {

  boolean isActive(EntityPlayer ep, ItemStack equipped);

  void extractInternal(ItemStack item, int power);

  int getEnergyStored(ItemStack item);

}
