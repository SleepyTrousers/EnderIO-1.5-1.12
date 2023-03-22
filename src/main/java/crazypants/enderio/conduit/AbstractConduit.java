package crazypants.enderio.conduit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import mods.immibis.microblocks.api.EnumPartClass;
import mods.immibis.microblocks.api.EnumPosition;
import mods.immibis.microblocks.api.IMicroblockCoverSystem;
import mods.immibis.microblocks.api.Part;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.gas.GasConduitNetwork;
import crazypants.enderio.conduit.gas.IGasConduit;
import crazypants.enderio.conduit.geom.CollidableCache;
import crazypants.enderio.conduit.geom.CollidableCache.CacheKey;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.geom.ConduitGeometryUtil;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.item.ItemConduitNetwork;
import crazypants.enderio.conduit.liquid.AdvancedLiquidConduit;
import crazypants.enderio.conduit.liquid.AdvancedLiquidConduitNetwork;
import crazypants.enderio.conduit.liquid.CrystallineEnderLiquidConduit;
import crazypants.enderio.conduit.liquid.CrystallinePinkSlimeEnderLiquidConduit;
import crazypants.enderio.conduit.liquid.EnderLiquidConduit;
import crazypants.enderio.conduit.liquid.EnderLiquidConduitNetwork;
import crazypants.enderio.conduit.liquid.ILiquidConduit;
import crazypants.enderio.conduit.liquid.LiquidConduitNetwork;
import crazypants.enderio.conduit.liquid.MelodicEnderLiquidConduit;
import crazypants.enderio.conduit.liquid.StellarEnderLiquidConduit;
import crazypants.enderio.conduit.me.IMEConduit;
import crazypants.enderio.conduit.me.MEConduitNetwork;
import crazypants.enderio.conduit.oc.IOCConduit;
import crazypants.enderio.conduit.oc.OCConduitNetwork;
import crazypants.enderio.conduit.power.IPowerConduit;
import crazypants.enderio.conduit.power.PowerConduitNetwork;
import crazypants.enderio.conduit.redstone.IRedstoneConduit;
import crazypants.enderio.conduit.redstone.RedstoneConduitNetwork;

public abstract class AbstractConduit implements IConduit {

    protected final Set<ForgeDirection> conduitConnections = EnumSet.noneOf(ForgeDirection.class);

    protected final Set<ForgeDirection> externalConnections = EnumSet.noneOf(ForgeDirection.class);

    public static final float STUB_WIDTH = 0.2f;

    public static final float STUB_HEIGHT = 0.2f;

    public static final float TRANSMISSION_SCALE = 0.3f;

    // NB: This is a transient field controlled by the owning bundle. It is not
    // written to the NBT etc
    protected IConduitBundle bundle;

    protected boolean active;

    protected List<CollidableComponent> collidables;

    protected final EnumMap<ForgeDirection, ConnectionMode> conectionModes = new EnumMap<ForgeDirection, ConnectionMode>(
            ForgeDirection.class);

    protected boolean collidablesDirty = true;

    private boolean clientStateDirty = true;

    private boolean dodgyChangeSinceLastCallFlagForBundle = true;

    protected boolean connectionsDirty = true;

    protected boolean needUpdateConnections = false;

    protected AbstractConduit() {}

    @Override
    public boolean writeConnectionSettingsToNBT(ForgeDirection dir, NBTTagCompound nbt) {
        if (!getExternalConnections().contains(dir)) {
            return false;
        }
        NBTTagCompound dataRoot = getNbtRootForType(nbt, true);
        dataRoot.setShort("connectionMode", (short) getConnectionMode(dir).ordinal());
        writeTypeSettingsToNbt(dir, dataRoot);
        return true;
    }

    @Override
    public boolean readConduitSettingsFromNBT(ForgeDirection dir, NBTTagCompound nbt) {
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

    protected void readTypeSettings(ForgeDirection dir, NBTTagCompound dataRoot) {}

    protected void writeTypeSettingsToNbt(ForgeDirection dir, NBTTagCompound dataRoot) {}

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
    public ConnectionMode getConnectionMode(ForgeDirection dir) {
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
    public void setConnectionMode(ForgeDirection dir, ConnectionMode mode) {
        ConnectionMode oldVal = conectionModes.get(dir);
        if (oldVal == mode) {
            return;
        }

        if (mode == null) {
            conectionModes.remove(dir);
        } else {
            conectionModes.put(dir, mode);
        }
        clientStateDirty = true;
        collidablesDirty = true;

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
    public ConnectionMode getNextConnectionMode(ForgeDirection dir) {
        ConnectionMode curMode = getConnectionMode(dir);
        ConnectionMode next = ConnectionMode.getNext(curMode);
        if (next == ConnectionMode.NOT_SET) {
            next = ConnectionMode.IN_OUT;
        }
        return next;
    }

    @Override
    public ConnectionMode getPreviousConnectionMode(ForgeDirection dir) {
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
    }

    @Override
    public void conduitConnectionRemoved(ForgeDirection fromDirection) {
        conduitConnections.remove(fromDirection);
    }

    @Override
    public boolean canConnectToConduit(ForgeDirection direction, IConduit conduit) {
        if (conduit == null) {
            return false;
        }
        if (MicroblocksUtil.supportMicroblocks() && isBlockedByMicroblocks(direction, conduit)) {
            return false;
        }
        return getConnectionMode(direction) != ConnectionMode.DISABLED
                && conduit.getConnectionMode(direction.getOpposite()) != ConnectionMode.DISABLED;
    }

    @Override
    public boolean canConnectToExternal(ForgeDirection direction, boolean ignoreConnectionMode) {
        return false;
    }

    protected boolean isBlockedByMicroblocks(ForgeDirection direction, IConduit conduit) {
        IMicroblockCoverSystem covers = getBundle().getCoverSystem();
        for (Part part : covers.getAllParts()) {
            if (part.type.getPartClass() == EnumPartClass.Panel) {
                return part.pos == EnumPosition.getFacePosition(direction.ordinal());
            }
        }
        covers = conduit.getBundle().getCoverSystem();
        for (Part part : covers.getAllParts()) {
            if (part.type.getPartClass() == EnumPartClass.Panel) {
                return part.pos == EnumPosition.getFacePosition(direction.getOpposite().ordinal());
            }
        }
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
    }

    @Override
    public void externalConnectionRemoved(ForgeDirection fromDirection) {
        externalConnections.remove(fromDirection);
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
        if (active != this.active) {
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

        if (conectionModes.size() > 0) {
            byte[] modes = new byte[6];
            int i = 0;
            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
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
        if (modes != null && modes.length == 6) {
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
        if (clientStateDirty && getBundle() != null) {
            getBundle().dirty();
            clientStateDirty = false;
        }
    }

    private void updateConnections() {
        if (!connectionsDirty) {
            return;
        }

        boolean externalConnectionsChanged = false;
        List<ForgeDirection> copy = new ArrayList<ForgeDirection>(externalConnections);
        // remove any no longer valid connections
        for (ForgeDirection dir : copy) {
            if (!canConnectToExternal(dir, false)) {
                externalConnectionRemoved(dir);
                externalConnectionsChanged = true;
            }
        }

        // then check for new ones
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
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

        if (needUpdateConnections) {
            needUpdateConnections = false;
        } else {
            connectionsDirty = false;
        }
    }

    protected void needUpdateConnections() {
        needUpdateConnections = true;
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
        if (getNetwork() == null && world.blockExists(pos.x, pos.y, pos.z)) {
            ConduitUtil.ensureValidNetwork(this);
            // TODO figure out if removing this causes anything horrible to happen.
            // Initial testing shows no difference.
            // if(getNetwork() != null && !world.isRemote && bundle != null) {
            // world.notifyBlocksOfNeighborChange(bundle.getEntity().xCoord,
            // bundle.getEntity().yCoord, bundle.getEntity().zCoord,
            // bundle.getEntity().getBlockType());
            // }
        }
    }

    @Override
    public void onAddedToBundle() {

        TileEntity te = bundle.getEntity();
        World world = te.getWorldObj();

        conduitConnections.clear();
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            IConduit neighbour = ConduitUtil.getConduit(world, te, dir, getBaseConduitType());
            if (neighbour != null && neighbour.canConnectToConduit(dir.getOpposite(), this)) {
                conduitConnections.add(dir);
                neighbour.conduitConnectionAdded(dir.getOpposite());
                neighbour.connectionsChanged();
            }
        }

        externalConnections.clear();
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (!containsConduitConnection(dir) && canConnectToExternal(dir, false)) {
                externalConnectionAdded(dir);
            }
        }

        connectionsChanged();
    }

    @Override
    public void onRemovedFromBundle() {
        TileEntity te = bundle.getEntity();
        World world = te.getWorldObj();

        for (ForgeDirection dir : conduitConnections) {
            IConduit neighbour = ConduitUtil.getConduit(world, te, dir, getBaseConduitType());
            if (neighbour != null) {
                neighbour.conduitConnectionRemoved(dir.getOpposite());
                neighbour.connectionsChanged();
            }
        }
        conduitConnections.clear();

        if (!externalConnections.isEmpty()) {
            world.notifyBlocksOfNeighborChange(te.xCoord, te.yCoord, te.zCoord, EnderIO.blockConduitBundle);
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
        if (block == EnderIO.blockConduitBundle) {
            return false;
        }

        // Check for changes to external connections, connections to conduits are
        // handled by the bundle
        Set<ForgeDirection> newCons = EnumSet.noneOf(ForgeDirection.class);
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (!containsConduitConnection(dir) && canConnectToExternal(dir, false)) {
                newCons.add(dir);
            }
        }
        if (newCons.size() != externalConnections.size()) {
            connectionsDirty = true;
            return true;
        }
        for (ForgeDirection dir : externalConnections) {
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
    public boolean onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ) {
        return onNeighborBlockChange(world.getBlock(tileX, tileY, tileZ));
    }

    @Override
    public Collection<CollidableComponent> createCollidables(CacheKey key) {
        return Collections.singletonList(
                new CollidableComponent(
                        getCollidableType(),
                        ConduitGeometryUtil.instance
                                .getBoundingBox(getBaseConduitType(), key.dir, key.isStub, key.offset),
                        key.dir,
                        null));
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
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            Collection<CollidableComponent> col = getCollidables(dir);
            if (col != null) {
                result.addAll(col);
            }
        }
        collidables = result;

        collidablesDirty = false;

        return result;
    }

    protected boolean renderStub(ForgeDirection dir) {
        // return getConectionMode(dir) == ConnectionMode.DISABLED;
        return false;
    }

    private Collection<CollidableComponent> getCollidables(ForgeDirection dir) {
        CollidableCache cc = CollidableCache.instance;
        Class<? extends IConduit> type = getCollidableType();
        if (isConnectedTo(dir) && getConnectionMode(dir) != ConnectionMode.DISABLED) {
            return cc.getCollidables(
                    cc.createKey(type, getBundle().getOffset(getBaseConduitType(), dir), dir, renderStub(dir)),
                    this);
        }
        return null;
    }

    @Override
    public AbstractConduitNetwork<?, ?> createNetworkForType() {
        Class<? extends IConduit> type = this.getClass();
        if (IRedstoneConduit.class.isAssignableFrom(type)) {
            return new RedstoneConduitNetwork();
        } else if (IPowerConduit.class.isAssignableFrom(type)) {
            return new PowerConduitNetwork();
        } else if (StellarEnderLiquidConduit.class.isAssignableFrom(type)) {
            return new EnderLiquidConduitNetwork(StellarEnderLiquidConduit.TYPE);
        } else if (MelodicEnderLiquidConduit.class.isAssignableFrom(type)) {
            return new EnderLiquidConduitNetwork(MelodicEnderLiquidConduit.TYPE);
        } else if (CrystallinePinkSlimeEnderLiquidConduit.class.isAssignableFrom(type)) {
            return new EnderLiquidConduitNetwork(CrystallinePinkSlimeEnderLiquidConduit.TYPE);
        } else if (CrystallineEnderLiquidConduit.class.isAssignableFrom(type)) {
            return new EnderLiquidConduitNetwork(CrystallineEnderLiquidConduit.TYPE);
        } else if (EnderLiquidConduit.class.isAssignableFrom(type)) {
            return new EnderLiquidConduitNetwork(EnderLiquidConduit.TYPE);
        } else if (AdvancedLiquidConduit.class.isAssignableFrom(type)) {
            return new AdvancedLiquidConduitNetwork();
        } else if (ILiquidConduit.class.isAssignableFrom(type)) {
            return new LiquidConduitNetwork();
        } else if (IItemConduit.class.isAssignableFrom(type)) {
            return new ItemConduitNetwork();
        } else if (IGasConduit.class.isAssignableFrom(type)) {
            return new GasConduitNetwork();
        } else if (IMEConduit.class.isAssignableFrom(type)) {
            return new MEConduitNetwork();
        } else if (IOCConduit.class.isAssignableFrom(type)) {
            return new OCConduitNetwork();
        } else {
            return null;
        }
    }

    @Override
    public boolean shouldMirrorTexture() {
        return true;
    }
}
