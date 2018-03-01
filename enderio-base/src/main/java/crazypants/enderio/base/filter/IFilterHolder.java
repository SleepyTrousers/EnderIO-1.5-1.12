package crazypants.enderio.base.filter;

import javax.annotation.Nonnull;

public interface IFilterHolder {

  /**
   * Gets the filter from the TileEntity with an additional parameter for extra information
   * 
   * @param filterId
   *          the filter to get (e.g. conduits have an input and output filter, which are 0 and 1 respectively)
   * @param param1
   *          an extra parameter for locating the filter (e.g. conduits use this as the ordinal dir)
   * @return the filter found from the given parameters
   */
  IItemFilter getFilter(int filterId, int param1);

  void setFilter(int filterId, int param1, @Nonnull IItemFilter filter);

}
