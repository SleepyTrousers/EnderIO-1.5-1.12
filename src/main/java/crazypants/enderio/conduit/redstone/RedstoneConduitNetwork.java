package crazypants.enderio.conduit.redstone;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.IConduitBundle;

public class RedstoneConduitNetwork extends AbstractConduitNetwork<IRedstoneConduit, IRedstoneConduit> {

  private final Set<Signal> signals = new HashSet<Signal>();

  boolean updatingNetwork = false;

  private boolean networkEnabled = true;

  public RedstoneConduitNetwork() {
    super(IRedstoneConduit.class, IRedstoneConduit.class);
  }

  @Override
  public void init(IConduitBundle tile, Collection<IRedstoneConduit> connections, World world) {
    super.init(tile, connections, world);
    updatingNetwork = true;
    notifyNeigborsOfSignals();
    updatingNetwork = false;
  }

  @Override
  public void destroyNetwork() {
    updatingNetwork = true;
    for (IRedstoneConduit con : conduits) {
      con.setActive(false);
    }
    // Notify neighbours that all signals have been lost
    List<Signal> copy = new ArrayList<Signal>(signals);
    signals.clear();
    for (Signal s : copy) {
      notifyNeigborsOfSignalUpdate(s);
    }
    updatingNetwork = false;
    super.destroyNetwork();
  }

  @Override
  public void addConduit(IRedstoneConduit con) {
    updatingNetwork = true;
    super.addConduit(con);
    Set<Signal> newInputs = con.getNetworkInputs();
    signals.addAll(newInputs);
    // Notify existing nodes of new signals
    for (Signal signal : newInputs) {
      notifyNeigborsOfSignalUpdate(signal);
    }
    // and new nodes neighbours of all signals
    for (Signal signal : signals) {
      notifyConduitNeighbours(con, signal);
    }
    updatingNetwork = false;
  }

  public Set<Signal> getSignals() {
    if(networkEnabled) {
      return signals;
    } else {
      return Collections.emptySet();
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

  public void addSignals(Set<Signal> newSignals) {
    for (Signal signal : newSignals) {
      addSignal(signal);
    }
  }

  public void addSignal(Signal signal) {
    updatingNetwork = true;
    signals.add(signal);
    notifyNetworkOfUpdate();
    notifyNeigborsOfSignalUpdate(signal);
    updatingNetwork = false;
  }

  public void removeSignals(Set<Signal> remove) {
    for (Signal signal : remove) {
      removeSignal(signal);
    }
  }

  public void removeSignal(Signal signal) {
    updatingNetwork = true;
    signals.remove(signal);
    notifyNetworkOfUpdate();
    notifyNeigborsOfSignalUpdate(signal);
    updatingNetwork = false;
  }

  public void replaceSignal(Signal oldSig, Signal newSig) {
    updatingNetwork = true;
    signals.remove(oldSig);
    signals.add(newSig);
    notifyNetworkOfUpdate();
    notifyNeigborsOfSignalUpdate(newSig);
    updatingNetwork = false;
  }

  @Override
  public void notifyNetworkOfUpdate() {
    for (IRedstoneConduit con : conduits) {
      con.setActive(false);
      for (Signal s : getSignals()) {
        if (s.strength > 0) {
          con.setActive(true);
          break;
        }
      }
    }
    super.notifyNetworkOfUpdate();
  }

  @Override
  public String toString() {
    return "RedstoneConduitNetwork [signals=" + signalsString() + ", conduits=" + conduitsString() + "]";
  }

  private String conduitsString() {
    StringBuilder sb = new StringBuilder();
    for (IRedstoneConduit con : conduits) {
      TileEntity te = con.getBundle().getEntity();
      sb.append("<").append(te.xCoord).append(",").append(te.yCoord).append(",").append(te.zCoord).append(">");
    }
    return sb.toString();
  }

  String signalsString() {
    StringBuilder sb = new StringBuilder();
    for (Signal s : signals) {
      sb.append("<");
      sb.append(s);
      sb.append(">");

    }
    return sb.toString();
  }

  public void notifyNeigborsOfSignals() {
    for (Signal signal : signals) {
      notifyNeigborsOfSignalUpdate(signal);
    }
  }

  public void notifyNeigborsOfSignalUpdate(Signal signal) {
    ArrayList<IRedstoneConduit> conduitsCopy = new ArrayList<IRedstoneConduit>(conduits);
    for (IRedstoneConduit con : conduitsCopy) {
      notifyConduitNeighbours(con, signal);
    }
  }

  private void notifyConduitNeighbours(IRedstoneConduit con, Signal signal) {
    if(con.getBundle() == null) {
      System.out.println("RedstoneConduitNetwork.notifyNeigborsOfSignalUpdate: NULL BUNDLE!!!!");
      return;
    }
    TileEntity te = con.getBundle().getEntity();

    World worldObj = te.getWorldObj();
    
    if (!worldObj.blockExists(te.xCoord, te.yCoord, te.zCoord)) {
      return;
    }
    
    worldObj.notifyBlocksOfNeighborChange(te.xCoord, te.yCoord, te.zCoord, EnderIO.blockConduitBundle);

    // Need to notify neighbours neighbours for changes to  signals
    if(signal != null /*&& signal.strength >= 15 && signal.x == te.xCoord && signal.y == te.yCoord && signal.z == te.zCoord*/) {
      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        BlockCoord bc = new BlockCoord(te).getLocation(dir);
        if (worldObj.blockExists(bc.x, bc.y, bc.z) && bc.getBlock(worldObj).isNormalCube()) {
          worldObj.notifyBlocksOfNeighborChange(bc.x, bc.y, bc.z, EnderIO.blockConduitBundle);
        }
      }
    }
  }

}
