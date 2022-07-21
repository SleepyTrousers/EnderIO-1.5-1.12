package crazypants.enderio.conduit;

import com.enderio.core.common.util.BlockCoord;
import crazypants.enderio.conduit.geom.CollidableCache.CacheKey;
import crazypants.enderio.conduit.geom.CollidableComponent;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public interface IConduit {

    // Base functionality
    Class<? extends IConduit> getBaseConduitType();

    ItemStack createItem();

    List<ItemStack> getDrops();

    int getLightValue();

    boolean isActive();

    void setActive(boolean active);

    void writeToNBT(NBTTagCompound conduitBody);

    void readFromNBT(NBTTagCompound conduitBody, short nbtVersion);

    // Container

    void setBundle(IConduitBundle tileConduitBundle);

    IConduitBundle getBundle();

    void onAddedToBundle();

    void onRemovedFromBundle();

    BlockCoord getLocation();

    // Conections
    boolean hasConnections();

    boolean hasExternalConnections();

    boolean hasConduitConnections();

    // Conduit Connections

    boolean canConnectToConduit(ForgeDirection direction, IConduit conduit);

    Set<ForgeDirection> getConduitConnections();

    boolean containsConduitConnection(ForgeDirection dir);

    void conduitConnectionAdded(ForgeDirection fromDirection);

    void conduitConnectionRemoved(ForgeDirection fromDirection);

    void connectionsChanged();

    AbstractConduitNetwork<?, ?> getNetwork();

    boolean setNetwork(AbstractConduitNetwork<?, ?> network);

    // External Connections

    boolean canConnectToExternal(ForgeDirection direction, boolean ignoreConnectionMode);

    Set<ForgeDirection> getExternalConnections();

    boolean containsExternalConnection(ForgeDirection dir);

    void externalConnectionAdded(ForgeDirection fromDirection);

    void externalConnectionRemoved(ForgeDirection fromDirection);

    boolean isConnectedTo(ForgeDirection dir);

    ConnectionMode getConnectionMode(ForgeDirection dir);

    void setConnectionMode(ForgeDirection dir, ConnectionMode mode);

    boolean hasConnectionMode(ConnectionMode mode);

    ConnectionMode getNextConnectionMode(ForgeDirection dir);

    ConnectionMode getPreviousConnectionMode(ForgeDirection dir);

    // rendering, only needed us default rendering is used

    IIcon getTextureForState(CollidableComponent component);

    IIcon getTransmitionTextureForState(CollidableComponent component);

    float getTransmitionGeometryScale();

    float getSelfIlluminationForState(CollidableComponent component);

    // geometry

    boolean haveCollidablesChangedSinceLastCall();

    Collection<CollidableComponent> getCollidableComponents();

    Collection<CollidableComponent> createCollidables(CacheKey key);

    Class<? extends IConduit> getCollidableType();

    // Actions

    boolean onBlockActivated(EntityPlayer player, RaytraceResult res, List<RaytraceResult> all);

    void onChunkUnload(World worldObj);

    void updateEntity(World worldObj);

    boolean onNeighborBlockChange(Block blockId);

    boolean onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ);

    // For Copy/Paste of connection settings
    boolean writeConnectionSettingsToNBT(ForgeDirection dir, NBTTagCompound nbt);

    boolean readConduitSettingsFromNBT(ForgeDirection dir, NBTTagCompound nbt);

    public AbstractConduitNetwork<?, ?> createNetworkForType();

    /**
     * Should the texture of the conduit connectors be mirrored around the conduit
     * node?
     */
    boolean shouldMirrorTexture();
}
