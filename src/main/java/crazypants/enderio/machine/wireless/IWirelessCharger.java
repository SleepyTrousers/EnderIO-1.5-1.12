package crazypants.enderio.machine.wireless;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import crazypants.util.BlockCoord;

public interface IWirelessCharger {

  World getWorld();

  BlockCoord getLocation();

  boolean chargeItems(ItemStack[] armorInventory);
  
  int takeEnergy(int max);
}
