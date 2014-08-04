package crazypants.enderio.machine.wireless;

import crazypants.util.BlockCoord;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IWirelessCharger {

  World getWorld();

  BlockCoord getLocation();

  boolean chargeItems(ItemStack[] armorInventory);

}
