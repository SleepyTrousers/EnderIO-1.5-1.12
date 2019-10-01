package crazypants.enderio.base.conduit.redstone.rsnew;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

import com.enderio.core.common.util.NNList;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.math.BlockPos;

public interface IRedstoneConduitNetwork {

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
   * @return <code>null</code> if there's no signal on that channel, a signal level otherwise
   */
  Integer getSignalLevel(@Nonnull EnumDyeColor channel);

  class Temp implements IRedstoneConduitNetwork {

    private final @Nonnull Map<UID, ISignal> signals = new HashMap<>();
    private final @Nonnull Map<UID, ISignal> tickingSignals = new HashMap<>();
    private final @Nonnull Map<UID, IOutput> outputs = new HashMap<>();
    private boolean signalsDirty = false;

    private final @Nonnull EnumMap<EnumDyeColor, Integer> levels = new EnumMap<>(EnumDyeColor.class);
    private final @Nonnull Set<EnumDyeColor> changed = new HashSet<>();

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
    public Integer getSignalLevel(@Nonnull EnumDyeColor channel) {
      return levels.get(channel);
    }

    public void tick() {
      // 1 send output
      if (!changed.isEmpty()) {
        Set<BlockPos> collect = outputs.values().stream().flatMap(output -> output.getNotificationTargets(this, changed).stream()).collect(Collectors.toSet());
        // TODO notify each pos in collect
      }
      // 2 tick
      boolean tickChanges = tickingSignals.values().stream().map(signal -> signal.tick(this, !changed.isEmpty())).reduce(false, (a, b) -> a || b);
      changed.clear();
      // 2 acquire input
      if (signalsDirty || tickChanges) {

        final NNList<EnumDyeColor> CHANNELS = NNList.of(EnumDyeColor.class); // -> static field
        CHANNELS.apply(channel -> {
          int value = 0;
          for (ISignal signal : signals.values()) {
            value = Math.max(value, signal.get3(channel));
          }
          if (levels.get(channel) != value) {
            changed.add(channel);
            levels.put(channel, value);
          }
        });

        CHANNELS.apply(channel -> {
          Integer value = null;
          for (ISignal signal : signals.values()) {
            Integer v = signal.get(channel);
            if (v != null && (value == null || v > value)) {
              value = v;
            }
          }
          if (levels.get(channel) != value) {
            changed.add(channel);
            levels.put(channel, value);
          }
        });

        if (signals.values().stream().filter(signal -> signal.isDirty()).map(signal -> signal.acquire(this)).reduce(false, (a, b) -> a || b) || tickChanges) {
          CHANNELS.apply(channel -> {
            Integer value = signals.values().stream().map(signal -> signal.get2(channel)).filter(v -> v.isPresent())
                .reduce(Optional.empty(), (a, b) -> a.orElse(-1) > b.orElse(-1) ? a : b).orElse(null);
            if (levels.get(channel) != value) {
              changed.add(channel);
            }
            levels.put(channel, value);
          });

          changed.addAll(Arrays.stream(EnumDyeColor.values())
              .map(channel -> Pair.of(channel,
                  signals.values().stream().map(signal -> channel != null ? signal.get2(channel) : Optional.<Integer> empty()).filter(v -> v.isPresent())
                      .reduce(Optional.empty(), (a, b) -> a.orElse(-1) > b.orElse(-1) ? a : b).orElse(null)))
              .filter(pair -> levels.get(pair.getKey()) != pair.getValue()).peek(pair -> levels.put(pair.getKey(), pair.getValue())).map(pair -> pair.getKey())
              .collect(Collectors.toSet()));

          // changed.addAll(Arrays.stream(EnumDyeColor.values())
          // .map(channel -> Pair.of(channel,
          // signals.values().stream().map(signal -> channel != null ? signal.get(channel) : Optional.<Integer> empty()).filter(v -> v.isPresent())
          // .reduce(Optional.empty(), (a, b) -> a.orElse(-1) > b.orElse(-1) ? a : b)))
          // .filter(pair -> !levels.get(pair.getKey()).equals(pair.getValue())).peek(pair -> levels.put(pair.getKey(), pair.getValue()))
          // .map(pair -> pair.getKey()).collect(Collectors.toSet()));

        }
      }
    }

  }

}
