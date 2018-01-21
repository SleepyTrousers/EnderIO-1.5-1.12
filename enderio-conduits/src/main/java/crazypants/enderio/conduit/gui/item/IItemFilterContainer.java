package crazypants.enderio.conduit.gui.item;

import crazypants.enderio.base.filter.IItemFilter;

public interface IItemFilterContainer {

  /**
   *
   * @return The ItemFilter in the container
   */
  IItemFilter getItemFilter();

  /**
   * Called when the filter in the container is changed
   */
  void onFilterChanged();

}
