package crazypants.enderio.conduit.gui.item;

import crazypants.enderio.base.filter.IItemFilter;

public interface IItemFilterContainer {

  IItemFilter getItemFilter();

  void onFilterChanged();

}
