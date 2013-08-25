package crazypants.enderio.conduit.power;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.power.IPowerReceptor;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.util.BlockCoord;

public class PowerConduitNetwork extends AbstractConduitNetwork<IPowerConduit> {

  // ----------------------------------------------------------------------

  NetworkPowerManager powerManager;

  private Map<ReceptorKey, ReceptorEntry> powerReceptors = new HashMap<ReceptorKey, ReceptorEntry>();

  private long timeAtLastApply = -1;

  public PowerConduitNetwork() {
  }

  @Override
  public void init(IConduitBundle tile, Collection<IPowerConduit> connections, World world) {
    super.init(tile, connections, world);
    powerManager = new NetworkPowerManager(this, world);
    powerManager.receptorsChanged();
  }

  @Override
  public void destroyNetwork() {
    for (IPowerConduit con : conduits) {
      con.setActive(false);
    }
    powerManager.onNetworkDestroyed();
    super.destroyNetwork();
  }

  public NetworkPowerManager getPowerManager() {
    return powerManager;
  }

  @Override
  public void addConduit(IPowerConduit con) {
    super.addConduit(con);
    Set<ForgeDirection> externalDirs = con.getExternalConnections();
    for (ForgeDirection dir : externalDirs) {
      IPowerReceptor pr = con.getExternalPowerReceptor(dir);
      if (pr != null) {
        TileEntity te = con.getBundle().getEntity();
        powerReceptorAdded(con, dir, te.xCoord + dir.offsetX, te.yCoord + dir.offsetY, te.zCoord + dir.offsetZ, pr);
      }
    }
    if (powerManager != null) {
      con.setActive(powerManager.isActive());
    }
  }

  @Override
  public Class<? extends IPowerConduit> getBaseConduitType() {
    return IPowerConduit.class;
  }

  public void powerReceptorAdded(IPowerConduit powerConduit, ForgeDirection direction, int x, int y, int z, IPowerReceptor powerReceptor) {
    if (powerReceptor == null) {
      return;
    }
    BlockCoord location = new BlockCoord(x, y, z);
    ReceptorKey key = new ReceptorKey(location, direction);
    ReceptorEntry re = powerReceptors.get(key);
    if (re == null) {
      re = new ReceptorEntry(powerReceptor, location, powerConduit, direction);
      powerReceptors.put(key, re);
    }    
    if (powerManager != null) {
      powerManager.receptorsChanged();
    }
  }

  public void powerReceptorRemoved(int x, int y, int z) {
    powerReceptors.remove(new BlockCoord(x, y, z));
    powerManager.receptorsChanged();
  }

  public Collection<ReceptorEntry> getPowerReceptors() {
    return powerReceptors.values();
  }

  @Override
  public void onUpdateEntity(IConduit conduit) {
    World world = conduit.getBundle().getEntity().worldObj;
    if (world == null) {
      return;
    }
    if (world.isRemote) {
      return;
    }
    long curTime = world.getTotalWorldTime();
    if (curTime != timeAtLastApply) {
      timeAtLastApply = curTime;
      powerManager.applyRecievedPower();
    }
  }

  public static class ReceptorEntry {

    IPowerConduit emmiter;
    IPowerReceptor powerReceptor;
    BlockCoord coord;
    ForgeDirection direction;

    public ReceptorEntry(IPowerReceptor powerReceptor, BlockCoord coord, IPowerConduit emmiter, ForgeDirection direction) {
      this.powerReceptor = powerReceptor;
      this.coord = coord;
      this.emmiter = emmiter;
      this.direction = direction;
    }

  }
  
  private static class ReceptorKey {
    BlockCoord coord;
    ForgeDirection direction;
    
    ReceptorKey(BlockCoord coord, ForgeDirection direction) {    
      this.coord = coord;
      this.direction = direction;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((coord == null) ? 0 : coord.hashCode());
      result = prime * result + ((direction == null) ? 0 : direction.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      ReceptorKey other = (ReceptorKey) obj;
      if (coord == null) {
        if (other.coord != null)
          return false;
      } else if (!coord.equals(other.coord))
        return false;
      if (direction != other.direction)
        return false;
      return true;
    }
 
  }

}
