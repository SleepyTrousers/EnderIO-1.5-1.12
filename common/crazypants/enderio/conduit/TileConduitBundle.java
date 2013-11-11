package crazypants.enderio.conduit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.PacketHandler;
import crazypants.enderio.conduit.geom.CollidableCache;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.geom.ConduitConnectorType;
import crazypants.enderio.conduit.geom.Offset;
import crazypants.enderio.conduit.geom.Offsets;
import crazypants.enderio.conduit.geom.Offsets.Axis;
import crazypants.enderio.conduit.liquid.ILiquidConduit;
import crazypants.enderio.conduit.power.IPowerConduit;
import crazypants.enderio.conduit.redstone.InsulatedRedstoneConduit;
import crazypants.render.BoundingBox;
import crazypants.util.BlockCoord;

public class TileConduitBundle extends TileEntity implements IConduitBundle {

  private final List<IConduit> conduits = new ArrayList<IConduit>();

  private int facadeId = -1;
  private int facadeMeta = 0;

  private boolean facadeChanged;

  private final List<CollidableComponent> cachedCollidables = new ArrayList<CollidableComponent>();

  private final List<CollidableComponent> cachedConnectors = new ArrayList<CollidableComponent>();

  private boolean conduitsDirty = true;
  private boolean collidablesDirty = true;
  private boolean connectorsDirty = true;

  private int lightOpacity = 0;

  @SideOnly(Side.CLIENT)
  private FacadeRenderState facadeRenderAs;

  public TileConduitBundle() {
    blockType = EnderIO.blockConduitBundle;
  }

  @Override
  public void dirty() {
    conduitsDirty = true;
    collidablesDirty = true;
  }

  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {
    super.writeToNBT(nbtRoot);

    NBTTagList conduitTags = new NBTTagList();
    for (IConduit conduit : conduits) {
      NBTTagCompound conduitRoot = new NBTTagCompound();
      ConduitUtil.writeToNBT(conduit, conduitRoot);
      conduitTags.appendTag(conduitRoot);
    }
    nbtRoot.setTag("conduits", conduitTags);
    nbtRoot.setInteger("facadeId", facadeId);
    nbtRoot.setInteger("facadeMeta", facadeMeta);
  }

  @Override
  public void readFromNBT(NBTTagCompound nbtRoot) {
    super.readFromNBT(nbtRoot);

    conduits.clear();
    NBTTagList conduitTags = nbtRoot.getTagList("conduits");
    for (int i = 0; i < conduitTags.tagCount(); i++) {
      NBTTagCompound conduitTag = (NBTTagCompound) conduitTags.tagAt(i);
      IConduit conduit = ConduitUtil.readConduitFromNBT(conduitTag);
      if(conduit != null) {
        conduit.setBundle(this);
        conduits.add(conduit);
      }
    }
    facadeId = nbtRoot.getInteger("facadeId");
    facadeMeta = nbtRoot.getInteger("facadeMeta");

  }

  @Override
  public boolean hasFacade() {
    return facadeId > 0;
  }

  @Override
  public void setFacadeId(int blockID, boolean triggerUpdate) {
    this.facadeId = blockID;
    if(triggerUpdate) {
      facadeChanged = true;
    }
  }

  @Override
  public void setFacadeId(int blockID) {
    setFacadeId(blockID, true);
  }

  @Override
  public int getFacadeId() {
    return facadeId;
  }

  @Override
  public void setFacadeMetadata(int meta) {
    facadeMeta = meta;
  }

  @Override
  public int getFacadeMetadata() {
    return facadeMeta;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public FacadeRenderState getFacadeRenderedAs() {
    if(facadeRenderAs == null) {
      facadeRenderAs = FacadeRenderState.NONE;
    }
    return facadeRenderAs;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void setFacadeRenderAs(FacadeRenderState state) {
    this.facadeRenderAs = state;
  }

  @Override
  public int getLightOpacity() {
    return lightOpacity;
  }

  @Override
  public void setLightOpacity(int opacity) {
    lightOpacity = opacity;
  }

  @Override
  public Packet getDescriptionPacket() {
    return PacketHandler.getPacket(this);
  }

  @Override
  public void onChunkUnload() {
    for (IConduit conduit : conduits) {
      conduit.onChunkUnload(worldObj);
    }
  }

  @Override
  public void updateEntity() {
    for (IConduit conduit : conduits) {
      conduit.updateEntity(worldObj);
    }

    if(worldObj != null && !worldObj.isRemote && conduitsDirty) {
      worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
      conduitsDirty = false;
    }

    if(worldObj != null && facadeChanged) {
      worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
      facadeChanged = false;
    }
  }

  public BlockCoord getLocation() {
    return new BlockCoord(xCoord, yCoord, zCoord);
  }

  @Override
  public void onNeighborBlockChange(int blockId) {
    boolean needsUpdate = false;
    for (IConduit conduit : conduits) {
      needsUpdate |= conduit.onNeighborBlockChange(blockId);
    }
    if(needsUpdate) {
      dirty();
    }
  }

  @Override
  public TileConduitBundle getEntity() {
    return this;
  }

  @Override
  public boolean hasType(Class<? extends IConduit> type) {
    return getConduit(type) != null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends IConduit> T getConduit(Class<T> type) {
    if(type == null) {
      return null;
    }
    for (IConduit conduit : conduits) {
      if(type.isInstance(conduit)) {
        return (T) conduit;
      }
    }
    return null;
  }

  @Override
  public void addConduit(IConduit conduit) {
    if(worldObj.isRemote) {
      return;
    }
    conduits.add(conduit);
    conduit.setBundle(this);
    conduit.onAddedToBundle();
    dirty();
  }

  @Override
  public void removeConduit(IConduit conduit) {
    if(conduit != null) {
      removeConduit(conduit, true);
    }
  }

  public void removeConduit(IConduit conduit, boolean notify) {
    if(worldObj.isRemote) {
      return;
    }
    conduit.onRemovedFromBundle();
    conduits.remove(conduit);
    conduit.setBundle(null);
    if(notify) {
      dirty();
    }
  }

  @Override
  public void onBlockRemoved() {
    if(worldObj.isRemote) {
      return;
    }
    List<IConduit> copy = new ArrayList<IConduit>(conduits);
    for (IConduit con : copy) {
      removeConduit(con, false);
    }
    dirty();
  }

  @Override
  public Collection<IConduit> getConduits() {
    return conduits;
  }

  @Override
  public Set<ForgeDirection> getConnections(Class<? extends IConduit> type) {
    IConduit con = getConduit(type);
    if(con != null) {
      return con.getConduitConnections();
    }
    return null;
  }

  @Override
  public boolean containsConnection(Class<? extends IConduit> type, ForgeDirection dir) {
    IConduit con = getConduit(type);
    if(con != null) {
      return con.containsConduitConnection(dir);
    }
    return false;
  }

  @Override
  public boolean containsConnection(ForgeDirection dir) {
    for (IConduit con : conduits) {
      if(con.containsConduitConnection(dir)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Set<ForgeDirection> getAllConnections() {
    Set<ForgeDirection> result = new HashSet<ForgeDirection>();
    for (IConduit con : conduits) {
      result.addAll(con.getConduitConnections());
    }
    return result;
  }

  // Geometry

  @Override
  public Offset getOffset(Class<? extends IConduit> type, ForgeDirection dir) {
    if(getConnectionCount(dir) < 2) {
      return Offset.NONE;
    }
    return Offsets.get(type, dir);
  }

  @Override
  public List<CollidableComponent> getCollidableComponents() {

    for (IConduit con : conduits) {
      collidablesDirty = collidablesDirty || con.haveCollidablesChangedSinceLastCall();
    }
    if(collidablesDirty) {
      connectorsDirty = true;
    }
    if(!collidablesDirty && !cachedCollidables.isEmpty()) {
      return cachedCollidables;
    }
    cachedCollidables.clear();
    for (IConduit conduit : conduits) {
      cachedCollidables.addAll(conduit.getCollidableComponents());
    }

    addConnectors(cachedCollidables);

    collidablesDirty = false;

    return cachedCollidables;
  }

  @Override
  public List<CollidableComponent> getConnectors() {
    List<CollidableComponent> result = new ArrayList<CollidableComponent>();
    addConnectors(result);
    return result;
  }

  private void addConnectors(List<CollidableComponent> result) {

    if(conduits.isEmpty()) {
      return;
    }

    for (IConduit con : conduits) {
      boolean b = con.haveCollidablesChangedSinceLastCall();
      collidablesDirty = collidablesDirty || b;
      connectorsDirty = connectorsDirty || b;
    }

    if(!connectorsDirty && !cachedConnectors.isEmpty()) {
      result.addAll(cachedConnectors);
      return;
    }

    cachedConnectors.clear();

    List<CollidableComponent> coreBounds = new ArrayList<CollidableComponent>();
    for (IConduit con : conduits) {
      addConduitCores(coreBounds, con);
    }
    cachedConnectors.addAll(coreBounds);
    result.addAll(coreBounds);

    List<CollidableComponent> conduitsBounds = new ArrayList<CollidableComponent>();
    for (IConduit con : conduits) {
      conduitsBounds.addAll(con.getCollidableComponents());
      addConduitCores(conduitsBounds, con);
    }

    Set<Class<IConduit>> collidingTypes = new HashSet<Class<IConduit>>();
    for (CollidableComponent conCC : conduitsBounds) {
      for (CollidableComponent innerCC : conduitsBounds) {
        if(!InsulatedRedstoneConduit.COLOR_CONTROLLER_ID.equals(innerCC.data) && !InsulatedRedstoneConduit.COLOR_CONTROLLER_ID.equals(conCC.data)
            && conCC != innerCC && conCC.bound.intersects(innerCC.bound)) {
          collidingTypes.add((Class<IConduit>) conCC.conduitType);
        }
      }
    }

    //TODO: Remove the core geometries covered up by this as no point in rendering these
    if(!collidingTypes.isEmpty()) {
      List<CollidableComponent> colCores = new ArrayList<CollidableComponent>();
      for (Class<IConduit> c : collidingTypes) {
        IConduit con = getConduit(c);
        if(con != null) {
          addConduitCores(colCores, con);
        }
      }

      BoundingBox bb = null;
      for (CollidableComponent cBB : colCores) {
        if(bb == null) {
          bb = cBB.bound;
        } else {
          bb = bb.expandBy(cBB.bound);
        }
      }
      if(bb != null) {
        bb = bb.scale(1.05, 1.05, 1.05);
        CollidableComponent cc = new CollidableComponent(null, bb, ForgeDirection.UNKNOWN,
            ConduitConnectorType.BOTH);
        result.add(cc);
        cachedConnectors.add(cc);
      }
    }

    connectorsDirty = false;

  }

  private boolean axisOfConnectionsEqual(Set<ForgeDirection> cons) {
    Axis axis = null;
    for (ForgeDirection dir : cons) {
      if(axis == null) {
        axis = Offsets.getAxisForDir(dir);
      } else {
        if(axis != Offsets.getAxisForDir(dir)) {
          return false;
        }
      }
    }
    return true;
  }

  private void addConduitCores(List<CollidableComponent> result, IConduit con) {
    CollidableCache cc = CollidableCache.instance;
    Class<? extends IConduit> type = con.getCollidableType();
    if(con.hasConnections()) {
      for (ForgeDirection dir : con.getExternalConnections()) {
        result.addAll(cc.getCollidables(cc.createKey(type, getOffset(con.getBaseConduitType(), dir), ForgeDirection.UNKNOWN, false), con));
      }
      for (ForgeDirection dir : con.getConduitConnections()) {
        result.addAll(cc.getCollidables(cc.createKey(type, getOffset(con.getBaseConduitType(), dir), ForgeDirection.UNKNOWN, false), con));
      }
    } else {
      result.addAll(cc.getCollidables(cc.createKey(type, getOffset(con.getBaseConduitType(), ForgeDirection.UNKNOWN), ForgeDirection.UNKNOWN, false), con));
    }
  }

  private boolean containsOnlySingleVerticalConnections() {
    return getConnectionCount(ForgeDirection.UP) < 2 && getConnectionCount(ForgeDirection.DOWN) < 2;
  }

  private boolean containsOnlySingleHorizontalConnections() {
    return getConnectionCount(ForgeDirection.WEST) < 2 && getConnectionCount(ForgeDirection.EAST) < 2 &&
        getConnectionCount(ForgeDirection.NORTH) < 2 && getConnectionCount(ForgeDirection.SOUTH) < 2;
  }

  private boolean allDirectionsHaveSameConnectionCount() {
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      boolean hasCon = conduits.get(0).isConnectedTo(dir);
      for (int i = 1; i < conduits.size(); i++) {
        if(hasCon != conduits.get(i).isConnectedTo(dir)) {
          return false;
        }
      }
    }
    return true;
  }

  private boolean containsOnlyHorizontalConnections() {
    for (IConduit con : conduits) {
      for (ForgeDirection dir : con.getConduitConnections()) {
        if(dir == ForgeDirection.UP || dir == ForgeDirection.DOWN) {
          return false;
        }
      }
      for (ForgeDirection dir : con.getExternalConnections()) {
        if(dir == ForgeDirection.UP || dir == ForgeDirection.DOWN) {
          return false;
        }
      }
    }
    return true;
  }

  private int getConnectionCount(ForgeDirection dir) {
    if(dir == ForgeDirection.UNKNOWN) {
      return conduits.size();
    }
    int result = 0;
    for (IConduit con : conduits) {
      if(con.containsConduitConnection(dir) || con.containsExternalConnection(dir)) {
        result++;
      }
    }
    return result;
  }

  // ------------ Power -----------------------------

  @Override
  public void doWork(PowerHandler workProvider) {
    IPowerConduit pc = getConduit(IPowerConduit.class);
    if(pc != null) {
      pc.doWork(workProvider);
    }
  }

  @Override
  public PowerReceiver getPowerReceiver(ForgeDirection side) {
    IPowerConduit pc = getConduit(IPowerConduit.class);
    if(pc != null) {
      return pc.getPowerReceiver(side);
    }
    return null;
  }

  @Override
  public PowerHandler getPowerHandler() {
    IPowerConduit pc = getConduit(IPowerConduit.class);
    if(pc != null) {
      return pc.getPowerHandler();
    }
    return null;
  }

  @Override
  public void applyPerdition() {
    IPowerConduit pc = getConduit(IPowerConduit.class);
    if(pc != null) {
      pc.applyPerdition();
    }

  }

  @Override
  public World getWorld() {
    return worldObj;
  }

  // ------- Liquids -----------------------------

  @Override
  public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
    ILiquidConduit lc = getConduit(ILiquidConduit.class);
    if(lc != null) {
      return lc.fill(from, resource, doFill);
    }
    return 0;
  }

  @Override
  public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
    ILiquidConduit lc = getConduit(ILiquidConduit.class);
    if(lc != null) {
      return lc.drain(from, resource, doDrain);
    }
    return null;
  }

  @Override
  public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
    ILiquidConduit lc = getConduit(ILiquidConduit.class);
    if(lc != null) {
      return lc.drain(from, maxDrain, doDrain);
    }
    return null;
  }

  @Override
  public boolean canFill(ForgeDirection from, Fluid fluid) {
    ILiquidConduit lc = getConduit(ILiquidConduit.class);
    if(lc != null) {
      return lc.canFill(from, fluid);
    }
    return false;
  }

  @Override
  public boolean canDrain(ForgeDirection from, Fluid fluid) {
    ILiquidConduit lc = getConduit(ILiquidConduit.class);
    if(lc != null) {
      return lc.canDrain(from, fluid);
    }
    return false;
  }

  @Override
  public FluidTankInfo[] getTankInfo(ForgeDirection from) {
    ILiquidConduit lc = getConduit(ILiquidConduit.class);
    if(lc != null) {
      return lc.getTankInfo(from);
    }
    return null;
  }

}
