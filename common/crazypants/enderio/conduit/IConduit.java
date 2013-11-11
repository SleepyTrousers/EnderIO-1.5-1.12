package crazypants.enderio.conduit;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import crazypants.enderio.conduit.geom.CollidableCache.CacheKey;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.util.BlockCoord;

public interface IConduit {

  // Base functionality
  Class<? extends IConduit> getBaseConduitType();

  ItemStack createItem();

  int getLightValue();

  boolean isActive();

  void setActive(boolean active);

  void writeToNBT(NBTTagCompound conduitBody);

  void readFromNBT(NBTTagCompound conduitBody);

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

  AbstractConduitNetwork<?> getNetwork();

  boolean setNetwork(AbstractConduitNetwork<?> network);

  // External Connections

  boolean canConnectToExternal(ForgeDirection direction);

  Set<ForgeDirection> getExternalConnections();

  boolean containsExternalConnection(ForgeDirection dir);

  void externalConnectionAdded(ForgeDirection fromDirection);

  void externalConnectionRemoved(ForgeDirection fromDirection);

  boolean isConnectedTo(ForgeDirection dir);

  ConnectionMode getConectionMode(ForgeDirection dir);

  void setConnectionMode(ForgeDirection dir, ConnectionMode mode);

  boolean hasConnectionMode(ConnectionMode mode);

  // rendering, only needed us default rendering is used

  Icon getTextureForState(CollidableComponent component);

  Icon getTransmitionTextureForState(CollidableComponent component);

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

  boolean onNeighborBlockChange(int blockId);

}
