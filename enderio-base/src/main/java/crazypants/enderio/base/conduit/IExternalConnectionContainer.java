package crazypants.enderio.base.conduit;

import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.util.NNList;

import net.minecraft.item.ItemStack;

public interface IExternalConnectionContainer {

  /**
   * Allows all input/output slots to be made visible
   */
  void setInOutSlotsVisible(boolean filtersVisible, boolean upgradesVisible, @Nonnull IConduit conduit);

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

  /**
   * Creates default ghost slots (Basic Item Filters, All Upgrades) for a conduit gui
   * 
   * @param ghostSlots
   *          Ghost slot list to add to
   */
  void createGhostSlots(@Nonnull NNList<GhostSlot> ghostSlots);

  /**
   * Creates ghost slots populated with the given items
   * 
   * @param ghostSlots
   *          The list of ghost slots to add to
   * @param filters
   *          The ItemStacks of the filters for the ghost slots
   * @param upgrades
   *          The ItemStacks of the upgrades for the ghost slot
   */
  void createGhostSlots(@Nonnull NNList<GhostSlot> ghostSlots, @Nonnull NNList<ItemStack> filtersIn, @Nonnull NNList<ItemStack> filtersOut,
      @Nonnull NNList<ItemStack> upgrades);

  /**
   * Creates ghost slots with the same filters for each slot
   * 
   * @param ghostSlots
   *          The list of ghost slots to add to
   * @param filters
   *          The ItemStacks of the filters for the ghost slots
   * @param upgrades
   *          The ItemStacks of the upgrades for the ghost slot
   */
  void createGhostSlots(@Nonnull NNList<GhostSlot> ghostSlots, @Nonnull NNList<ItemStack> filters, @Nonnull NNList<ItemStack> upgrades);

  @Nonnull
  List<String> getFunctionUpgradeToolTipText();
}
