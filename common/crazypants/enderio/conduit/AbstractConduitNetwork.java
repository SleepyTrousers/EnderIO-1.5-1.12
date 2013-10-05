package crazypants.enderio.conduit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class AbstractConduitNetwork<T extends IConduit> {

  protected final List<T> conduits = new ArrayList<T>();

  protected AbstractConduitNetwork() {
  }

  public void init(IConduitBundle tile, Collection<T> connections, World world) {

    if(world.isRemote) {
      throw new UnsupportedOperationException();
    }

    // Destroy all existing redstone networks around this block
    for (T con : connections) {
      AbstractConduitNetwork<?> network = con.getNetwork();
      if(network != null) {
        network.destroyNetwork();
      }
    }
    setNetwork(world, tile);
    notifyNetworkOfUpdate();
  }

  public abstract Class<? extends T> getBaseConduitType();

  protected void setNetwork(World world, IConduitBundle tile) {

    T conduit = tile.getConduit(getBaseConduitType());
    if(conduit.setNetwork(this)) {
      addConduit(conduit);
      TileEntity te = tile.getEntity();
      Collection<? extends T> connections = ConduitUtil.getConnectedConduits(world, te.xCoord, te.yCoord, te.zCoord, getBaseConduitType());
      for (T con : connections) {
        if(con.getNetwork() == null) {
          setNetwork(world, con.getBundle());
        }
      }
    }
  }

  public void addConduit(T con) {
    conduits.add(con);
  }

  public void destroyNetwork() {
    for (T con : conduits) {
      con.setNetwork(null);
    }
    conduits.clear();
  }

  public List<T> getConduits() {
    return conduits;
  }

  public void notifyNetworkOfUpdate() {
    for (T con : conduits) {
      TileEntity te = con.getBundle().getEntity();
      te.worldObj.markBlockForUpdate(te.xCoord, te.yCoord, te.zCoord);
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

  public void onUpdateEntity(IConduit conduit) {
  }

}
