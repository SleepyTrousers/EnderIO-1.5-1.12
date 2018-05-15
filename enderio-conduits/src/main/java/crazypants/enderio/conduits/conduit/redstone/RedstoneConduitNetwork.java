package crazypants.enderio.conduits.conduit.redstone;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.DyeColor;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.base.conduit.redstone.signals.BundledSignal;
import crazypants.enderio.base.conduit.redstone.signals.Signal;
import crazypants.enderio.base.conduit.registry.ConduitRegistry;
import crazypants.enderio.base.filter.redstone.IInputSignalFilter;
import crazypants.enderio.conduits.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduits.config.ConduitConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class RedstoneConduitNetwork extends AbstractConduitNetwork<IRedstoneConduit, IRedstoneConduit> {

  private final BundledSignal bundledSignal = new BundledSignal();

  boolean updatingNetwork = false;

  private boolean networkEnabled = true;

  private boolean endTickUpdate = false;

  public RedstoneConduitNetwork() {
    super(IRedstoneConduit.class, IRedstoneConduit.class);
  }

  @Override
  public void init(@Nonnull IConduitBundle tile, Collection<IRedstoneConduit> connections, @Nonnull World world) {
    super.init(tile, connections, world);
    updatingNetwork = true;
    notifyNeigborsOfSignalUpdate();
    updatingNetwork = false;
  }

  @Override
  public void destroyNetwork() {
    updatingNetwork = true;
    for (IRedstoneConduit con : getConduits()) {
      con.setActive(false);
    }
    // Notify neighbours that all signals have been lost
    bundledSignal.clear();
    notifyNeigborsOfSignalUpdate();
    updatingNetwork = false;
    super.destroyNetwork();
  }

  @Override
  public void addConduit(@Nonnull IRedstoneConduit con) {
    super.addConduit(con);
    updateInputsFromConduit(con, true); // all call paths to here come from updateNetwork() which already notifies all neighbors
  }

  public void updateInputsFromConduit(@Nonnull IRedstoneConduit con, boolean delayUpdate) {
    // Make my neighbors update as if we have no signals
    updatingNetwork = true;
    notifyConduitNeighbours(con);
    updatingNetwork = false;

    // Then ask them what inputs they have now
    Set<EnumFacing> externalConnections = con.getExternalConnections();
    for (EnumFacing side : EnumFacing.values()) {
      if (externalConnections.contains(side)) {
        updateInputsForSource(con, side);
      }
    }

    if (!delayUpdate) {
      // then tell the whole network about the change
      notifyNeigborsOfSignalUpdate();
    }

    if (ConduitConfig.showState.get()) {
      updateActiveState();
    }
  }

  private void updateActiveState() {
    boolean isActive = false;
    for (Signal s : bundledSignal.getSignals()) {
      if (s.getStrength() > 0) {
        isActive = true;
        break;
      }
    }
    for (IRedstoneConduit con : getConduits()) {
      con.setActive(isActive);
    }
  }

  private void updateInputsForSource(@Nonnull IRedstoneConduit con, @Nonnull EnumFacing dir) {
    updatingNetwork = true;
    Signal oldSig = con.getExternalSignalForDir(dir);
    Signal signal = con.getNetworkInput(dir);
    if (oldSig != null && oldSig.getStrength() != signal.getStrength() || endTickUpdate) {
      bundledSignal.remove(con.getInputSignalColor(dir), oldSig.getStrength());
    }
    bundledSignal.add(con.getInputSignalColor(dir), signal.getStrength());

    if (Loader.isModLoaded("computercraft")) {
      Map<DyeColor, Signal> ccSignals = con.getComputerCraftSignals(dir);

      if (!ccSignals.isEmpty()) {
        for (DyeColor color : ccSignals.keySet()) {
          Signal oldSigB = bundledSignal.getSignal(color);
          Signal ccSig = ccSignals.get(color);

          if (oldSigB.getStrength() != ccSig.getStrength()) {
            bundledSignal.remove(color, oldSigB.getStrength());
          }
          bundledSignal.add(color, ccSig.getStrength());
        }
      }
    }

    con.setExternalSignalForDir(dir, signal);
    updatingNetwork = false;

  }

  public BundledSignal getBundledSignal() {
    return bundledSignal;
  }

  // Need to disable the network when determining the strength of external
  // signals
  // to avoid feed back looops
  void setNetworkEnabled(boolean enabled) {
    networkEnabled = enabled;
  }

  public boolean isNetworkEnabled() {
    return networkEnabled;
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
    for (Signal s : bundledSignal.getSignals()) {
      sb.append("<");
      sb.append(s);
      sb.append(">");

    }
    return sb.toString();
  }

  public void notifyNeigborsOfSignalUpdate() {
    ArrayList<IRedstoneConduit> conduitsCopy = new ArrayList<IRedstoneConduit>(getConduits());
    for (IRedstoneConduit con : conduitsCopy) {
      notifyConduitNeighbours(con);
    }
  }

  private void notifyConduitNeighbours(@Nonnull IRedstoneConduit con) {
    TileEntity te = con.getBundle().getEntity();

    World world = te.getWorld();

    BlockPos bc1 = te.getPos();

    if (!world.isBlockLoaded(bc1)) {
      return;
    }

    // Done manually to avoid orphaning chunks
    EnumSet<EnumFacing> cons = EnumSet.copyOf(con.getExternalConnections());
    if (!neighborNotifyEvent(world, bc1, null, cons)) {
      for (EnumFacing dir : con.getExternalConnections()) {
        BlockPos bc2 = bc1.offset(NullHelper.notnull(dir, "Conduit external connections contains null"));
        if (world.isBlockLoaded(bc2)) {
          world.neighborChanged(bc2, ConduitRegistry.getConduitModObjectNN().getBlockNN(), bc1);
          IBlockState bs = world.getBlockState(bc2);
          if (bs.isBlockNormalCube() && !neighborNotifyEvent(world, bc2, bs, EnumSet.allOf(EnumFacing.class))) {
            for (NNIterator<EnumFacing> itr = NNList.FACING.fastIterator(); itr.hasNext();) {
              EnumFacing dir2 = itr.next();
              BlockPos bc3 = bc2.offset(dir2);
              if (!bc3.equals(bc1) && world.isBlockLoaded(bc3)) {
                world.neighborChanged(bc3, ConduitRegistry.getConduitModObjectNN().getBlockNN(), bc1);
              }
            }
          }
        }
      }
    }
  }

  private boolean neighborNotifyEvent(World world, @Nonnull BlockPos pos, @Nullable IBlockState state, EnumSet<EnumFacing> dirs) {
    return ForgeEventFactory.onNeighborNotify(world, pos, state == null ? world.getBlockState(pos) : state, dirs, false).isCanceled();
  }

  /**
   * This is a bit of a hack...avoids the network searching for inputs from unloaded chunks by only filtering out the invalid signals from the unloaded chunk.
   * 
   * @param conduits
   * @param oldSignals
   */
  public void afterChunkUnload(@Nonnull List<IRedstoneConduit> conduits, @Nonnull BundledSignal oldSignals) {
    // World world = null;
    // for (IRedstoneConduit c : conduits) {
    // if (world == null) {
    // world = c.getBundle().getBundleworld();
    // }
    // BlockPos pos = c.getBundle().getLocation();
    // if (world.isBlockLoaded(pos)) {
    // this.getConduits().add(c);
    // c.setNetwork(this);
    // }
    // }
    //
    // bundledSignal.clear();
    // boolean signalsChanged = false;
    // for (Entry<SignalSource, Signal> s : oldSignals.entries()) {
    // if (world != null && world.isBlockLoaded(s.getKey().getSource())) {
    // signals.put(s.getKey(), s.getValue());
    // } else {
    // signalsChanged = true;
    // }
    // }
    // if (signalsChanged) {
    // // broadcast out a change
    // notifyNeigborsOfSignalUpdate();
    // }
  }

  public int getSignalStrengthForColor(@Nonnull DyeColor color) {
    return bundledSignal.getSignal(color).getStrength();
  }

  @Override
  public void tickEnd(ServerTickEvent event, @Nullable Profiler profiler) {
    super.tickEnd(event, profiler);

    endTickUpdate = true;

    for (IRedstoneConduit con : getConduits()) {
      for (EnumFacing dir : EnumFacing.VALUES) {
        if (((IInputSignalFilter) con.getSignalFilter(dir, false)).shouldUpdate()) {
          updateInputsForSource(con, dir);
          break;
        }
      }
    }

    endTickUpdate = false;
  }

}
