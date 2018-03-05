package crazypants.enderio.base.conduit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public interface IServerConduit extends IConduit {

  /**
   * Creates a conduit network for the given type of conduit
   *
   * @return the network for the given conduit type
   */
  @Nonnull
  IConduitNetwork<?, ?> createNetworkForType();

  default int getExternalRedstoneLevel() {
    return 0;
  }

  /**
   * Sets the conduit's state to active. Called when the conduit is operating
   *
   * @param active
   *          true if the conduit is currently doing something
   */
  void setActive(boolean active);

  void writeToNBT(@Nonnull NBTTagCompound conduitBody);

  void onAddedToBundle();

  default void onBeforeRemovedFromBundle() {
  }

  void onAfterRemovedFromBundle();

  /**
   * Called when a conduit has a connection added
   *
   * @param fromDirection
   *          the direction of the connection
   */
  void conduitConnectionAdded(@Nonnull EnumFacing fromDirection);

  /**
   * Called when a conduit has a connection removed
   *
   * @param fromDirection
   *          the direction that the connection was removed from
   */
  void conduitConnectionRemoved(@Nonnull EnumFacing fromDirection);

  /**
   * Called when conduit connections are changed
   */
  void connectionsChanged();

  /**
   * @return the network of the conduit
   */
  @Nullable
  IConduitNetwork<?, ?> getNetwork() throws NullPointerException;

  /**
   * Sets the network of this conduit to a new network. Called when the conduit is connected to a new network
   *
   * @param network
   *          the network to make the conduit a part of
   * @return true if a new network is successfully set
   */
  boolean setNetwork(@Nonnull IConduitNetwork<?, ?> network);

  /**
   * Unsets the network of this conduit.
   */
  void clearNetwork();

  /**
   * Called when a connection to a non-conduit block is added
   *
   * @param fromDirection
   *          the direction of the connection
   */
  void externalConnectionAdded(@Nonnull EnumFacing fromDirection);

  /**
   * Called whan a connection to a non-conduit block is removed
   *
   * @param fromDirection
   *          the direction of the connection
   */
  void externalConnectionRemoved(@Nonnull EnumFacing fromDirection);

  /**
   * Sets the connection mode of the conduit in the given direction
   *
   * @param dir
   *          direction of the connection
   * @param mode
   *          the connection mode (IN, OUT, IN_OUT, DISABLED or NONE)
   */
  void setConnectionMode(@Nonnull EnumFacing dir, @Nonnull ConnectionMode mode);

  /**
   * Checks if this conduit can use the given connection mode
   *
   * @param mode
   *          connection mode (IN, OUT, IN_OUT, DISABLED or NONE) to check
   * @return true if the conduit can use the given connection mode
   */
  boolean supportsConnectionMode(@Nonnull ConnectionMode mode);

  /**
   * Gets the next connection mode in the cycle
   *
   * @param dir
   *          the direction of the connection for getting its connection mode
   * @return next connection mode in the list
   */
  @Nonnull
  default ConnectionMode getNextConnectionMode(@Nonnull EnumFacing dir) {
    return NNList.of(ConnectionMode.class).next(getConnectionMode(dir));
  }

  /**
   * Gets the previous connection mode in the cycle
   *
   * @param dir
   *          the direction of the connection for getting its connection mode
   * @return previous connection mode in the list
   */
  @Nonnull
  default ConnectionMode getPreviousConnectionMode(@Nonnull EnumFacing dir) {
    return NNList.of(ConnectionMode.class).prev(getConnectionMode(dir));
  }

  /**
   * Determines if this conduit can connect to another conduit.
   *
   * @param direction
   *          direction of the conduit to connect to
   * @param conduit
   *          conduit to connect to
   * @return true if the conduit can connect in that direction and if the conduit is of the same or another valid type
   */
  boolean canConnectToConduit(@Nonnull EnumFacing direction, @Nonnull IConduit conduit);

  void onChunkUnload();

  boolean onNeighborBlockChange(@Nonnull Block block);

  boolean onNeighborChange(@Nonnull BlockPos neighbourPos);

  void invalidate();

  // For Copy/Paste of connection settings

  boolean writeConnectionSettingsToNBT(@Nonnull EnumFacing dir, @Nonnull NBTTagCompound nbt);

  boolean readConduitSettingsFromNBT(@Nonnull EnumFacing dir, @Nonnull NBTTagCompound nbt);
}
