package crazypants.enderio.conduits.conduit.power;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.base.power.IPowerInterface;
import crazypants.enderio.conduits.conduit.AbstractConduitNetwork;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class PowerConduitNetwork extends AbstractConduitNetwork<IPowerConduit, IPowerConduit> {

  // ----------------------------------------------------------------------

  NetworkPowerManager powerManager;

  private final Map<ReceptorKey, ReceptorEntry> powerReceptors = new HashMap<ReceptorKey, ReceptorEntry>();

  public PowerConduitNetwork() {
    super(IPowerConduit.class, IPowerConduit.class);
  }

  @Override
  public void init(@Nonnull IConduitBundle tile, Collection<IPowerConduit> connections, @Nonnull World world) {
    super.init(tile, connections, world);
    powerManager = new NetworkPowerManager(this, world);
    powerManager.receptorsChanged();
  }

  @Override
  public void destroyNetwork() {
    for (IPowerConduit con : getConduits()) {
      con.setActive(false);
    }
    if (powerManager != null) {
      powerManager.onNetworkDestroyed();
    }
    super.destroyNetwork();
  }

  public NetworkPowerManager getPowerManager() {
    return powerManager;
  }

  @Override
  public void addConduit(@Nonnull IPowerConduit con) {
    super.addConduit(con);
    Set<EnumFacing> externalDirs = con.getExternalConnections();
    for (EnumFacing dir : externalDirs) {
      IPowerInterface pr = con.getExternalPowerReceptor(dir);
      if (pr != null) {
        TileEntity te = con.getBundle().getEntity();
        BlockPos p = te.getPos().offset(dir);
        powerReceptorAdded(con, dir, p.getX(), p.getY(), p.getZ(), pr);
      }
    }
    if (powerManager != null) {
      con.setActive(powerManager.isActive());
    }
  }

  public void powerReceptorAdded(@Nonnull IPowerConduit powerConduit, @Nonnull EnumFacing direction, int x, int y, int z,
      @Nonnull IPowerInterface powerReceptor) {
    if (powerReceptor == null) {
      return;
    }
    BlockPos pos = new BlockPos(x, y, z);
    ReceptorKey key = new ReceptorKey(pos, direction);
    ReceptorEntry re = powerReceptors.get(key);
    if (re == null) {
      re = new ReceptorEntry(powerReceptor, pos, powerConduit, direction);
      powerReceptors.put(key, re);
    }
    if (powerManager != null) {
      powerManager.receptorsChanged();
    }
  }

  public void powerReceptorRemoved(int x, int y, int z) {
    BlockPos pos = new BlockPos(x, y, z);
    List<ReceptorKey> remove = new ArrayList<ReceptorKey>();
    for (ReceptorKey key : powerReceptors.keySet()) {
      if (key != null && key.pos.equals(pos)) {
        remove.add(key);
      }
    }
    for (ReceptorKey key : remove) {
      powerReceptors.remove(key);
    }
    powerManager.receptorsChanged();
  }

  public Collection<ReceptorEntry> getPowerReceptors() {
    return powerReceptors.values();
  }

  @Override
  public void tickEnd(ServerTickEvent event, Profiler profiler) {
    powerManager.applyRecievedPower(profiler);
  }

  public static class ReceptorEntry {

    IPowerConduit emmiter;
    BlockPos pos;
    EnumFacing direction;

    IPowerInterface powerInterface;

    public ReceptorEntry(@Nonnull IPowerInterface powerReceptor, @Nonnull BlockPos pos, @Nonnull IPowerConduit emmiter, @Nonnull EnumFacing direction) {
      powerInterface = powerReceptor;
      this.pos = pos;
      this.emmiter = emmiter;
      this.direction = direction;
    }

  }

  private static class ReceptorKey {
    BlockPos pos;
    EnumFacing direction;

    ReceptorKey(@Nonnull BlockPos pos, @Nonnull EnumFacing direction) {
      this.pos = pos;
      this.direction = direction;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((pos == null) ? 0 : pos.hashCode());
      result = prime * result + ((direction == null) ? 0 : direction.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      ReceptorKey other = (ReceptorKey) obj;
      if (pos == null) {
        if (other.pos != null) {
          return false;
        }
      } else if (!pos.equals(other.pos)) {
        return false;
      }
      if (direction != other.direction) {
        return false;
      }
      return true;
    }

  }

}
