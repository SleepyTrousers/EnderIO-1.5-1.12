package crazypants.enderio.base.conduit;

import javax.annotation.Nonnull;

public interface IExternalConnectionContainer {

  /**
   * Allows all input/output slots to be made visible
   */
  void setInOutSlotsVisible(boolean inputVisible, boolean outputVisible, IConduit conduit);

  /**
   * Returns true if there are speed upgrades
   */
  boolean hasFunctionUpgrade();

  /**
   * Returns true if the given direction has a filter
   * 
   * @param input
   *          true to check the input filter, false to check the output
   */
  boolean hasFilter(boolean input);

  /**
   * Adds a filter listener to the list
   * 
   * @param listener
   *          Filter Listener
   */
  void addFilterListener(@Nonnull IFilterChangeListener listener);
}
