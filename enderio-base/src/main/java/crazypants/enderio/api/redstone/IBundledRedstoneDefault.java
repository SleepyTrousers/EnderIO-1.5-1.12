package crazypants.enderio.api.redstone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

/**
 * This interface provides a default implementation of {@link IBundledRedstone}.
 * <p>
 * Nobody is required to use it, or to even keep the logic---so do not cast other mods' capabilities to this.
 *
 */
public interface IBundledRedstoneDefault extends IBundledRedstone {

  /**
   * 
   * @return The ID of the network this capability is connected to. This ID should never change as long as the network stays the same.
   */
  @Nonnull
  IBundledRedstoneSignalNetwork getNetwork();

  /**
   * 
   * @return A list of all signal sources the network manages. This must not include any signal sources managed by other networks.
   */
  @Nonnull
  List<IBundledRedstoneSignalSource> getManagedSignalSources();

  /**
   * 
   * @return A list of all connections to other networks. This list must include both input and output connections.
   */
  @Nonnull
  List<IBundledRedstone> getConnections();

  /**
   * Clears the cache of cached signal values for signals from other networks.
   */
  void clearCachedSignal();

  /**
   * Clears the cache of signal sources from other sources.
   */
  void clearCachedSources();

  /**
   * 
   * @return <code>true</code> if this connection accepts signals from other networks.
   */
  boolean isInput();

  /**
   * 
   * @return <code>true</code> if this connection provides signals to other networks.
   */
  boolean isOutput();

  @Override
  default @Nonnull List<IBundledRedstoneSignalSource> getSignalSources(@Nonnull Set<IBundledRedstoneSignalNetwork> seen) {
    if (!isOutput() || seen.contains(getNetwork())) {
      return Collections.<IBundledRedstoneSignalSource> emptyList();
    }
    seen.add(getNetwork());
    List<IBundledRedstoneSignalSource> result = new ArrayList<>(getManagedSignalSources());
    for (IBundledRedstone connection : getConnections()) {
      result.addAll(connection.getSignalSources(seen));
    }
    return result;
  }

  @Override
  default void notifySignalSourceChange(@Nonnull Set<IBundledRedstoneSignalNetwork> seen) {
    if (!isInput() || seen.contains(getNetwork())) {
      return;
    }
    seen.add(getNetwork());
    clearCachedSources();
    for (IBundledRedstone connection : getConnections()) {
      connection.notifySignalSourceChange(seen);
    }
  }

  @Override
  default void notifySignalValueChange(@Nonnull Set<IBundledRedstoneSignalNetwork> seen) {
    if (!isInput() || seen.contains(getNetwork())) {
      return;
    }
    seen.add(getNetwork());
    clearCachedSignal();
    for (IBundledRedstone connection : getConnections()) {
      connection.notifySignalValueChange(seen);
    }
  }

}
