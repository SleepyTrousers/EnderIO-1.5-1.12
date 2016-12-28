package crazypants.enderio.conduit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import crazypants.enderio.Log;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

//I=base type, I is the base class of the implementations accepted by the network 
public abstract class AbstractConduitNetwork<T extends IConduit, I extends T> {

  protected final List<I> conduits = new ArrayList<I>();

  protected final Class<I> implClass;
  protected final Class<T> baseConduitClass;

  protected AbstractConduitNetwork(Class<I> implClass, Class<T> baseConduitClass) {
    this.implClass = implClass;
    this.baseConduitClass = baseConduitClass;
  }

  public void init(IConduitBundle tile, Collection<I> connections, World world) {

    if(world.isRemote) {
      throw new UnsupportedOperationException();
    }

    // Destroy all existing redstone networks around this block
    for (I con : connections) {
      AbstractConduitNetwork<?, ?> network = con.getNetwork();
      if(network != null) {
        network.destroyNetwork();
      }
    }
    setNetwork(world, tile);
    sendBlockUpdatesForEntireNetwork();
  }

  public final Class<T> getBaseConduitType() {
    return baseConduitClass;
  }

  protected void setNetwork(World world, IConduitBundle tile) {

    T conduit = tile.getConduit(getBaseConduitType());

    if(conduit != null && implClass.isAssignableFrom(conduit.getClass()) && conduit.setNetwork(this)) {
      addConduit(implClass.cast(conduit));
      TileEntity te = tile.getEntity();
      Collection<T> connections = ConduitUtil.getConnectedConduits(world, te.getPos(), getBaseConduitType());
      for (T con : connections) {
        if(con.getNetwork() == null) {
          setNetwork(world, con.getBundle());
        } else if(con.getNetwork() != this) {
          con.getNetwork().destroyNetwork();
          setNetwork(world, con.getBundle());
        }
      }
    }
  }

  public void addConduit(I con) {
    if(!conduits.contains(con)) {
      if(conduits.isEmpty()) {
        ConduitNetworkTickHandler.instance.registerNetwork(this);
      }
      conduits.add(con);
    }
  }

  public void destroyNetwork() {
    for (I con : conduits) {
      con.setNetwork(null);
    }
    conduits.clear();
    ConduitNetworkTickHandler.instance.unregisterNetwork(this);
  }

  public List<I> getConduits() {
    return conduits;
  }

  public void sendBlockUpdatesForEntireNetwork() {
    long[] times = new long[conduits.size()];
    try {
      for (int i = 0; i < conduits.size(); i++) {
        times[i] = System.nanoTime();
        I con = conduits.get(i);
      TileEntity te = con.getBundle().getEntity();
      if (te.getWorld().isBlockLoaded(te.getPos())) {
        IBlockState bs = te.getWorld().getBlockState(te.getPos());
        te.getWorld().notifyBlockUpdate(te.getPos(), bs, bs, 3);
        te.getWorld().notifyNeighborsOfStateChange(te.getPos(), te.getBlockType());
      }
    }
    } catch (Error e) { // Watchdog?
      try {
        diagnostics(times);
      } catch (Throwable t) {
        // prefer to throw the original error
      }
      throw e;
    }
  }

  private void diagnostics(long[] times) {
    long end = System.nanoTime();
    Log.error("Conduit network " + this.getClass() + " interrupted while notifying neighbors of changes");
    for (int i = times.length - 1; i >= 0; i--) {
      if (times[i] != 0) {
        long tmp = times[i];
        times[i] = end - times[i];
        end = tmp;
      }
    }
    for (int i = 0; i < conduits.size(); i++) {
      if (times[i] != 0) {
        I con = conduits.get(i);
        TileEntity te = con.getBundle().getEntity();
        Log.error("Updating neigbors at " + te.getPos() + " took " + times[i] + "ns");
      }
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (IConduit con : conduits) {
      sb.append(con.getLocation());
      sb.append(", ");
    }
    return "AbstractConduitNetwork [conduits=" + sb.toString() + "]";
  }

  public void doNetworkTick() {
  }
}
