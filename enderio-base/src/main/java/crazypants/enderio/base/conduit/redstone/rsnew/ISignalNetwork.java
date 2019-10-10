package crazypants.enderio.base.conduit.redstone.rsnew;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.registry.ConduitRegistry;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import static net.minecraftforge.event.ForgeEventFactory.onNeighborNotify;

public interface ISignalNetwork {

  /**
   * Adds or updates a signal.
   * <p>
   * Note that an updated signal still needs to be dirty to be acquired, this only causes the overall state to be set to dirty.
   * 
   * @param signal
   */
  void addSignal(@Nonnull ISignal signal);

  void removeSignal(@Nonnull ISignal signal);

  void removeSignal(@Nonnull UID uuid);

  default void removeAllSignals(@Nonnull BlockPos source) {
    NNList.FACING.apply(side -> {
      removeSignal(new UID(source.offset(side), side));
    });
  }

  void addOutput(@Nonnull IOutput output);

  void removeOutput(@Nonnull IOutput output);

  void removeOutput(@Nonnull UID uuid);

  default void removeAllOutputs(@Nonnull BlockPos source) {
    NNList.FACING.apply(side -> {
      removeOutput(new UID(source.offset(side), side));
    });
  }

  default void removeConduit(@Nonnull IConduit conduit) {
    removeAllSignals(conduit.getBundle().getLocation());
    removeAllOutputs(conduit.getBundle().getLocation());
  }

  /**
   * Notifies the network that one or more signals have become dirty. The network will find out which signals are dirty and acquire them later.
   * <p>
   * Note that {@link #addSignal(ISignal)}, {@link #removeSignal(ISignal)} and {@link #removeSignal(crazypants.enderio.base.conduit.redstone.rsnew.ISignal.UID)}
   * automatically set the network status to dirty.
   */
  void setSignalsDirty();

  /**
   * Gets the cached signal level for the given channel.
   * 
   * @param channel
   * @return a signal level
   */
  int getSignalLevel(@Nonnull EnumDyeColor channel);

  class Temp implements ISignalNetwork {

    private static final @Nonnull NNList<EnumDyeColor> CHANNELS = NNList.of(EnumDyeColor.class);

    private final @Nonnull Map<UID, ISignal> signals = new HashMap<>();
    private final @Nonnull Map<UID, ISignal> tickingSignals = new HashMap<>();
    private final @Nonnull Map<UID, IOutput> outputs = new HashMap<>();
    private boolean signalsDirty = false;

    private final @Nonnull EnumMap<EnumDyeColor, Integer> levels = new EnumMap<>(EnumDyeColor.class);

    @Override
    public void addSignal(@Nonnull ISignal signal) {
      signals.put(signal.getUID(), signal);
      if (signal.needsTicking()) {
        tickingSignals.put(signal.getUID(), signal);
      }
      signalsDirty = true;
    }

    @Override
    public void removeSignal(@Nonnull ISignal signal) {
      removeSignal(signal.getUID());
    }

    @Override
    public void removeSignal(@Nonnull UID uuid) {
      if (signals.remove(uuid) != null) {
        signalsDirty = true;
      }
      tickingSignals.remove(uuid);
    }

    @Override
    public void addOutput(@Nonnull IOutput output) {
      outputs.put(output.getUID(), output);
    }

    @Override
    public void removeOutput(@Nonnull IOutput output) {
      removeOutput(output.getUID());
    }

    @Override
    public void removeOutput(@Nonnull UID uuid) {
      outputs.remove(uuid);
    }

    @Override
    public void setSignalsDirty() {
      signalsDirty = true;
    }

    @Override
    public int getSignalLevel(@Nonnull EnumDyeColor channel) {
      return NullHelper.first(levels.get(channel), 0);
    }

    public void tick() {
      // acquire input
      int tickcount = 0;
      Set<EnumDyeColor> changes = new HashSet<>();
      do {
        Set<EnumDyeColor> tickchanges = new HashSet<>();
        if (signalsDirty
            && signals.values().stream().filter(signal -> signal.isDirty()).map(signal -> signal.acquire(this)).reduce(false, this::anyMatchNoShortCircuit)) {
          CHANNELS.apply(channel -> {
            int value = 0;
            for (ISignal signal : signals.values()) {
              value = Math.max(value, signal.get(channel));
            }
            if (levels.get(channel) != value) {
              levels.put(channel, value);
              tickchanges.add(channel);
            }
          });
        }
        final boolean firstTick = tickcount == 0;
        signalsDirty = tickingSignals.values().stream().map(signal -> signal.tick(this, tickchanges, firstTick)).reduce(false, this::anyMatchNoShortCircuit);
        changes.addAll(tickchanges);
      } while (signalsDirty && tickcount++ < 4);
      // send output
      if (!changes.isEmpty()) {
        outputs.values().stream().flatMap(output -> output.getNotificationTargets(this, changes).stream()).collect(Collectors.toSet()).stream()
            .filter(t -> t.getWorld().isBlockLoaded(t.getFrom()) && t.getWorld().isBlockLoaded(t.getTo())
                && !onNeighborNotify(t.getWorld(), t.getFrom(), t.getWorld().getBlockState(t.getFrom()), EnumSet.allOf(EnumFacing.class), false).isCanceled())
            .forEach(t -> t.getWorld().neighborChanged(t.getTo(), ConduitRegistry.getConduitModObjectNN().getBlockNN(), t.getFrom()));
      }
    }

    /**
     * Note: This can be used in <code>.reduce(false, this::anyMatchNoShortCircuit)</code> to have an <code>.anyMatch()</code> that doesn't short-circuit. This
     * is needed if all elements of the stream need to be processed because there are side effects in the stream.
     */
    private boolean anyMatchNoShortCircuit(Boolean a, Boolean b) {
      return a || b;
    }

  }

}
