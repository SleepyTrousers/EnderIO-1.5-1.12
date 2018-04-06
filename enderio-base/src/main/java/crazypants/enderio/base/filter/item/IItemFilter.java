package crazypants.enderio.base.filter.item;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.filter.IFilter;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public interface IItemFilter extends IFilter {

  /**
   * Checks if the given item passes the filter or not.
   * 
   * @param inv
   *          the attached inventory - or null when used without an inventory (eg for a GUI)
   * @param item
   *          the item to check
   * @return true if the item is allowed to pass
   */
  default boolean doesItemPassFilter(@Nullable IItemHandler inv, @Nonnull ItemStack item) {
    return getMaxCountThatPassesFilter(inv, item) > 0;
  };

  /**
   * Checks if the given item passes the filter or not.
   * 
   * @param inv
   *          the attached inventory - or null when used without an inventory (eg for a GUI)
   * @param item
   *          the item to check
   * @return false if the item is not allowed to pass, otherwise the maximum number of items that pass. If the filter doesn't impose a limit, the item's max
   *         stacksize is returned.
   */
  default int getMaxCountThatPassesFilter(@Nullable IItemHandler inv, @Nonnull ItemStack item) {
    return doesItemPassFilter(inv, item) ? item.getMaxStackSize() : 0;
  };

  boolean isValid();

  default boolean isSticky() {
    return false;
  };

  /**
   * @return true if getMaxCountThatPassesFilter() is implemented
   */
  default boolean isLimited() {
    return false;
  };

  public interface WithGhostSlots extends IItemFilter {

    void createGhostSlots(@Nonnull NNList<GhostSlot> slots, int xOffset, int yOffset, @Nullable Runnable cb);
  }

  int getSlotCount();

}
