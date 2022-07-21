package crazypants.enderio.conduit.gui.item;

import crazypants.enderio.conduit.item.filter.ItemFilter;

public interface IItemFilterContainer {

    ItemFilter getItemFilter();

    void onFilterChanged();
}
