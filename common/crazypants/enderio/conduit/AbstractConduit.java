package crazypants.enderio.conduit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.geom.CollidableCache;
import crazypants.enderio.conduit.geom.CollidableCache.CacheKey;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.geom.ConduitGeometryUtil;
import crazypants.util.BlockCoord;

public abstract class AbstractConduit implements IConduit {

  protected final Set<ForgeDirection> conduitConnections = new HashSet<ForgeDirection>();

  protected final Set<ForgeDirection> externalConnections = new HashSet<ForgeDirection>();

  public static final float STUB_WIDTH = 0.2f;

  public static final float STUB_HEIGHT = 0.2f;

  public static final float TRANSMISSION_SCALE = 0.3f;

  // NB: This is a transient field controlled by the owning bundle. It is not
  // written to the NBT etc
  protected IConduitBundle bundle;

  protected boolean active;

  protected List<CollidableComponent> collidables;

  protected final EnumMap<ForgeDirection, ConnectionMode> conectionModes = new EnumMap<ForgeDirection, ConnectionMode>(ForgeDirection.class);

  protected boolean collidablesDirty = true;

  private boolean clientStateDirty = true;

  private boolean dodgyChangeSinceLastCallFlagForBundle = true;

  private int lastNumConections = -1;

  private boolean updateConnections = true;

  protected AbstractConduit() {
  }

  @Override
  public ConnectionMode getConectionMode(ForgeDirection dir) {
    ConnectionMode res = conectionModes.get(dir);
    if(res == null) {
      return getDefaultConnectionMode();
    }
    return res;
  }

  protected ConnectionMode getDefaultConnectionMode() {
    return ConnectionMode.IN_OUT;
  }

  @Override
  public void setConnectionMode(ForgeDirection dir, ConnectionMode mode) {
    ConnectionMode oldVal = conectionModes.get(dir);
    if(oldVal == mode) {
      return;
    }
    if(mode == null) {
      conectionModes.remove(dir);
    } else {
      conectionModes.put(dir, mode);
    }
    clientStateDirty = true;
    collidablesDirty = true;
  }

  @Override
  public boolean hasConnectionMode(ConnectionMode mode) {
    for (ConnectionMode cm : conectionModes.values()) {
      if(cm == mode) {
        return true;
      }
    }
    return false;
  }

  @Override
  public ConnectionMode getNextConnectionMode(ForgeDirection dir) {
    ConnectionMode curMode = getConectionMode(dir);
    ConnectionMode next = ConnectionMode.getNext(curMode);
    if(next == ConnectionMode.NOT_SET) {
      next = ConnectionMode.IN_OUT;
    }
    return next;
  }

  @Override
  public ConnectionMode getPreviousConnectionMode(ForgeDirection dir) {
    ConnectionMode curMode = getConectionMode(dir);
    ConnectionMode prev = ConnectionMode.getPrevious(curMode);
    if(prev == ConnectionMode.NOT_SET) {
      prev = ConnectionMode.DISABLED;
    }
    return prev;
  }

  @Override
  public boolean haveCollidablesChangedSinceLastCall() {
    if(dodgyChangeSinceLastCallFlagForBundle) {
      dodgyChangeSinceLastCallFlagForBundle = false;
      return true;
    }
    return false;
  }

  @Override
  public BlockCoord getLocation() {
    if(bundle == null) {
      return null;
    }
    TileEntity te = bundle.getEntity();
    if(te == null) {
      return null;
    }
    return new BlockCoord(te.xCoord, te.yCoord, te.zCoord);
  }

  @Override
  public void setBundle(IConduitBundle tileConduitBundle) {
    bundle = tileConduitBundle;
  }

  @Override
  public IConduitBundle getBundle() {
    return bundle;
  }

  // Connections
  @Override
  public Set<ForgeDirection> getConduitConnections() {
    return conduitConnections;
  }

  @Override
  public boolean containsConduitConnection(ForgeDirection dir) {
    return conduitConnections.contains(dir);
  }

  @Override
  public void conduitConnectionAdded(ForgeDirection fromDirection) {
    conduitConnections.add(fromDirection);
    connectionsChanged();
  }

  @Override
  public void conduitConnectionRemoved(ForgeDirection fromDirection) {
    conduitConnections.remove(fromDirection);
    connectionsChanged();
  }

  @Override
  public boolean canConnectToConduit(ForgeDirection direction, IConduit conduit) {
    if(conduit == null) {
      return false;
    }
    return getConectionMode(direction) != ConnectionMode.DISABLED && conduit.getConectionMode(direction.getOpposite()) != ConnectionMode.DISABLED;
  }

  @Override
  public boolean canConnectToExternal(ForgeDirection direction, boolean ignoreConnectionMode) {
    return false;
  }

  @Override
  public Set<ForgeDirection> getExternalConnections() {
    return externalConnections;
  }

  @Override
  public boolean hasExternalConnections() {
    return !externalConnections.isEmpty();
  }

  @Override
  public boolean hasConnections() {
    return hasConduitConnections() || hasExternalConnections();
  }

  @Override
  public boolean hasConduitConnections() {
    return !conduitConnections.isEmpty();
  }

  @Override
  public boolean containsExternalConnection(ForgeDirection dir) {
    return externalConnections.contains(dir);
  }

  @Override
  public void externalConnectionAdded(ForgeDirection fromDirection) {
    externalConnections.add(fromDirection);
    connectionsChanged();
  }

  @Override
  public void externalConnectionRemoved(ForgeDirection fromDirection) {
    externalConnections.remove(fromDirection);
    conectionModes.remove(fromDirection);
    connectionsChanged();
  }

  @Override
  public boolean isConnectedTo(ForgeDirection dir) {
    return containsConduitConnection(dir) || containsExternalConnection(dir);
  }

  @Override
  public boolean isActive() {
    return active;
  }

  @Override
  public void setActive(boolean active) {
    if(active != this.active) {
      clientStateDirty = true;
    }
    this.active = active;
  }

  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {
    int[] dirs = new int[conduitConnections.size()];
    Iterator<ForgeDirection> cons = conduitConnections.iterator();
    for (int i = 0; i < dirs.length; i++) {
      dirs[i] = cons.next().ordinal();
    }
    nbtRoot.setIntArray("connections", dirs);

    dirs = new int[externalConnections.size()];
    cons = externalConnections.iterator();
    for (int i = 0; i < dirs.length; i++) {
      dirs[i] = cons.next().ordinal();
    }
    nbtRoot.setIntArray("externalConnections", dirs);
    nbtRoot.setBoolean("signalActive", active);

    if(conectionModes.size() > 0) {
      byte[] modes = new byte[6];
      int i = 0;
      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        modes[i] = (byte) getConectionMode(dir).ordinal();
        i++;
      }
      nbtRoot.setByteArray("conModes", modes);
    }
  }

  @Override
  public void readFromNBT(NBTTagCompound nbtRoot) {
    conduitConnections.clear();
    int[] dirs = nbtRoot.getIntArray("connections");
    for (int i = 0; i < dirs.length; i++) {
      conduitConnections.add(ForgeDirection.values()[dirs[i]]);
    }

    externalConnections.clear();
    dirs = nbtRoot.getIntArray("externalConnections");
    for (int i = 0; i < dirs.length; i++) {
      externalConnections.add(ForgeDirection.values()[dirs[i]]);
    }
    active = nbtRoot.getBoolean("signalActive");

    conectionModes.clear();
    byte[] modes = nbtRoot.getByteArray("conModes");
    if(modes != null && modes.length == 6) {
      int i = 0;
      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        conectionModes.put(dir, ConnectionMode.values()[modes[i]]);
        i++;
      }
    }
  }

  @Override
  public int getLightValue() {
    return 0;
  }

  @Override
  public boolean onBlockActivated(EntityPlayer player, RaytraceResult res, List<RaytraceResult> all) {
    return false;
  }

  @Override
  public float getSelfIlluminationForState(CollidableComponent component) {
    return isActive() ? 1 : 0;
  }

  @Override
  public float getTransmitionGeometryScale() {
    return TRANSMISSION_SCALE;
  }

  @Override
  public void onChunkUnload(World worldObj) {
    AbstractConduitNetwork<?, ?> network = getNetwork();
    if(network != null) {
      network.destroyNetwork();
    }
  }

  @Override
  public void updateEntity(World world) {
    if(world.isRemote) {
      return;
    }
    updateNetwork(world);
    updateConnections();
    if(clientStateDirty && getBundle() != null) {
      getBundle().dirty();
      clientStateDirty = false;
    }
  }

  private void updateConnections() {
    if(!updateConnections) {
      return;
    }

    boolean externalConnectionsChanged = false;
    List<ForgeDirection> copy = new ArrayList<ForgeDirection>(externalConnections);
    // remove any no longer valid connections
    for (ForgeDirection dir : copy) {
      if(!canConnectToExternal(dir, false)) {
        externalConnectionRemoved(dir);
        externalConnectionsChanged = true;
      }
    }

    // then check for new ones
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      if(!conduitConnections.contains(dir) && !externalConnections.contains(dir)) {
        if(canConnectToExternal(dir, false)) {
          externalConnectionAdded(dir);
          externalConnectionsChanged = true;
        }
      }
    }
    if(externalConnectionsChanged) {
      connectionsChanged();
    }

    updateConnections = false;
  }

  protected void connectionsChanged() {
    collidablesDirty = true;
    clientStateDirty = true;
    dodgyChangeSinceLastCallFlagForBundle = true;
  }

  protected void setClientStateDirty() {
    clientStateDirty = true;
  }

  protected void updateNetwork(World world) {
    if(getNetwork() == null) {
      ConduitUtil.ensureValidNetwork(this);
      if(getNetwork() != null && !world.isRemote && bundle != null) {
        world.notifyBlocksOfNeighborChange(bundle.getEntity().xCoord, bundle.getEntity().yCoord, bundle.getEntity().zCoord,
            bundle.getEntity().getBlockType().blockID);
      }
    }
    if(getNetwork() != null) {
      getNetwork().onUpdateEntity(this);
    }
  }

  @Override
  public void onAddedToBundle() {

    TileEntity te = bundle.getEntity();
    World world = te.worldObj;

    conduitConnections.clear();
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      IConduit neighbour = ConduitUtil.getConduit(world, te, dir, getBaseConduitType());
      if(neighbour != null && neighbour.canConnectToConduit(dir.getOpposite(), this)) {
        conduitConnections.add(dir);
        neighbour.conduitConnectionAdded(dir.getOpposite());
      }
    }

    externalConnections.clear();
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      if(!containsConduitConnection(dir) && canConnectToExternal(dir, false)) {
        externalConnectionAdded(dir);
      }
    }

    connectionsChanged();
  }

  @Override
  public void onRemovedFromBundle() {
    TileEntity te = bundle.getEntity();
    World world = te.worldObj;

    for (ForgeDirection dir : conduitConnections) {
      IConduit neighbour = ConduitUtil.getConduit(world, te, dir, getBaseConduitType());
      if(neighbour != null) {
        neighbour.conduitConnectionRemoved(dir.getOpposite());
      }
    }
    conduitConnections.clear();

    if(!externalConnections.isEmpty()) {
      world.notifyBlocksOfNeighborChange(te.xCoord, te.yCoord, te.zCoord, EnderIO.blockConduitBundle.blockID);
    }
    externalConnections.clear();

    AbstractConduitNetwork<?, ?> network = getNetwork();
    if(network != null) {
      network.destroyNetwork();
    }
    connectionsChanged();
  }

  @Override
  public boolean onNeighborBlockChange(int blockId) {
    // Check for changes to external connections, connections to conduits are
    // handled by the bundle

    // NB: No need to check externals if the neighbour that changed was a
    // conduit bundle as this
    // can't effect external connections.
    if(blockId == EnderIO.blockConduitBundle.blockID) {
      return false;
    }

    updateConnections = true;

    return true;
  }

  @Override
  public Collection<CollidableComponent> createCollidables(CacheKey key) {
    return Collections.singletonList(new CollidableComponent(getCollidableType(), ConduitGeometryUtil.instance.getBoundingBox(getBaseConduitType(), key.dir,
        key.isStub, key.offset), key.dir, null));
  }

  @Override
  public Class<? extends IConduit> getCollidableType() {
    return getBaseConduitType();
  }

  @Override
  public List<CollidableComponent> getCollidableComponents() {

    if(collidables != null && !collidablesDirty) {
      return collidables;
    }

    List<CollidableComponent> result = new ArrayList<CollidableComponent>();
    CollidableCache cc = CollidableCache.instance;

    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      Collection<CollidableComponent> col = getCollidables(dir);
      if(col != null) {
        result.addAll(col);
      }
    }
    collidables = result;

    collidablesDirty = false;

    return result;
  }

  protected boolean renderStub(ForgeDirection dir) {
    return getConectionMode(dir) == ConnectionMode.DISABLED;
  }

  private Collection<CollidableComponent> getCollidables(ForgeDirection dir) {
    CollidableCache cc = CollidableCache.instance;
    Class<? extends IConduit> type = getCollidableType();
    if(isConnectedTo(dir)) {
      return cc.getCollidables(cc.createKey(type, getBundle().getOffset(getBaseConduitType(), dir), dir, renderStub(dir)), this);
    }
    return null;
  }

}
