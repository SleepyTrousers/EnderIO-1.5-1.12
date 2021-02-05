package crazypants.enderio.base.power.wireless;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.BoundingBox;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IWirelessCharger {

  @Nonnull
  World getworld();

  @Nonnull
  BoundingBox getRange() throws SelfDestructionException;

  boolean chargeItems(NonNullList<ItemStack> items);

  int takeEnergy(int max);

  /**
   * Can prevent {@link #chargeItems(NonNullList)} from being called.
   * 
   * @return If this charger is "active". If the charger is not active it will not be attempted to be used.
   */
  boolean isActive();

  @Nonnull
  BlockPos getLocation();

  /**
   * If true, this charger will force force the charging process to end if it reports true from chargeItems().
   * <p>
   * It is not expected that this value differs between chargers, it's here to allow the config value to propagate from machines to base.
   */
  boolean forceSingle();

  /**
   * Messaging exception that tells the calling code that the charger should be (and potentially already has been!) removed from the list of chargers.
   * <p>
   * This is needed to allow modifications of the charger list while it is iterated over.
   */
  public static class SelfDestructionException extends Exception {
    private static final long serialVersionUID = 2001446847785302704L;
  }

}
