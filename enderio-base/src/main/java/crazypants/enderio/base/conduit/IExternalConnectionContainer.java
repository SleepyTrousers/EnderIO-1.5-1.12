package crazypants.enderio.base.conduit;

public interface IExternalConnectionContainer {

  /**
   * Allows all input/output slots to be made visible
   */
  void setInOutSlotsVisible(boolean inputVisible, boolean outputVisible);

  /**
   * Sets inventory slot visibility
   */
  void setInventorySlotsVisible(boolean visible);

  /**
   * Returns true if there are speed upgrades
   */
  boolean hasSpeedUpgrades();

  /**
   * Returns true if there is a function upgrade
   */
  boolean hasFunctionUpgrade();

  /**
   * Returns true if the given direction has a filter
   * @param input true to check the input filter, false to check the output
   */
  boolean hasFilter(boolean input);

  /**
   * Adds a filter listener to the list
   * @param listener Filter Listener
   */
  void addFilterListener(IFilterChangeListener listener);
}
