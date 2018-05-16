package crazypants.enderio.base.filter.redstone;

import javax.annotation.Nonnull;

public interface IFilterIncrementingValue {

  int getIncrementingValue();

  void setIncrementingValue(int value);

  @Nonnull
  String getFilterHeading();

  @Nonnull
  String getIncrementingValueName();

}
