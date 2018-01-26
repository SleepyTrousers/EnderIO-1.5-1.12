package crazypants.enderio.base.conduit;

import com.enderio.core.api.client.gui.ITabPanel;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.vecmath.Vector4f;
import crazypants.enderio.base.conduit.geom.CollidableCache.CacheKey;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface IConduit extends ICapabilityProvider {

  // stuff that would be on the class, not the object if there were interfaces for classes...

  /**
   * Creates the gui for the conduit within the external connection gui
   *
   * @param gui the gui to construct the panel inside of
   * @param con the conduit that the gui references
   * @return the panel for the conduit's information on the gui
   */
  @SideOnly(Side.CLIENT)
  @Nonnull
  ITabPanel createGuiPanel(@Nonnull IGuiExternalConnection gui, @Nonnull IConduit con);

  /**
   * Determines the order the panels are shown in the conduit gui tabs
   *
   * @return the integer position of the panel in order (top --> bottom)
   */
  @SideOnly(Side.CLIENT)
  int getGuiPanelTabOrder();

  /**
   * Creates a conduit network for the given type of conduit
   *
   * @return the network for the given conduit type
   */
  @Nonnull
  IConduitNetwork<?, ?> createNetworkForType();

  // Base functionality

  /**
   * Gets the conduit's base type (e.g. ItemConduit, LiquidConduit, etc.)
   *
   * @return The conduit's base type
   */
  @Nonnull
  Class<? extends IConduit> getBaseConduitType();

  /**
   * Creates a single item for the conduit. Called when determining the Conduit's drops
   *
   * @return ItemStack of amount 1 for the given conduit
   */
  @Nonnull
  ItemStack createItem();

  /**
   * List of drops for the conduit. This includes filters and upgrades
   *
   * @return NNList of ItemStacks to drop
   */
  @Nonnull
  NNList<ItemStack> getDrops();

  default int getLightValue() {
    return 0;
  }

  default int getExternalRedstoneLevel() {
    return 0;
  }

  /**
   * @return true if the conduit is currently in use
   */
  boolean isActive();

  /**
   * Sets the conduit's state to active. Called when the conduit is operating
   *
   * @param active true if the conduit is currently doing something
   */
  void setActive(boolean active);

  void writeToNBT(@Nonnull NBTTagCompound conduitBody);

  void readFromNBT(@Nonnull NBTTagCompound conduitBody);

  // Container

  /**
   * Sets the conduit's bundle
   *
   * @param tileConduitBundle bundle that this conduit belongs to
   */
  void setBundle(@Nullable IConduitBundle tileConduitBundle);

  /**
   * @return the bundle the conduit belongs to
   * @throws NullPointerException
   */
  @Nonnull
  IConduitBundle getBundle() throws NullPointerException;

  void onAddedToBundle();

  void onRemovedFromBundle();

  // Connections

  /**
   * @return true if the conduit has conduit or non-conduit connections
   */
  boolean hasConnections();

  /**
   * @return true if the conduit has connections to non-conduits
   */
  boolean hasExternalConnections();

  /**
   * @return true if the conduit has conduit connections
   */
  boolean hasConduitConnections();

  // Conduit Connections

  /**
   * Determines if this conduit can connect to another conduit
   *
   * @param direction direction of the conduit to connect to
   * @param conduit   conduit to connect to
   * @return true if the conduit can connect in that direction and if the conduit is of the same or another valid type
   */
  boolean canConnectToConduit(@Nonnull EnumFacing direction, @Nonnull IConduit conduit);

  /**
   * @return Returns the set of the directions of connection for the conduits
   */
  @Nonnull
  Set<EnumFacing> getConduitConnections();

  /**
   * Checks if the conduit has a connection in the direction given
   *
   * @param dir direction to check for connection
   * @return true if the conduit has a connection in the given direction
   */
  default boolean containsConduitConnection(@Nonnull EnumFacing dir) {
    return getConduitConnections().contains(dir);
  }

  /**
   * Called when a conduit has a connection added
   *
   * @param fromDirection the direction of the connection
   */
  void conduitConnectionAdded(@Nonnull EnumFacing fromDirection);

  /**
   * Called when a conduit has a connection removed
   *
   * @param fromDirection the direction that the connection was removed from
   */
  void conduitConnectionRemoved(@Nonnull EnumFacing fromDirection);

  /**
   * Called when conduit connections are changed
   */
  void connectionsChanged();

  /**
   * @return the network of the conduit
   * @throws NullPointerException
   */
  IConduitNetwork<?, ?> getNetwork() throws NullPointerException;

  /**
   * Sets the network of this conduit to a new network. Called when the conduit is connected to a new network
   *
   * @param network the network to make the conduit a part of
   * @return true if a new network is successfully set
   */
  boolean setNetwork(@Nonnull IConduitNetwork<?, ?> network);

  // External Connections

  /**
   * Checks if the conduit can connect to non-conduit blocks
   *
   * @param direction            direction of the non-conduit block
   * @param ignoreConnectionMode true if the conduit should connect regardless of the block
   * @return true if the conduit can connect
   */
  boolean canConnectToExternal(@Nonnull EnumFacing direction, boolean ignoreConnectionMode);

  /**
   * @return the set of directions of all non-conduit connections
   */
  @Nonnull
  Set<EnumFacing> getExternalConnections();

  /**
   * Checks if the conduit is connected to a non-conduit block in the given direction
   *
   * @param dir the direction to check for an external connection
   * @return true if the given direction is an external connection
   */
  default boolean containsExternalConnection(@Nonnull EnumFacing dir) {
    return getExternalConnections().contains(dir);
  }

  /**
   * Called when a connection to a non-conduit block is added
   *
   * @param fromDirection the direction of the connection
   */
  void externalConnectionAdded(@Nonnull EnumFacing fromDirection);

  /**
   * Called whan a connection to a non-conduit block is removed
   *
   * @param fromDirection the direction of the connection
   */
  void externalConnectionRemoved(@Nonnull EnumFacing fromDirection);

  /**
   * Checks if the conduit has a connection in the given direction
   *
   * @param dir the direction to check for a connection
   * @return true if the conduit has a connection in that direction
   */
  boolean isConnectedTo(@Nonnull EnumFacing dir);

  /**
   * Gets the connection mode in the given direction
   *
   * @param dir the direction of the connection
   * @return the connection mode (IN, OUT, IN_OUT, DISABLED or NONE)
   */
  @Nonnull
  ConnectionMode getConnectionMode(@Nonnull EnumFacing dir);

  /**
   * Sets the connection mode of the conduit in the given direction
   *
   * @param dir  direction of the connection
   * @param mode the connection mode (IN, OUT, IN_OUT, DISABLED or NONE)
   */
  void setConnectionMode(@Nonnull EnumFacing dir, @Nonnull ConnectionMode mode);

  /**
   * Checks if this conduit can use the given connection mode
   *
   * @param mode connection mode (IN, OUT, IN_OUT, DISABLED or NONE) to check
   * @return true if the conduit can use the given connection mode
   */
  boolean supportsConnectionMode(@Nonnull ConnectionMode mode);

  /**
   * Gets the next connection mode in the cycle
   *
   * @param dir the direction of the connection for getting its connection mode
   * @return next connection mode in the list
   */
  @Nonnull
  default ConnectionMode getNextConnectionMode(@Nonnull EnumFacing dir) {
    return NNList.of(ConnectionMode.class).next(getConnectionMode(dir));
  }

  /**
   * Gets the previous connection mode in the cycle
   *
   * @param dir the direction of the connection for getting its connection mode
   * @return previous connection mode in the list
   */
  @Nonnull
  default ConnectionMode getPreviousConnectionMode(@Nonnull EnumFacing dir) {
    return NNList.of(ConnectionMode.class).prev(getConnectionMode(dir));
  }

  // rendering, only needed if default rendering is used
  interface WithDefaultRendering extends IConduit {

    @SideOnly(Side.CLIENT)
    @Nonnull
    TextureAtlasSprite getTextureForState(@Nonnull CollidableComponent component);

    @SideOnly(Side.CLIENT)
    @Nonnull
    TextureAtlasSprite getTransmitionTextureForState(@Nonnull CollidableComponent component);

    @SideOnly(Side.CLIENT)
    public @Nonnull Vector4f getTransmitionTextureColorForState(@Nonnull CollidableComponent component);

    @SideOnly(Side.CLIENT)
    float getTransmitionGeometryScale();

    @SideOnly(Side.CLIENT)
    float getSelfIlluminationForState(@Nonnull CollidableComponent component);

    /**
     * Should the texture of the conduit connectors be mirrored around the conduit node?
     */
    @SideOnly(Side.CLIENT)
    boolean shouldMirrorTexture();

  }

  // geometry (collision, a.k.a. server-side)

  boolean haveCollidablesChangedSinceLastCall();

  @Nonnull
  Collection<CollidableComponent> getCollidableComponents();

  @Nonnull
  Collection<CollidableComponent> createCollidables(@Nonnull CacheKey key);

  @Nonnull
  Class<? extends IConduit> getCollidableType();

  // Actions (mostly relayed from the Block/TE methods of the same name)
  boolean onBlockActivated(@Nonnull EntityPlayer player, @Nonnull EnumHand hand, @Nonnull RaytraceResult res, @Nonnull List<RaytraceResult> all);

  void onChunkUnload();

  void updateEntity(@Nonnull World world); // Please, do not tick unless really, really needed!

  boolean onNeighborBlockChange(@Nonnull Block block);

  boolean onNeighborChange(@Nonnull BlockPos neighbourPos);

  void invalidate();

  // For Copy/Paste of connection settings

  boolean writeConnectionSettingsToNBT(@Nonnull EnumFacing dir, @Nonnull NBTTagCompound nbt);

  boolean readConduitSettingsFromNBT(@Nonnull EnumFacing dir, @Nonnull NBTTagCompound nbt);
}
