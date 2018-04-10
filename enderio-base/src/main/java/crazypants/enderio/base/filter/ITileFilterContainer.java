package crazypants.enderio.base.filter;

/**
 * Interface to allow the implementation of filter access for tile entities. Used on conduit bundles and vac chest
 *
 */
public interface ITileFilterContainer {

  void setFilter(int filterIndex, int param, IFilter filter);

}
