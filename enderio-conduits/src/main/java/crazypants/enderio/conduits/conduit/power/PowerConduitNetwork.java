package crazypants.enderio.conduits.conduit.power;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.conduit.ConduitUtil.UnloadedBlockException;
import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.base.power.IPowerInterface;
import crazypants.enderio.base.power.PowerHandlerUtil;
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

  private final @Nonnull Set<ReceptorEntry> powerReceptors = new HashSet<ReceptorEntry>();

  public PowerConduitNetwork() {
    super(IPowerConduit.class, IPowerConduit.class);
  }

  @Override
  public void init(@Nonnull IConduitBundle tile, Collection<IPowerConduit> connections, @Nonnull World world) throws UnloadedBlockException {
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
      if (dir != null) {
        IPowerInterface pr = con.getExternalPowerReceptor(dir);
        if (pr != null) {
          TileEntity te = con.getBundle().getEntity();
          BlockPos p = te.getPos().offset(dir);
          powerReceptorAdded(con, dir, p);
        }
      }
    }
  }

  public void powerReceptorAdded(@Nonnull IPowerConduit powerConduit, @Nonnull EnumFacing direction, @Nonnull BlockPos pos) {
    powerReceptors.add(new ReceptorEntry(pos, powerConduit, direction));
    if (powerManager != null) {
      powerManager.receptorsChanged();
    }
  }

  public void powerReceptorRemoved(@Nonnull IPowerConduit powerConduit, @Nonnull EnumFacing direction, @Nonnull BlockPos pos) {
    powerReceptors.remove(new ReceptorEntry(pos, powerConduit, direction));
    if (powerManager != null) {
      powerManager.receptorsChanged();
    }
  }

  public Collection<ReceptorEntry> getPowerReceptors() {
    return powerReceptors;
  }

  @Override
  public void tickEnd(ServerTickEvent event, @Nullable Profiler profiler) {
    powerManager.applyRecievedPower(profiler);
  }

  public static class ReceptorEntry {

    final @Nonnull IPowerConduit emmiter;
    final @Nonnull BlockPos pos;
    final @Nonnull EnumFacing direction;

    public ReceptorEntry(@Nonnull BlockPos pos, @Nonnull IPowerConduit emmiter, @Nonnull EnumFacing direction) {
      this.pos = pos;
      this.emmiter = emmiter;
      this.direction = direction;
    }

    @Nullable
    IPowerInterface getPowerInterface() {
      return PowerHandlerUtil.getPowerInterface(emmiter.getBundle().getBundleworld().getTileEntity(pos), direction.getOpposite());
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + pos.hashCode();
      result = prime * result + direction.hashCode();
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
      ReceptorEntry other = (ReceptorEntry) obj;
      if (!pos.equals(other.pos)) {
        return false;
      }
      if (direction != other.direction) {
        return false;
      }
      return true;
    }

  }

}
