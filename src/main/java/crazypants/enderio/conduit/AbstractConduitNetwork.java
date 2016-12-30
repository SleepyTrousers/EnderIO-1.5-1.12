package crazypants.enderio.conduit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import crazypants.enderio.diagnostics.ConduitNeighborUpdateTracker;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

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

    // Destroy all existing networks around this block
    for (I con : connections) {
      AbstractConduitNetwork<?, ?> network = con.getNetwork();
      if(network != null) {
        network.destroyNetwork();
      }
    }
    setNetwork(world, tile);
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

  private static final EnumFacing[] WEDUNS = new EnumFacing[] { EnumFacing.WEST, EnumFacing.EAST, EnumFacing.DOWN, EnumFacing.UP, EnumFacing.NORTH,
      EnumFacing.SOUTH };

  public void sendBlockUpdatesForEntireNetwork() {
    ConduitNeighborUpdateTracker tracker = null;
    Set<BlockPos> notified = new HashSet<BlockPos>();
    for (I con : conduits) {
      TileEntity te = con.getBundle().getEntity();
      if (con.hasExternalConnections()) {
        final BlockPos pos = te.getPos();
        final Block blockType = te.getBlockType();
        final World world = te.getWorld();
        if (world.isBlockLoaded(pos)) {
          IBlockState bs = world.getBlockState(pos);
          if (tracker == null) {
            tracker = new ConduitNeighborUpdateTracker("Conduit network " + this.getClass() + " was interrupted while notifying neighbors of changes");
          }
          tracker.start("World.notifyBlockUpdate() at " + pos);
          world.notifyBlockUpdate(pos, bs, bs, 3);
          tracker.stop();

          // the following is a fancy version of world.notifyNeighborsOfStateChange(pos, blockType);

          // don't notify other conduits and don't notify the same block twice
          EnumSet<EnumFacing> sidesToNotify = EnumSet.noneOf(EnumFacing.class);
          for (EnumFacing side : WEDUNS) {
            final BlockPos offset = pos.offset(side);
            if (con.containsExternalConnection(side) && !notified.contains(offset) && world.isBlockLoaded(offset)) {
              IBlockState blockState = world.getBlockState(offset);
              if (blockState.getBlock() != blockType && blockState.getBlock() != Blocks.AIR) {
                sidesToNotify.add(side);
                notified.add(offset);
              }
            }
          }

          if (!sidesToNotify.isEmpty()) {
            tracker.start("ForgeEventFactory.onNeighborNotify() at " + pos);
            boolean canceled = ForgeEventFactory.onNeighborNotify(world, pos, bs, sidesToNotify).isCanceled();
            tracker.stop();

            if (!canceled) {
              for (EnumFacing side : WEDUNS) {
                if (sidesToNotify.contains(side)) {
                  final BlockPos offset = pos.offset(side);
                  tracker.start("World.notifyNeighborsOfStateChange() from " + pos + " to " + offset + " (" + world.getBlockState(offset) + ")");
                  world.notifyBlockOfStateChange(offset, blockType);
                  tracker.stop();
                }
              }
            }
          }
        }
      }
    }
    if (tracker != null) {
      tracker.discard();
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (IConduit con : conduits) {
      sb.append(con.getLocation());
      sb.append(", ");
    }
    return "AbstractConduitNetwork@" + Integer.toHexString(hashCode()) + " [conduits=" + sb.toString() + "]";
  }

  public void doNetworkTick() {
  }
}
