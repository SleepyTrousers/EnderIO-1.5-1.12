package crazypants.enderio.conduits.conduit.redstone;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.conduit.redstone.rsnew.IOutput;
import crazypants.enderio.base.conduit.redstone.rsnew.ISignal;
import crazypants.enderio.base.conduit.redstone.rsnew.ISignalNetwork;
import crazypants.enderio.base.conduit.redstone.rsnew.UID;
import crazypants.enderio.base.conduit.registry.ConduitRegistry;
import crazypants.enderio.base.diagnostics.Prof;
import crazypants.enderio.conduits.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduits.config.ConduitConfig;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

import static net.minecraftforge.event.ForgeEventFactory.onNeighborNotify;

public class RedstoneConduitNetwork extends AbstractConduitNetwork<IRedstoneConduit, IRedstoneConduit> implements ISignalNetwork {

  boolean updatingNetwork = false;

  public RedstoneConduitNetwork() {
    super(IRedstoneConduit.class, IRedstoneConduit.class);
  }

  @Override
  public void destroyNetwork() {
    for (IRedstoneConduit con : getConduits()) {
      con.setActive(false);
    }
    super.destroyNetwork();
    levels.clear();
    signals.clear();
    tickingSignals.clear();
    outputs.clear();
  }

  @Override
  public void addConduit(@Nonnull IRedstoneConduit con) {
    super.addConduit(con);
    con.onAddedToNetwork();
  }

  private void updateActiveState() {
    boolean isActive = false;
    for (Integer i : levels.values()) {
      if (i != null && i > 0) {
        isActive = true;
        break;
      }
    }
    for (IRedstoneConduit con : getConduits()) {
      con.setActive(isActive);
    }
  }

  /**
   * Conduits need to check this manually because they could have a filter in front of getSignalLevel which could turn any value into an active signal...
   */
  public boolean isUpdatingNetwork() {
    return updatingNetwork;
  }

  @Override
  public String toString() {
    return "RedstoneConduitNetwork [signals=" + signalsString() + ", conduits=" + conduitsString() + "]";
  }

  private String conduitsString() {
    StringBuilder sb = new StringBuilder();
    for (IRedstoneConduit con : getConduits()) {
      TileEntity te = con.getBundle().getEntity();
      sb.append("<").append(te.getPos().getX()).append(",").append(te.getPos().getY()).append(",").append(te.getPos().getZ()).append(">");
    }
    return sb.toString();
  }

  String signalsString() {
    StringBuilder sb = new StringBuilder();
    for (Entry<EnumDyeColor, Integer> e : levels.entrySet()) {
      sb.append("<");
      sb.append(e.getKey());
      sb.append(":");
      sb.append(e.getValue());
      sb.append(">");

    }
    return sb.toString();
  }

  ///////////////////////////////////////////////////////////////////////////////////////////////

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

  @Override
  public void tickEnd(ServerTickEvent event, @Nullable Profiler profiler) {
    super.tickEnd(event, profiler);

    Prof.start(profiler, "input");
    int tickcount = 0;
    Set<EnumDyeColor> changes = new HashSet<>();
    do {
      Set<EnumDyeColor> tickchanges = new HashSet<>();
      if (signalsDirty) {
        try {
          updatingNetwork = true;
          if (signals.values().stream().filter(signal -> signal.isDirty()).map(signal -> signal.acquire(this)).reduce(false, this::anyMatchNoShortCircuit)) {
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
        } finally {
          updatingNetwork = false;
        }
      }
      final boolean firstTick = tickcount == 0;
      signalsDirty = tickingSignals.values().stream().map(signal -> signal.tick(this, tickchanges, firstTick)).reduce(false, this::anyMatchNoShortCircuit);
      changes.addAll(tickchanges);
    } while (signalsDirty && tickcount++ < 4);

    if (ConduitConfig.showState.get()) {
      updateActiveState();
    }

    Prof.next(profiler, "output");
    if (!changes.isEmpty()) {
      outputs.values().stream().flatMap(output -> output.getNotificationTargets(this, changes).stream()).collect(Collectors.toSet()).stream()
          .filter(t -> t.getWorld().isBlockLoaded(t.getFrom()) && t.getWorld().isBlockLoaded(t.getTo())
              && !onNeighborNotify(t.getWorld(), t.getFrom(), t.getWorld().getBlockState(t.getFrom()), EnumSet.allOf(EnumFacing.class), false).isCanceled())
          .forEach(t -> t.getWorld().neighborChanged(t.getTo(), ConduitRegistry.getConduitModObjectNN().getBlockNN(), t.getFrom()));
    }
    Prof.stop(profiler);
  }

  /**
   * Note: This can be used in <code>.reduce(false, this::anyMatchNoShortCircuit)</code> to have an <code>.anyMatch()</code> that doesn't short-circuit. This is
   * needed if all elements of the stream need to be processed because there are side effects in the stream.
   */
  private boolean anyMatchNoShortCircuit(Boolean a, Boolean b) {
    return a || b;
  }

}
