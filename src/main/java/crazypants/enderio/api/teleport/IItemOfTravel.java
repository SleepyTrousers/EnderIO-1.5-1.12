package crazypants.enderio.api.teleport;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IItemOfTravel {

  boolean isActive(@Nonnull EntityPlayer ep, @Nonnull ItemStack equipped);

  void extractInternal(@Nonnull ItemStack item, int power);

  int getEnergyStored(@Nonnull ItemStack item);

}
