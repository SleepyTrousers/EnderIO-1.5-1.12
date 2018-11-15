package crazypants.enderio.conduits.conduit;

import java.util.Map;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.DyeColor;

import net.minecraft.util.EnumFacing;

/**
 * Interface for conduits that have advanced networking features, including priorities, self-feed, color channels and round robin.
 * 
 * Note: Input in the context of conduits means "into the conduit network" and output means "out of the conduit network"
 */
public interface IEnderConduit {

  /**
   * Gets the conduit output priority for a given direction
   * 
   * @param dir
   *          Direction of the conduit connection
   * @return Output priority
   */
  default int getOutputPriority(@Nonnull EnumFacing dir) {
    Integer res = getOutputPriorities().get(dir);
    if (res == null) {
      return 0;
    }
    return res;
  }

  /**
   * Sets the conduit output priority for a given direction
   * 
   * @param dir
   *          Direction of the conduit connection
   * @param priority
   *          The priority of the output
   */
  default void setOutputPriority(@Nonnull EnumFacing dir, int priority) {
    if (priority == 0) {
      getOutputPriorities().remove(dir);
    } else {
      getOutputPriorities().put(dir, priority);
    }
    refreshConnection(dir);
  }

  /**
   * Checks if the given conduit connection has self feed on
   * 
   * @param dir
   *          Direction of the conduit connection
   * @return true if self feed is active
   */
  default boolean isSelfFeedEnabled(@Nonnull EnumFacing dir) {
    Boolean val = getSelfFeed().get(dir);
    if (val == null) {
      return false;
    }
    return val;
  }

  /**
   * Sets self feed for a given conduit connection
   * 
   * @param dir
   *          Direction of the conduit connection
   * @param enabled
   *          true to enable self feed, false to disable it
   */
  default void setSelfFeedEnabled(@Nonnull EnumFacing dir, boolean enabled) {
    if (!enabled) {
      getSelfFeed().remove(dir);
    } else {
      getSelfFeed().put(dir, enabled);
    }
    refreshConnection(dir);
  }

  /**
   * Checks if the given conduit connection is using round robin to route items it takes as an input
   * 
   * @param dir
   *          Direction of the conduit connection
   * @return true if the given input connection has round robin enabled
   */
  default boolean isRoundRobinEnabled(@Nonnull EnumFacing dir) {
    Boolean val = getRoundRobin().get(dir);
    if (val == null) {
      return false;
    }
    return val;
  }

  /**
   * Sets round robin for a given conduit connection
   * 
   * @param dir
   *          Direction of a conduit connection
   * @param enabled
   *          true to enable round robin, false to disable it
   */
  default void setRoundRobinEnabled(@Nonnull EnumFacing dir, boolean enabled) {
    if (!enabled) {
      getRoundRobin().remove(dir);
    } else {
      getRoundRobin().put(dir, enabled);
    }
    refreshConnection(dir);
  }

  /**
   * Gets the color channel for the given conduit input connection
   * 
   * @param dir
   *          Direction of the conduit connection
   * @return The color channel of the input
   */
  default @Nonnull DyeColor getInputColor(@Nonnull EnumFacing dir) {
    DyeColor result = getInputColors().get(dir);
    if (result == null) {
      return DyeColor.GREEN;
    }
    return result;
  }

  /**
   * Gets the color channel for the given conduit output connection
   * 
   * @param dir
   *          Direction of the conduit connection
   * @return The color channel of the output
   */
  default @Nonnull DyeColor getOutputColor(@Nonnull EnumFacing dir) {
    DyeColor result = getOutputColors().get(dir);
    if (result == null) {
      return DyeColor.GREEN;
    }
    return result;
  }

  /**
   * Sets the color channel for the given conduit input connection
   * 
   * @param dir
   *          Direction of the conduit connection
   * @param col
   *          The color to set the input connection channel to
   */
  default void setInputColor(@Nonnull EnumFacing dir, @Nonnull DyeColor col) {
    getInputColors().put(dir, col);
    refreshConnection(dir);
    setClientDirty();
  }

  /**
   * Sets the color channel for the given conduit output connection
   * 
   * @param dir
   *          Direction of the conduit connection
   * @param col
   *          The color to set the output connection channel to
   */
  default void setOutputColor(@Nonnull EnumFacing dir, @Nonnull DyeColor col) {
    getOutputColors().put(dir, col);
    refreshConnection(dir);
    setClientDirty();
  }

  /**
   * Gets the input connection colors for the conduit
   * 
   * @return The map of input colors to their respective directions
   */
  @Nonnull
  Map<EnumFacing, DyeColor> getInputColors();

  /**
   * Gets the output connection colors for the conduit
   * 
   * @return The map of the output colors to the respective directions
   */
  @Nonnull
  Map<EnumFacing, DyeColor> getOutputColors();

  /**
   * Gets the map of self feed status for each connection
   * 
   * @return The map of self feed status for each direction
   */
  @Nonnull
  Map<EnumFacing, Boolean> getSelfFeed();

  /**
   * Gets the map of round robin status for each connection
   * 
   * @return The map of round robin status for each direction
   */
  @Nonnull
  Map<EnumFacing, Boolean> getRoundRobin();

  /**
   * Gets the output priority for each conduit connection
   * 
   * @return The map of the output priority for each direction
   */
  @Nonnull
  Map<EnumFacing, Integer> getOutputPriorities();

  /**
   * Refreshes a given conduit connection
   * 
   * @param dir
   *          The direction of the conduit connection
   */
  void refreshConnection(@Nonnull EnumFacing dir);

  /**
   * Used to trigger a client side update
   */
  void setClientDirty();

}
