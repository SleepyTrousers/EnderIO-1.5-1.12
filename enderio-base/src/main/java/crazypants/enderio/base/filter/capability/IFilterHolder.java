package crazypants.enderio.base.filter.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.filter.IFilter;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

/**
 * Simple capability handler for Filters
 *
 * @param <T>
 *          Type of Filter
 */
public interface IFilterHolder<T extends IFilter> {

  /**
   * Gets a filter from the capability
   * 
   * @param filterIndex
   *          The id of the filter, specifically used by conduits to identify if the filter is a input or output filter
   * @param param1
   *          An extra parameter for even more specific filter selection, see conduits using it for facing
   * @return the Filter of type T
   */
  T getFilter(int filterIndex, int param1);

  /**
   * Sets the filter of the capability
   * 
   * @param filterIndex
   *          The id of the filter location
   * @param param1
   *          Useful extra parameter for things that cannot be expressed simply by the filter index
   * @param filter
   *          The filter to set it to
   */
  void setFilter(int filterIndex, int param1, @Nonnull T filter);

  /**
   * Used by the existing item filter to get the inventory it is connected to
   * 
   * @param filterIndex
   *          The index of the filter
   * @param param1
   *          Extra param for the filter (e.g. EnumFacing.ordinal() for conduits)
   * @return The inventory for the snapshot
   */
  @Nullable
  default IItemHandler getInventoryForSnapshot(int filterIndex, int param1) {
    return null;
  }

  @Nonnull
  ItemStack getFilterStack(int filterIndex, int param1);

  void setFilterStack(int filterIndex, int param1, @Nonnull ItemStack stack);

}
