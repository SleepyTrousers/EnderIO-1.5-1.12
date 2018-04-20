package crazypants.enderio.base.filter;

import javax.annotation.Nonnull;

public interface IFilterContainer<I extends IFilter> {

  @Nonnull
  I getFilter(int filterIndex);

}
