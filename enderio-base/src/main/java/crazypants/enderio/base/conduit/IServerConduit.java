package crazypants.enderio.base.conduit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.logging.log4j.util.Strings;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public interface IServerConduit extends IConduit, ICapabilityProvider {

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
   * Tells the conduit that it has been part of an unsuccessful attempt to form a network. It is recommended that the conduit waits a good amount of time before
   * trying to form a network again.
   * <p>
   * This may be called while a half-formed network is still set.
   */
  void setNetworkBuildFailed();

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

  @Override
  default boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
    return false;
  }

  @Override
  default @Nullable <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
    return null;
  }

  default @Nonnull NNList<ITextComponent> getConduitProbeInformation(@Nonnull EntityPlayer player) {
    String info = getConduitProbeInfo(player);
    if (Strings.isBlank(info)) {
      return NNList.emptyList();
    }
    NNList<ITextComponent> result = new NNList<>();
    for (String s : info.split("\n")) {
      result.add(new TextComponentString(NullHelper.first(s, "")));
    }
    return result;
  }

}
