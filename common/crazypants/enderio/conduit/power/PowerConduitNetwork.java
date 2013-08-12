package crazypants.enderio.conduit.power;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.util.BlockCoord;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;

public class PowerConduitNetwork extends AbstractConduitNetwork<IPowerConduit> {

  // ----------------------------------------------------------------------

  NetworkPowerManager powerManager;
  

  private Map<BlockCoord, ReceptorEntry> powerReceptors = new HashMap<BlockCoord, ReceptorEntry>();
  
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
    if(powerManager != null) {
      con.setActive(powerManager.isActive());
    }
  }

  @Override
  public Class<? extends IPowerConduit> getBaseConduitType() {
    return IPowerConduit.class;
  }

  public void powerReceptorAdded(IPowerConduit powerConduit, ForgeDirection direction, int x, int y, int z, IPowerReceptor powerReceptor) {
    if(powerReceptor == null) {      
      return;
    }    
    BlockCoord key = new BlockCoord(x, y, z);
    ReceptorEntry re = powerReceptors.get(key);
    if(re == null) {
      re = new ReceptorEntry(powerReceptor, key, powerConduit);
      powerReceptors.put(key, re);
    }
    re.directions.add(direction); 
    if(powerManager != null) {
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
    if(world == null) {
      return;
    }
    if(world.isRemote) {
      return;
    }
    long curTime = world.getTotalWorldTime();    
    if(curTime != timeAtLastApply) {
      timeAtLastApply = curTime;
      powerManager.applyRecievedPower();
    }  
  }
  

  public static class ReceptorEntry {
    
    IPowerConduit emmiter;
    IPowerReceptor powerReceptor;
    BlockCoord coord;
    //The different directions that we connect to the sucker
    Set<ForgeDirection> directions = new HashSet<ForgeDirection>();
    
    public ReceptorEntry(IPowerReceptor powerReceptor, BlockCoord coord,IPowerConduit emmiter) {
      this.powerReceptor = powerReceptor;
      this.coord = coord;      
      this.emmiter = emmiter;
    }
            
  }


}
