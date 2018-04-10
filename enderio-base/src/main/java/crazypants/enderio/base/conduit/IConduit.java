package crazypants.enderio.base.conduit;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.conduit.geom.CollidableCache.CacheKey;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import info.loenwind.autosave.annotations.Storable;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public interface IConduit extends ICapabilityProvider {

  // Base functionality

  /**
   * Gets the conduit's base type (e.g. ItemConduit, LiquidConduit, etc.)
   *
   * @return The conduit's base type
   */
  @Nonnull
  Class<? extends IConduit> getBaseConduitType();

  /**
   * Creates a single item for the conduit. Called when determining the Conduit's drops, when picking the block and when determining if a held item is the same
   * as a placed conduit.
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
  default @Nonnull NNList<ItemStack> getDrops() {
    return new NNList<ItemStack>(createItem());
  }

  /**
   * see {@link Block#getLightValue(net.minecraft.block.state.IBlockState, net.minecraft.world.IBlockAccess, net.minecraft.util.math.BlockPos)}
   * <p>
   * Only used if ConduitConfig.dynamicLighting is set.
   */
  default int getLightValue() {
    return 0;
  }

  /**
   * Called to read data from the save file (for server conduits) or from the update package (client conduits).
   * <p>
   * Note that this will not be called for conduits that implement {@link Storable} (once conduits can do that).
   * 
   * @param tag
   *          The tag as it was written by {@link IServerConduit#writeToNBT(NBTTagCompound)}
   */
  void readFromNBT(@Nonnull NBTTagCompound tag);

  // Container

  /**
   * Sets the conduit's bundle. This will be called just before the conduit is placed into the list of conduits in the bundle. It can be used to initialize
   * transient values, but should not access the world or other conduits of the bundle. It will also only ever be called once.
   *
   * @param bundle
   *          bundle that this conduit belongs to
   */
  void setBundle(@Nullable IConduitBundle bundle);

  /**
   * @return The bundle the conduit belongs to
   * @throws NullPointerException
   */
  @Nonnull
  IConduitBundle getBundle() throws NullPointerException;

  // Connections

  /**
   * @return <code>true</code> if the conduit has conduit or non-conduit connections
   */
  default boolean hasConnections() {
    return hasConduitConnections() || hasExternalConnections();
  }

  /**
   * External connections are those to non-conduit neighbors. They are rendered with a connector plate and are required to have a connection settings GUI.
   * 
   * @return <code>true</code> if the conduit has connections to non-conduits
   */
  boolean hasExternalConnections();

  /**
   * Conduit connections are connections to the same conduit type (see {@link IServerConduit#canConnectToConduit(EnumFacing, IConduit)}) in neighboring conduit
   * bundles. They cannot have a GUI, but they can be removed/added with the wrench.
   * 
   * @return <code>true</code> if the conduit has conduit connections
   */
  boolean hasConduitConnections();

  // Conduit Connections

  /**
   * @return Returns the set of the directions of connection for the conduits
   */
  @Nonnull
  Set<EnumFacing> getConduitConnections();

  /**
   * Checks if the conduit has a connection in the direction given
   *
   * @param dir
   *          direction to check for connection
   * @return true if the conduit has a connection in the given direction
   */
  default boolean containsConduitConnection(@Nonnull EnumFacing dir) {
    return getConduitConnections().contains(dir);
  }

  // External Connections

  /**
   * Checks if the conduit can connect to non-conduit blocks
   *
   * @param direction
   *          direction of the non-conduit block
   * @param ignoreConnectionMode
   *          true if the conduit should connect regardless of the block
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
   * @param dir
   *          the direction to check for an external connection
   * @return true if the given direction is an external connection
   */
  default boolean containsExternalConnection(@Nonnull EnumFacing dir) {
    return getExternalConnections().contains(dir);
  }

  /**
   * Checks if the conduit has a connection in the given direction
   *
   * @param dir
   *          the direction to check for a connection
   * @return true if the conduit has a connection in that direction
   */
  default boolean isConnectedTo(@Nonnull EnumFacing dir) {
    return containsConduitConnection(dir) || containsExternalConnection(dir);
  }

  /**
   * Gets the connection mode in the given direction
   *
   * @param dir
   *          the direction of the connection
   * @return the connection mode (IN, OUT, IN_OUT, DISABLED or NONE)
   */
  @Nonnull
  ConnectionMode getConnectionMode(@Nonnull EnumFacing dir);

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

  void updateEntity(@Nonnull World world); // Please, do not tick unless really, really needed!

  @Nonnull
  String getConduitProbeInfo(@Nonnull EntityPlayer player);

}
