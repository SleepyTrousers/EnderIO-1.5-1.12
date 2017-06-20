package crazypants.enderio.machine.wireless;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IWirelessCharger {

  World getworld();

  BlockPos getLocation();

  boolean chargeItems(NonNullList<ItemStack> items);

  int takeEnergy(int max);

  /**
   * Can prevent {@link #chargeItems(NonNullList<ItemStack>)} from being called.
   * 
   * @return If this charger is "active". If the charger is not active it will not be attempted to be used.
   */
  boolean isActive();
}
