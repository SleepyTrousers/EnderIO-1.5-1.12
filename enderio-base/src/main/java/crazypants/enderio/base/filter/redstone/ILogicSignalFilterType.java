package crazypants.enderio.base.filter.redstone;

import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.conduit.redstone.signals.BundledSignal;
import crazypants.enderio.base.conduit.redstone.signals.CombinedSignal;

/**
 * Interface for defining "logic filters", or more simply put, filters that take multiple inputs from a bundle. See {@link LogicOutputSignalFilter}
 *
 */
public interface ILogicSignalFilterType {

  /**
   * Applies the filter to the given signal
   * 
   * @param bundledSignal
   *          The bundle of redstone signals to search through
   * @param signalColors
   *          The list of accepted colors
   * @return A CombinedSignal.MAX if conditions are met, else CombinedSignal.NONE
   */
  @Nonnull
  public CombinedSignal apply(@Nonnull BundledSignal bundledSignal, @Nonnull List<DyeColor> signalColors);

  /**
   * Gets the number of buttons for a gui
   * 
   * @return The number of color buttons
   */
  public int getNumButtons();

  /**
   * Gets the heading at the top of the filter gui
   * 
   * @return The filter gui heading String
   */
  @Nonnull
  public String getHeading();

}
