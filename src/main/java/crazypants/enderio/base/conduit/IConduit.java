package crazypants.enderio.base.conduit;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IConduit {

  // stuff that would be on the class, not the object if there were interfaces for classes...

  @SideOnly(Side.CLIENT)
  @Nonnull
  ITabPanel createGuiPanel(@Nonnull Object gui); // TODO 1.11 IGuiExternalConnection

  @SideOnly(Side.CLIENT)
  int getGuiPanelTabOrder();

  public @Nonnull IConduitNetwork<?, ?> createNetworkForType();

  // Base functionality
  @Nonnull
  Class<? extends IConduit> getBaseConduitType();

  @Nonnull
  ItemStack createItem();

  @Nonnull
  NNList<ItemStack> getDrops();

  default int getLightValue() {
    return 0;
  }

  default int getExternalRedstoneLevel() {
    return 0;
  }

  boolean isActive();

  void setActive(boolean active);

  void writeToNBT(@Nonnull NBTTagCompound conduitBody);

  void readFromNBT(@Nonnull NBTTagCompound conduitBody);

  // Container

  void setBundle(@Nonnull IConduitBundle tileConduitBundle);

  @Nonnull
  IConduitBundle getBundle() throws NullPointerException;

  void onAddedToBundle();

  void onRemovedFromBundle();

  // Connections

  boolean hasConnections();

  boolean hasExternalConnections();

  boolean hasConduitConnections();

  // Conduit Connections

  boolean canConnectToConduit(@Nonnull EnumFacing direction, @Nonnull IConduit conduit);

  @Nonnull
  Set<EnumFacing> getConduitConnections();

  default boolean containsConduitConnection(@Nonnull EnumFacing dir) {
    return getConduitConnections().contains(dir);
  }

  void conduitConnectionAdded(@Nonnull EnumFacing fromDirection);

  void conduitConnectionRemoved(@Nonnull EnumFacing fromDirection);

  void connectionsChanged();

  @Nonnull
  IConduitNetwork<?, ?> getNetwork() throws NullPointerException;

  boolean setNetwork(@Nonnull IConduitNetwork<?, ?> network); // TODO: is setNetwork(null) used to clear the network?

  // External Connections

  boolean canConnectToExternal(@Nonnull EnumFacing direction, boolean ignoreConnectionMode);

  @Nonnull
  Set<EnumFacing> getExternalConnections();

  default boolean containsExternalConnection(@Nonnull EnumFacing dir) {
    return getExternalConnections().contains(dir);
  }

  void externalConnectionAdded(@Nonnull EnumFacing fromDirection);

  void externalConnectionRemoved(@Nonnull EnumFacing fromDirection);

  boolean isConnectedTo(@Nonnull EnumFacing dir); // TODO: what does this do?

  @Nonnull
  ConnectionMode getConnectionMode(@Nonnull EnumFacing dir);

  void setConnectionMode(@Nonnull EnumFacing dir, @Nonnull ConnectionMode mode);

  boolean supportsConnectionMode(@Nonnull ConnectionMode mode);

  @Nonnull
  default ConnectionMode getNextConnectionMode(@Nonnull EnumFacing dir) {
    return NNList.of(ConnectionMode.class).next(getConnectionMode(dir));
  }

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
