package crazypants.enderio.conduit.gui.item;

import crazypants.enderio.conduit.item.filter.IItemFilter;

public interface IItemFilterContainer {

  IItemFilter getItemFilter();

  void onFilterChanged();

}
