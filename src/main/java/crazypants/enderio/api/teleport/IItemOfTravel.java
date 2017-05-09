package crazypants.enderio.api.teleport;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public interface IItemOfTravel {

  boolean isActive(EntityPlayer ep, @Nonnull ItemStack equipped);

  void extractInternal(@Nonnull ItemStack item, int power);

  int getEnergyStored(@Nonnull ItemStack item);

}
