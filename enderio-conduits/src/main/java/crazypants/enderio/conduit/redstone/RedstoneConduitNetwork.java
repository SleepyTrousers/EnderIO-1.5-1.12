package crazypants.enderio.conduit.redstone;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.base.conduit.redstone.signals.Signal;
import crazypants.enderio.base.conduit.redstone.signals.SignalSource;
import crazypants.enderio.base.conduit.registry.ConduitRegistry;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class RedstoneConduitNetwork extends AbstractConduitNetwork<IRedstoneConduit, IRedstoneConduit> {

  private final Multimap<SignalSource, Signal> signals = ArrayListMultimap.create();

  boolean updatingNetwork = false;

  private boolean networkEnabled = true;

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
    signals.clear();
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
    BlockPos pos = con.getBundle().getLocation();

    // Make my neighbors update as if we have no signals
    updatingNetwork = true;
    notifyConduitNeighbours(con);
    updatingNetwork = false;

    //Then ask them what inputs they have now
    Set<EnumFacing> externalConnections = con.getExternalConnections();
    for (EnumFacing side : EnumFacing.values()) {
      if (externalConnections.contains(side)) {
        updateInputsForSource(con, new SignalSource(pos, side));
      } else {
        signals.removeAll(new SignalSource(pos, side));
      }
    }

    if (!delayUpdate) {
      // then tell the whole network about the change
      notifyNeigborsOfSignalUpdate();
    }
    
    if(Config.redstoneConduitsShowState) {
      updateActiveState();
    }
  }
  
  
  private void updateActiveState() {
    boolean isActive = false;
    for (Signal s : getSignals().values()) {
      if (s.getStrength() > 0) {
        isActive = true;
        break;
      }
    }    
    for (IRedstoneConduit con : getConduits()) {
      con.setActive(isActive);      
    }
  }
  
  private void updateInputsForSource(@Nonnull IRedstoneConduit con, @Nonnull SignalSource source) {
    updatingNetwork = true;
    signals.removeAll(source);
    Set<Signal> sigs = con.getNetworkInputs(source.getDir());
    if(sigs != null && !sigs.isEmpty()) {      
      signals.putAll(source, sigs);
    }
    updatingNetwork = false;
  }
  
  

  public Multimap<SignalSource, Signal> getSignals() {
    if (networkEnabled) {
      return signals;
    } else {
      return ArrayListMultimap.create();
    }
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
    for (Signal s : signals.values()) {
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
    if (con.getBundle() == null) {
      return;
    }
    TileEntity te = con.getBundle().getEntity();

    World world = te.getWorld();

    BlockPos bc1 = te.getPos();

    if (!world.isBlockLoaded(bc1)) {
      return;
    }

    // Done manually to avoid orphaning chunks
    for (EnumFacing dir : con.getExternalConnections()) {
      BlockPos bc2 = bc1.offset(dir);
      if (world.isBlockLoaded(bc2)) {
        world.notifyNeighborsOfStateChange(bc2, ConduitRegistry.getConduitModObjectNN().getBlock(), false);
        IBlockState bs = world.getBlockState(bc2);
        if (bs.isBlockNormalCube()) {
          for (EnumFacing dir2 : EnumFacing.VALUES) {
            BlockPos bc3 = bc2.offset(dir2);
            if (!bc3.equals(bc1) && world.isBlockLoaded(bc3)) {
              world.notifyNeighborsOfStateChange(bc3, ConduitRegistry.getConduitModObjectNN().getBlock(), false);
            }
          }
        }
      }
    }
  }

  /**
   * This is a bit of a hack...avoids the network searching for inputs from
   * unloaded chunks by only filtering out the invalid signals from the unloaded
   * chunk.
   * 
   * @param conduits
   * @param oldSignals
   */
  public void afterChunkUnload(@Nonnull List<IRedstoneConduit> conduits, @Nonnull Multimap<SignalSource, Signal> oldSignals) {
    World world = null;
    for (IRedstoneConduit c : conduits) {
      if (world == null) {
        world = c.getBundle().getBundleworld();
      }
      BlockPos pos = c.getBundle().getLocation();
      if (world.isBlockLoaded(pos)) {
        this.getConduits().add(c);
        c.setNetwork(this);
      }
    }
    
    signals.clear();
    boolean signalsChanged = false;
    for (Entry<SignalSource, Signal> s : oldSignals.entries()) {
      if (world != null && world.isBlockLoaded(s.getKey().getSource())) {
        signals.put(s.getKey(), s.getValue());
      } else {
        signalsChanged = true;
      }
    }
    if(signalsChanged) {
      //broadcast out a change 
      notifyNeigborsOfSignalUpdate();
    }
  }

}
