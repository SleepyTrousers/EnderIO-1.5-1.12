package crazypants.enderio.conduit;

import static crazypants.enderio.base.ModObject.blockConduitBundle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.base.conduit.ConduitUtil;
import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.base.conduit.RaytraceResult;
import crazypants.enderio.base.conduit.geom.CollidableCache;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.base.conduit.geom.ConduitGeometryUtil;
import crazypants.enderio.base.conduit.geom.CollidableCache.CacheKey;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.conduit.render.BlockStateWrapperConduitBundle;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class AbstractConduit implements IConduit {

  protected final Set<EnumFacing> conduitConnections = EnumSet.noneOf(EnumFacing.class);

  protected final Set<EnumFacing> externalConnections = EnumSet.noneOf(EnumFacing.class);

  public static final float STUB_WIDTH = 0.2f;

  public static final float STUB_HEIGHT = 0.2f;

  public static final float TRANSMISSION_SCALE = 0.3f;

  // NB: This is a transient field controlled by the owning bundle. It is not
  // written to the NBT etc
  protected IConduitBundle bundle;

  protected boolean active;

  protected List<CollidableComponent> collidables;

  protected final EnumMap<EnumFacing, ConnectionMode> conectionModes = new EnumMap<EnumFacing, ConnectionMode>(
      EnumFacing.class);

  protected boolean collidablesDirty = true;

  private boolean clientStateDirty = true;

  private boolean dodgyChangeSinceLastCallFlagForBundle = true;

  protected boolean connectionsDirty = true;

  protected boolean readFromNbt = false;

  private Integer lastExternalRedstoneLevel = null;

  protected AbstractConduit() {
  }

  @Override
  public boolean writeConnectionSettingsToNBT(EnumFacing dir, NBTTagCompound nbt) {
    if (!getExternalConnections().contains(dir)) {
      return false;
    }
    NBTTagCompound dataRoot = getNbtRootForType(nbt, true);
    dataRoot.setShort("connectionMode", (short) getConnectionMode(dir).ordinal());
    writeTypeSettingsToNbt(dir, dataRoot);
    return true;
  }

  @Override
  public boolean readConduitSettingsFromNBT(EnumFacing dir, NBTTagCompound nbt) {
    if (!getExternalConnections().contains(dir)) {
      return false;
    }
    NBTTagCompound dataRoot = getNbtRootForType(nbt, false);
    if (dataRoot == null) {
      return false;
    }
    if (dataRoot.hasKey("connectionMode")) {
      ConnectionMode mode = ConnectionMode.values()[dataRoot.getShort("connectionMode")];
      setConnectionMode(dir, mode);
    }
    readTypeSettings(dir, dataRoot);
    return true;
  }

  protected void readTypeSettings(EnumFacing dir, NBTTagCompound dataRoot) {
  }

  protected void writeTypeSettingsToNbt(EnumFacing dir, NBTTagCompound dataRoot) {
  }

  protected NBTTagCompound getNbtRootForType(NBTTagCompound nbt, boolean createIfNull) {
    Class<? extends IConduit> bt = getBaseConduitType();
    String dataRootName = bt.getSimpleName();
    NBTTagCompound dataRoot = null;
    if (nbt.hasKey(dataRootName)) {
      dataRoot = nbt.getCompoundTag(dataRootName);
    }
    if (dataRoot == null && createIfNull) {
      dataRoot = new NBTTagCompound();
      nbt.setTag(dataRootName, dataRoot);
    }
    return dataRoot;
  }

  @Override
  public ConnectionMode getConnectionMode(EnumFacing dir) {
    ConnectionMode res = conectionModes.get(dir);
    if (res == null) {
      return getDefaultConnectionMode();
    }
    return res;
  }

  protected ConnectionMode getDefaultConnectionMode() {
    return ConnectionMode.IN_OUT;
  }

  @Override
  public List<ItemStack> getDrops() {
    return Collections.singletonList(createItem());
  }

  @Override
  public void setConnectionMode(EnumFacing dir, ConnectionMode mode) {
    ConnectionMode oldVal = conectionModes.get(dir);
    if (oldVal == mode) {
      return;
    }    
    if (mode == null) {
      conectionModes.remove(dir);
    } else {
      conectionModes.put(dir, mode);
    }

    connectionsChanged();
  }

  @Override
  public boolean hasConnectionMode(ConnectionMode mode) {
    if (mode == null) {
      return false;
    }
    if (mode == getDefaultConnectionMode() && conectionModes.size() != 6) {
      return true;
    }
    for (ConnectionMode cm : conectionModes.values()) {
      if (cm == mode) {
        return true;
      }
    }
    return false;
  }

  @Override
  public ConnectionMode getNextConnectionMode(EnumFacing dir) {
    ConnectionMode curMode = getConnectionMode(dir);
    ConnectionMode next = ConnectionMode.getNext(curMode);
    if (next == ConnectionMode.NOT_SET) {
      next = ConnectionMode.IN_OUT;
    }
    return next;
  }

  @Override
  public ConnectionMode getPreviousConnectionMode(EnumFacing dir) {
    ConnectionMode curMode = getConnectionMode(dir);
    ConnectionMode prev = ConnectionMode.getPrevious(curMode);
    if (prev == ConnectionMode.NOT_SET) {
      prev = ConnectionMode.DISABLED;
    }
    return prev;
  }

  @Override
  public boolean haveCollidablesChangedSinceLastCall() {
    if (dodgyChangeSinceLastCallFlagForBundle) {
      dodgyChangeSinceLastCallFlagForBundle = false;
      return true;
    }
    return false;
  }

  @Override
  public BlockCoord getLocation() {
    if (bundle == null) {
      return null;
    }
    return bundle.getLocation();
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
  public Set<EnumFacing> getConduitConnections() {
    return conduitConnections;
  }

  @Override
  public boolean containsConduitConnection(EnumFacing dir) {
    return conduitConnections.contains(dir);
  }

  @Override
  public void conduitConnectionAdded(EnumFacing fromDirection) {
    conduitConnections.add(fromDirection);
  }

  @Override
  public void conduitConnectionRemoved(EnumFacing fromDirection) {
    conduitConnections.remove(fromDirection);
  }

  @Override
  public boolean canConnectToConduit(EnumFacing direction, IConduit conduit) {
    if (conduit == null) {
      return false;
    }
    return getConnectionMode(direction) != ConnectionMode.DISABLED
        && conduit.getConnectionMode(direction.getOpposite()) != ConnectionMode.DISABLED;
  }

  @Override
  public boolean canConnectToExternal(EnumFacing direction, boolean ignoreConnectionMode) {
    return false;
  }

  @Override
  public Set<EnumFacing> getExternalConnections() {
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
  public boolean containsExternalConnection(EnumFacing dir) {
    return externalConnections.contains(dir);
  }

  @Override
  public void externalConnectionAdded(EnumFacing fromDirection) {
    externalConnections.add(fromDirection);
  }

  @Override
  public void externalConnectionRemoved(EnumFacing fromDirection) {
    externalConnections.remove(fromDirection);
  }

  @Override
  public boolean isConnectedTo(EnumFacing dir) {
    return containsConduitConnection(dir) || containsExternalConnection(dir);
  }

  @Override
  public boolean isActive() {
    return active;
  }

  @Override
  public void setActive(boolean active) {
    if (active != this.active) {
      clientStateDirty = true;
    }
    this.active = active;
  }

  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {
    int[] dirs = new int[conduitConnections.size()];
    Iterator<EnumFacing> cons = conduitConnections.iterator();
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

    if (conectionModes.size() > 0) {
      byte[] modes = new byte[6];
      int i = 0;
      for (EnumFacing dir : EnumFacing.VALUES) {
        modes[i] = (byte) getConnectionMode(dir).ordinal();
        i++;
      }
      nbtRoot.setByteArray("conModes", modes);
    }
  }

  @Override
  public void readFromNBT(NBTTagCompound nbtRoot, short nbtVersion) {
    conduitConnections.clear();
    int[] dirs = nbtRoot.getIntArray("connections");
    for (int i = 0; i < dirs.length; i++) {
      conduitConnections.add(EnumFacing.values()[dirs[i]]);
    }

    externalConnections.clear();
    dirs = nbtRoot.getIntArray("externalConnections");
    for (int i = 0; i < dirs.length; i++) {
      externalConnections.add(EnumFacing.values()[dirs[i]]);
    }
    active = nbtRoot.getBoolean("signalActive");

    conectionModes.clear();
    byte[] modes = nbtRoot.getByteArray("conModes");
    if (modes != null && modes.length == 6) {
      int i = 0;
      for (EnumFacing dir : EnumFacing.VALUES) {
        conectionModes.put(dir, ConnectionMode.values()[modes[i]]);
        i++;
      }
    }
    readFromNbt = true;
  }

  @Override
  public int getLightValue() {
    return 0;
  }

  @Override
  public boolean onBlockActivated(EntityPlayer player, EnumHand hand, RaytraceResult res, List<RaytraceResult> all) {
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
  public void onChunkUnload(World world) {
    AbstractConduitNetwork<?, ?> network = getNetwork();
    if (network != null) {
      network.destroyNetwork();
    }
  }

  @Override
  public void updateEntity(World world) {
    if (world.isRemote) {
      return;
    }
    updateNetwork(world);
    updateConnections();
    readFromNbt = false; // the two update*()s react to this on their first run
    if (clientStateDirty && getBundle() != null) {
      getBundle().dirty();
      clientStateDirty = false;
    }
  }

  private void updateConnections() {
    if (!connectionsDirty && !readFromNbt) {
      return;
    }

    boolean externalConnectionsChanged = false;
    List<EnumFacing> copy = new ArrayList<EnumFacing>(externalConnections);
    // remove any no longer valid connections
    for (EnumFacing dir : copy) {
      if (!canConnectToExternal(dir, false) || readFromNbt) {
        externalConnectionRemoved(dir);
        externalConnectionsChanged = true;
      }
    }

    // then check for new ones
    for (EnumFacing dir : EnumFacing.VALUES) {
      if (!conduitConnections.contains(dir) && !externalConnections.contains(dir)) {
        if (canConnectToExternal(dir, false)) {
          externalConnectionAdded(dir);
          externalConnectionsChanged = true;
        }
      }
    }
    if (externalConnectionsChanged) {
      connectionsChanged();
    }

    connectionsDirty = false;
  }

  @Override
  public void connectionsChanged() {
    collidablesDirty = true;
    clientStateDirty = true;
    dodgyChangeSinceLastCallFlagForBundle = true;
  }

  protected void setClientStateDirty() {
    clientStateDirty = true;
  }

  protected void updateNetwork(World world) {
    BlockCoord pos = getLocation();
    if (getNetwork() == null && world.isBlockLoaded(pos.getBlockPos())) {
      ConduitUtil.ensureValidNetwork(this);
      if (getNetwork() != null) {
        getNetwork().sendBlockUpdatesForEntireNetwork();
        if (readFromNbt) {
          connectionsChanged();
        }
      }
    }
  }

  @Override
  public void onAddedToBundle() {

    TileEntity te = bundle.getEntity();
    World world = te.getWorld();

    conduitConnections.clear();
    for (EnumFacing dir : EnumFacing.VALUES) {
      IConduit neighbour = ConduitUtil.getConduit(world, te, dir, getBaseConduitType());
      if (neighbour != null && neighbour.canConnectToConduit(dir.getOpposite(), this)) {
        conduitConnections.add(dir);
        neighbour.conduitConnectionAdded(dir.getOpposite());
        neighbour.connectionsChanged();
      }
    }

    externalConnections.clear();
    for (EnumFacing dir : EnumFacing.VALUES) {
      if (!containsConduitConnection(dir) && canConnectToExternal(dir, false)) {
        externalConnectionAdded(dir);
      }
    }

    connectionsChanged();
  }

  @Override
  public void onRemovedFromBundle() {
    TileEntity te = bundle.getEntity();
    World world = te.getWorld();

    for (EnumFacing dir : conduitConnections) {
      IConduit neighbour = ConduitUtil.getConduit(world, te, dir, getBaseConduitType());
      if (neighbour != null) {
        neighbour.conduitConnectionRemoved(dir.getOpposite());
        neighbour.connectionsChanged();
      }
    }
    conduitConnections.clear();

    if (!externalConnections.isEmpty()) {
      world.notifyNeighborsOfStateChange(te.getPos(), te.getBlockType());
    }
    externalConnections.clear();

    AbstractConduitNetwork<?, ?> network = getNetwork();
    if (network != null) {
      network.destroyNetwork();
    }
    connectionsChanged();
  }

  @Override
  public boolean onNeighborBlockChange(Block block) {

    // NB: No need to check externals if the neighbour that changed was a
    // conduit bundle as this
    // can't effect external connections.
    if (block == blockConduitBundle.getBlock()) {
      return false;
    }

    lastExternalRedstoneLevel = null;

    // Check for changes to external connections, connections to conduits are
    // handled by the bundle
    Set<EnumFacing> newCons = EnumSet.noneOf(EnumFacing.class);
    for (EnumFacing dir : EnumFacing.VALUES) {
      if (!containsConduitConnection(dir) && canConnectToExternal(dir, false)) {
        newCons.add(dir);
      }
    }
    if (newCons.size() != externalConnections.size()) {
      connectionsDirty = true;
      return true;
    }
    for (EnumFacing dir : externalConnections) {
      if (!newCons.remove(dir)) {
        connectionsDirty = true;
        return true;
      }
    }
    if (!newCons.isEmpty()) {
      connectionsDirty = true;
      return true;
    }
    return false;
  }

  @Override
  public boolean onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbourPos) {
    return false;
  }

  @Override
  public Collection<CollidableComponent> createCollidables(CacheKey key) {    
    return Collections.singletonList(new CollidableComponent(getCollidableType(), ConduitGeometryUtil.instance.getBoundingBox(
        getBaseConduitType(), key.dir, key.isStub, key.offset), key.dir, null));
  }

  @Override
  public Class<? extends IConduit> getCollidableType() {
    return getBaseConduitType();
  }

  @Override
  public List<CollidableComponent> getCollidableComponents() {

    if (collidables != null && !collidablesDirty) {
      return collidables;
    }

    List<CollidableComponent> result = new ArrayList<CollidableComponent>();
    for (EnumFacing dir : EnumFacing.VALUES) {
      Collection<CollidableComponent> col = getCollidables(dir);
      if (col != null) {
        result.addAll(col);
      }
    }
    collidables = result;

    collidablesDirty = false;

    return result;
  }

  protected boolean renderStub(EnumFacing dir) {
    // return getConectionMode(dir) == ConnectionMode.DISABLED;
    return false;
  }

  private Collection<CollidableComponent> getCollidables(EnumFacing dir) {
    CollidableCache cc = CollidableCache.instance;
    Class<? extends IConduit> type = getCollidableType();
    if (isConnectedTo(dir) && getConnectionMode(dir) != ConnectionMode.DISABLED) {
      return cc.getCollidables(cc.createKey(type, getBundle().getOffset(getBaseConduitType(), dir), dir, renderStub(dir)), this);
    }
    return null;
  }

  @Override
  public boolean shouldMirrorTexture() {
    return true;
  }

  @SideOnly(Side.CLIENT)
  public void hashCodeForModelCaching(IBlockStateWrapper wrapper, BlockStateWrapperConduitBundle.ConduitCacheKey hashCodes) {
    hashCodes.add(this.getClass());
    hashCodes.add(conduitConnections, externalConnections, conectionModes);
  }

  @Override
  public void invalidate() {
  }

  @Override
  public int getExternalRedstoneLevel() {
    if (lastExternalRedstoneLevel == null) {
      if (bundle == null || bundle.getEntity() == null) {
        return 0;
      }
      TileEntity te = bundle.getEntity();
      lastExternalRedstoneLevel = ConduitUtil.isBlockIndirectlyGettingPoweredIfLoaded(te.getWorld(), te.getPos());
    }
    return lastExternalRedstoneLevel;
  }

  @Override
  public String toString() {
    return "AbstractConduit [getClass()=" + getClass() + ", lastExternalRedstoneLevel=" + lastExternalRedstoneLevel + ", getConduitConnections()="
        + getConduitConnections() + ", getExternalConnections()=" + getExternalConnections() + ", getNetwork()=" + getNetwork() + "]";
  }
}
