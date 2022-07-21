package crazypants.enderio.machine.wireless;

import com.enderio.core.common.util.BlockCoord;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IWirelessCharger {

    World getWorld();

    BlockCoord getLocation();

    boolean chargeItems(ItemStack[] items);

    int takeEnergy(int max);

    /**
     * Can prevent {@link #chargeItems(ItemStack[])} from being called.
     *
     * @return If this charger is "active". If the charger is not active it will
     *         not be attempted to be used.
     */
    boolean isActive();
}
