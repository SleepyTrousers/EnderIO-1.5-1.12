package crazypants.enderio.base.filter;

public interface IFilterContainer<I extends IFilter> {

  I getFilter(int filterIndex);

}
