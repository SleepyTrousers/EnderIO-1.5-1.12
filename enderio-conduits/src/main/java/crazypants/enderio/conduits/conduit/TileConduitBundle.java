package crazypants.enderio.conduits.conduit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.util.DyeColor;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.TileEntityEio;
import crazypants.enderio.base.conduit.ConduitDisplayMode;
import crazypants.enderio.base.conduit.ConduitUtil;
import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.base.conduit.IServerConduit;
import crazypants.enderio.base.conduit.facade.EnumFacadeType;
import crazypants.enderio.base.conduit.geom.CollidableCache;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.base.conduit.geom.ConduitConnectorType;
import crazypants.enderio.base.conduit.geom.ConduitGeometryUtil;
import crazypants.enderio.base.conduit.geom.Offset;
import crazypants.enderio.base.conduit.geom.Offsets;
import crazypants.enderio.base.conduit.registry.ConduitRegistry;
import crazypants.enderio.base.diagnostics.Prof;
import crazypants.enderio.base.filter.IFilterHolder;
import crazypants.enderio.base.filter.IItemFilter;
import crazypants.enderio.base.filter.gui.FilterGuiUtil;
import crazypants.enderio.base.paint.YetaUtil;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.conduits.conduit.item.ItemConduit;
import crazypants.enderio.conduits.conduit.item.ItemConduitNetwork;
import crazypants.enderio.conduits.conduit.redstone.InsulatedRedstoneConduit;
import crazypants.enderio.conduits.config.ConduitConfig;
import crazypants.enderio.conduits.render.BlockStateWrapperConduitBundle;
import crazypants.enderio.conduits.render.BlockStateWrapperConduitBundle.ConduitCacheKey;
import crazypants.enderio.conduits.render.ConduitRenderMapper;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;

@Storable
public class TileConduitBundle extends TileEntityEio implements IConduitBundle, IConduitComponent.IConduitComponentProvider, IFilterHolder {

  // TODO Fix duct-tape
  // TODO Check store
  @Store(handler = ConduitHandler.ConduitCopyOnWriteArrayListHandler.class)
  private @Nonnull List<IConduit> conduits = new CopyOnWriteArrayList<IConduit>(); // <- duct-tape fix

  /*
   * ^ this one is written to nbt and read from nbt
   * 
   * -> (and this is duct-tape on duct-tape)
   * 
   * v this one is the one we work with
   */

  private final List<IServerConduit> serverConduits = new CopyOnWriteArrayList<>();
  private List<IClientConduit> clientConduits;

  @Store
  private @Nonnull EnumFacadeType facadeType = EnumFacadeType.BASIC;

  private final List<CollidableComponent> cachedCollidables = new CopyOnWriteArrayList<CollidableComponent>(); // <- duct-tape fix

  private final List<CollidableComponent> cachedConnectors = new CopyOnWriteArrayList<CollidableComponent>(); // <- duct-tape fix

  private boolean conduitsDirty = true;
  private boolean collidablesDirty = true;
  private boolean connectorsDirty = true;

  private boolean clientUpdated = false;

  private int lightOpacityOverride = -1;

  @SideOnly(Side.CLIENT)
  private @Nullable FacadeRenderState facadeRenderAs;

  private ConduitDisplayMode lastMode = ConduitDisplayMode.ALL;

  public TileConduitBundle() {
    this.blockType = ConduitRegistry.getConduitModObjectNN().getBlockNN();
  }

  @Nonnull
  @Override
  public BlockPos getLocation() {
    return getPos();
  }

  @Override
  public void dirty() {
    conduitsDirty = true;
    collidablesDirty = true;
  }

  @Override
  public boolean shouldRenderInPass(int arg0) {
    if (getPaintSource() != null && getPaintSource().isOpaqueCube() && !YetaUtil.isFacadeHidden(this, EnderIO.proxy.getClientPlayer())) {
      return false;
    }
    return super.shouldRenderInPass(arg0);
  }

  @Override
  public @Nonnull World getBundleworld() {
    return getWorld();
  }

  @Override
  public int getInternalRedstoneSignalForColor(@Nonnull DyeColor col) {
    return 0;
  }

  @Override
  public boolean handleFacadeClick(World world1, BlockPos placeAt, EntityPlayer player, EnumFacing opposite, ItemStack stack, EnumHand hand, float hitX,
      float hitY, float hitZ) {
    // TODO make this more useful
    return false;
  }

  // TODO Make each conduit use its own probe data
  @Nonnull
  @Override
  public String[] getConduitProbeData(@Nonnull EntityPlayer player, @Nullable EnumFacing side) {
    return new String[0];
  }

  @Override
  protected void onBeforeNbtWrite() {
    conduits = serverConduits != null ? new CopyOnWriteArrayList<>(serverConduits) : new CopyOnWriteArrayList<>();
  }

  @Override
  protected void onAfterNbtRead() {
    super.onAfterNbtRead();
    if (world.isRemote) {
      ConduitRegistry.sort(conduits); // keep conduits sorted so the client side cache key is stable
      CopyOnWriteArrayList<IClientConduit> temp = new CopyOnWriteArrayList<>();
      for (IConduit c : conduits) {
        if (c instanceof IClientConduit) {
          c.setBundle(this);
          temp.add((IClientConduit) c);
        }
      }
      final ConduitCacheKey oldHashCode = new ConduitCacheKey(), newHashCode = new ConduitCacheKey();
      makeConduitHashCode(getClientConduits(), oldHashCode);
      makeConduitHashCode(temp, newHashCode);
      if (hasWorld() && getWorld().isRemote && oldHashCode.hashCode() != newHashCode.hashCode()) {
        clientUpdated = true;
      }
      clientConduits = temp; // switch over atomically to avoid threading issues
      conduits = new CopyOnWriteArrayList<IConduit>();
    } else {
      // no threads on server-side. but to be safe, conduits only go into the list after they got a bundle set
      // (a.k.a. "do better than World.addTileEntities()"
      serverConduits.clear();
      for (IConduit c : conduits) {
        if (c instanceof IServerConduit) {
          c.setBundle(this);
          serverConduits.add((IServerConduit) c);
        }
      }
      conduits.clear();
    }
    cachedCollidables.clear();
  }

  @Override
  public boolean hasFacade() {
    return getPaintSource() != null;
  }

  @Override
  public void setPaintSource(@Nullable IBlockState paintSource) {
    super.setPaintSource(paintSource);
    // force re-calc of lighting for both client and server
    IBlockState bs = world.getBlockState(pos);
    IBlockState newBs = bs.withProperty(BlockConduitBundle.OPAQUE, getLightOpacity() > 0);
    if (bs == newBs) {
      world.setBlockState(getPos(), newBs.cycleProperty(BlockConduitBundle.OPAQUE));
    }
    world.setBlockState(getPos(), newBs);
    forceUpdatePlayers(); // send the packet now so the re-render we just triggered will render with the new data
  }

  @Override
  public void setFacadeType(@Nonnull EnumFacadeType type) {
    facadeType = type;
    markDirty();
  }

  @Override
  @Nonnull
  public EnumFacadeType getFacadeType() {
    return facadeType;
  }

  @Override
  @SideOnly(Side.CLIENT)
  @Nonnull
  public FacadeRenderState getFacadeRenderedAs() {
    FacadeRenderState ret = facadeRenderAs;
    if (ret == null) {
      ret = facadeRenderAs = FacadeRenderState.NONE;
    }
    return ret;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void setFacadeRenderAs(FacadeRenderState state) {
    this.facadeRenderAs = state;
  }

  @SuppressWarnings("deprecation")
  @Override
  public int getLightOpacity() {
    if (world.isRemote && lightOpacityOverride != -1) {
      return lightOpacityOverride;
    }
    final IBlockState paintSource = getPaintSource();
    if (paintSource != null) {
      if (getFacadeType().isTransparent() && ConduitConfig.transparentFacadesLetThroughBeaconBeam.get()) {
        return Math.min(paintSource.getLightOpacity(), 14);
      } else {
        return paintSource.getLightOpacity();
      }
    } else {
      return 0;
    }
  }

  @Override
  public void setLightOpacityOverride(int opacity) {
    lightOpacityOverride = opacity;
  }

  @Override
  public void onChunkUnload() {
    if (!world.isRemote) {
      for (IServerConduit conduit : getServerConduits()) {
        conduit.onChunkUnload();
      }
    }
  }

  @Override
  public void doUpdate() {
    Prof.start(getWorld(), "tick");

    for (IConduit conduit : getConduits()) {
      Prof.next(getWorld(), "", conduit);
      conduit.updateEntity(world);
    }

    if (!world.isRemote && conduitsDirty) {
      Prof.next(getWorld(), "neighborUpdate");
      doConduitsDirty();
    }

    // client side only, check for changes in rendering of the bundle
    if (world.isRemote) {
      Prof.next(getWorld(), "clientTick");
      updateEntityClient();
    }

    Prof.stop(getWorld());
  }

  private void doConduitsDirty() {
    IBlockState bs = world.getBlockState(pos);
    world.notifyBlockUpdate(pos, bs, bs, 3);
    world.neighborChanged(pos, getBlockType(), pos);
    markDirty();
    conduitsDirty = false;
  }

  private void updateEntityClient() {
    boolean markForUpdate = false;
    if (clientUpdated) {
      // TODO: This is not the correct solution here but just marking the block for a render update server side
      // seems to get out of sync with the client sometimes so connections are not rendered correctly
      markForUpdate = true;
      clientUpdated = false;
    }

    FacadeRenderState curRS = getFacadeRenderedAs();
    FacadeRenderState rs = ConduitUtil.getRequiredFacadeRenderState(this, NullHelper.notnull(EnderIO.proxy.getClientPlayer(), "Proxy#getClientPlayer"));

    if (ConduitConfig.updateLightingWhenHidingFacades.get()) {
      int shouldBeLO = rs == FacadeRenderState.FULL ? -1 : 0;
      if (lightOpacityOverride != shouldBeLO) {
        setLightOpacityOverride(shouldBeLO);
        world.checkLight(getPos());
      }
    }

    if (curRS != rs) {
      setFacadeRenderAs(rs);
      if (!ConduitUtil.forceSkylightRecalculation(world, getPos())) {
        markForUpdate = true;
      }
    }
    ConduitDisplayMode curMode = ConduitDisplayMode.getDisplayMode(EnderIO.proxy.getClientPlayer().getHeldItemMainhand());
    if (curMode != lastMode && !(lastMode.isAll() && curMode.isAll())) {
      markForUpdate = true;
    }
    lastMode = curMode;

    if (markForUpdate) {
      updateBlock();
    }
  }

  @Override
  public void onNeighborBlockChange(@Nonnull Block blockId) {
    if (!world.isRemote) {
      boolean needsUpdate = false;
      for (IServerConduit conduit : getServerConduits()) {
        needsUpdate |= conduit.onNeighborBlockChange(blockId);
      }
      if (needsUpdate) {
        dirty();
      }
    }
  }

  @Override
  public void onNeighborChange(@Nonnull IBlockAccess worldIn, @Nonnull BlockPos posIn, @Nonnull BlockPos neighbor) {
    if (!world.isRemote) {
      boolean needsUpdate = false;
      for (IServerConduit conduit : getServerConduits()) {
        needsUpdate |= conduit.onNeighborChange(neighbor);
      }
      if (needsUpdate) {
        dirty();
      }
    }
  }

  @Override
  @Nonnull
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
    if (type == null) {
      return null;
    }
    for (IConduit conduit : getConduits()) {
      if (type.isInstance(conduit)) {
        return (T) conduit;
      }
    }
    return null;
  }

  @Override
  public void addConduit(IServerConduit conduit) {
    if (world.isRemote) {
      return;
    }
    conduit.setBundle(this);
    getServerConduits().add(conduit);
    conduit.onAddedToBundle();
    dirty();
  }

  @Override
  public void removeConduit(IConduit conduit) {
    if (conduit instanceof IServerConduit) {
      removeConduit((IServerConduit) conduit, true);
    }
  }

  public void removeConduit(IServerConduit conduit, boolean notify) {
    if (world.isRemote) {
      return;
    }
    conduit.onBeforeRemovedFromBundle();
    getServerConduits().remove(conduit);
    conduit.onAfterRemovedFromBundle();
    conduit.setBundle(null);
    if (notify) {
      dirty();
    }
  }

  @Override
  public void onBlockRemoved() {
    if (world.isRemote) {
      return;
    }
    while (!serverConduits.isEmpty()) {
      removeConduit(serverConduits.get(0), false);
    }
    dirty();
  }

  @Override
  public Collection<IServerConduit> getServerConduits() {
    return serverConduits != null ? serverConduits : Collections.emptyList();
  }

  @Override
  public Collection<? extends IConduit> getConduits() {
    return NullHelper.first(world.isRemote ? clientConduits : serverConduits, Collections.emptyList());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public Collection<IClientConduit> getClientConduits() {
    return clientConduits != null ? clientConduits : Collections.emptyList();
  }

  // Geometry

  @Override
  public @Nonnull Offset getOffset(@Nonnull Class<? extends IConduit> type, @Nullable EnumFacing dir) {
    if (getConnectionCount(dir) < 2) {
      return Offset.NONE;
    }
    return Offsets.get(type, dir);
  }

  @Override
  public List<CollidableComponent> getCollidableComponents() {
    for (IConduit con : getConduits()) {
      collidablesDirty = collidablesDirty || con.haveCollidablesChangedSinceLastCall();
    }
    if (collidablesDirty) {
      connectorsDirty = true;
    }
    if (!collidablesDirty && !cachedCollidables.isEmpty()) {
      return cachedCollidables;
    }
    cachedCollidables.clear();
    for (IConduit conduit : getConduits()) {
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

  @SuppressWarnings("unchecked")
  private void addConnectors(List<CollidableComponent> result) {

    if (getConduits().isEmpty()) {
      return;
    }

    for (IConduit con : getConduits()) {
      boolean b = con.haveCollidablesChangedSinceLastCall();
      collidablesDirty = collidablesDirty || b;
      connectorsDirty = connectorsDirty || b;
    }

    if (!connectorsDirty && !cachedConnectors.isEmpty()) {
      result.addAll(cachedConnectors);
      return;
    }

    cachedConnectors.clear();

    // TODO: What an unholly mess! (and it doesn't even work correctly...)
    List<CollidableComponent> coreBounds = new ArrayList<CollidableComponent>();
    for (IConduit con : getConduits()) {
      addConduitCores(coreBounds, con);
    }
    cachedConnectors.addAll(coreBounds);
    result.addAll(coreBounds);

    // 1st algorithm
    List<CollidableComponent> conduitsBounds = new ArrayList<CollidableComponent>();
    for (IConduit con : getConduits()) {
      conduitsBounds.addAll(con.getCollidableComponents());
      addConduitCores(conduitsBounds, con);
    }

    Set<Class<IConduit>> collidingTypes = new HashSet<Class<IConduit>>();
    for (CollidableComponent conCC : conduitsBounds) {
      for (CollidableComponent innerCC : conduitsBounds) {
        if (!InsulatedRedstoneConduit.COLOR_CONTROLLER_ID.equals(innerCC.data) && !InsulatedRedstoneConduit.COLOR_CONTROLLER_ID.equals(conCC.data)
            && conCC != innerCC && conCC.bound.intersects(innerCC.bound)) {
          collidingTypes.add((Class<IConduit>) conCC.conduitType);
        }
      }
    }

    // TODO: Remove the core geometries covered up by this as no point in rendering these
    if (!collidingTypes.isEmpty()) {
      List<CollidableComponent> colCores = new ArrayList<CollidableComponent>();
      for (Class<IConduit> c : collidingTypes) {
        IConduit con = getConduit(c);
        if (con != null) {
          addConduitCores(colCores, con);
        }
      }

      BoundingBox bb = null;
      for (CollidableComponent cBB : colCores) {
        if (bb == null) {
          bb = cBB.bound;
        } else {
          bb = bb.expandBy(cBB.bound);
        }
      }
      if (bb != null) {
        bb = bb.scale(1.05, 1.05, 1.05);
        CollidableComponent cc = new CollidableComponent(null, bb, null, ConduitConnectorType.INTERNAL);
        result.add(cc);
        cachedConnectors.add(cc);
      }
    }

    // 2nd algorithm
    for (IConduit con : getConduits()) {

      if (con.hasConnections()) {
        List<CollidableComponent> cores = new ArrayList<CollidableComponent>();
        addConduitCores(cores, con);
        if (cores.size() > 1) {
          BoundingBox bb = cores.get(0).bound;
          double area = bb.getArea();
          for (CollidableComponent cc : cores) {
            bb = bb.expandBy(cc.bound);
          }
          if (bb.getArea() > area * 1.5f) {
            bb = bb.scale(1.05, 1.05, 1.05);
            CollidableComponent cc = new CollidableComponent(null, bb, null, ConduitConnectorType.INTERNAL);
            result.add(cc);
            cachedConnectors.add(cc);
          }
        }
      }
    }

    // Merge all internal conduit connectors into one box
    BoundingBox conBB = null;
    for (int i = 0; i < result.size(); i++) {
      CollidableComponent cc = result.get(i);
      if (cc.conduitType == null && cc.data == ConduitConnectorType.INTERNAL) {
        conBB = conBB == null ? cc.bound : conBB.expandBy(cc.bound);
        result.remove(i);
        i--;
        cachedConnectors.remove(cc);
      }
    }

    if (conBB != null) {
      CollidableComponent cc = new CollidableComponent(null, conBB, null, ConduitConnectorType.INTERNAL);
      result.add(cc);
      cachedConnectors.add(cc);
    }

    // External Connectors
    EnumSet<EnumFacing> externalDirs = EnumSet.noneOf(EnumFacing.class);
    for (IConduit con : getConduits()) {
      Set<EnumFacing> extCons = con.getExternalConnections();
      for (EnumFacing dir : extCons) {
        if (con.getConnectionMode(NullHelper.notnull(dir, "IConduit#getExternalConnections#iterator#next")) != ConnectionMode.DISABLED) {
          externalDirs.add(dir);
        }
      }
    }
    for (EnumFacing dir : externalDirs) {
      BoundingBox bb = ConduitGeometryUtil.instance.getExternalConnectorBoundingBox(dir);
      if (bb != null) {
        CollidableComponent cc = new CollidableComponent(null, bb, dir, ConduitConnectorType.EXTERNAL);
        result.add(cc);
        cachedConnectors.add(cc);
      }
    }

    connectorsDirty = false;
  }

  private void addConduitCores(List<CollidableComponent> result, IConduit con) {
    CollidableCache cc = CollidableCache.instance;
    Class<? extends IConduit> type = con.getCollidableType();
    if (con.hasConnections()) {
      for (EnumFacing dir : con.getExternalConnections()) {
        result.addAll(cc.getCollidables(cc.createKey(type, getOffset(con.getBaseConduitType(), dir), null, false), con));
      }
      for (EnumFacing dir : con.getConduitConnections()) {
        result.addAll(cc.getCollidables(cc.createKey(type, getOffset(con.getBaseConduitType(), dir), null, false), con));
      }
    } else {
      result.addAll(cc.getCollidables(cc.createKey(type, getOffset(con.getBaseConduitType(), null), null, false), con));
    }
  }

  private int getConnectionCount(@Nullable EnumFacing dir) {
    if (dir == null) {
      return getConduits().size();
    }
    int result = 0;
    for (IConduit con : getConduits()) {
      if (con.containsConduitConnection(dir) || con.containsExternalConnection(dir)) {
        result++;
      }
    }
    return result;
  }

  // ------------ Capabilities ----------------------

  @Override
  public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
    for (IConduit conduit : getServerConduits()) {
      if (conduit.hasCapability(capability, facing))
        return true;
    }
    return super.hasCapability(capability, facing);
  }

  @Nullable
  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
    for (IConduit conduit : getServerConduits()) {
      if (conduit.hasCapability(capability, facing))
        return conduit.getCapability(capability, facing);
    }
    return super.getCapability(capability, facing);
  }

  // // AE2
  //
  // private Object node; // IGridNode object, untyped to avoid crash w/o AE2
  //
  // @Override
  // @Method(modid = "appliedenergistics2")
  // public IGridNode getGridNode(AEPartLocation loc) {
  // IMEConduit cond = getConduit(IMEConduit.class);
  // if (cond != null) {
  // if (loc == null || loc == AEPartLocation.INTERNAL || cond.getConnectionMode(loc.getOpposite().getFacing()) == ConnectionMode.IN_OUT) {
  // return (IGridNode) node;
  // }
  // }
  // return null;
  // }
  //
  // @SuppressWarnings("cast")
  // @Override
  // @Method(modid = "appliedenergistics2")
  // public void setGridNode(Object node) {
  // this.node = (IGridNode) node;
  // }
  //
  // @Override
  // @Method(modid = "appliedenergistics2")
  // public AECableType getCableConnectionType(AEPartLocation loc) {
  // IMEConduit cond = getConduit(IMEConduit.class);
  // if (cond == null || loc == AEPartLocation.INTERNAL) {
  // return AECableType.NONE;
  // } else {
  // return cond.isConnectedTo(loc.getFacing()) ? cond.isDense() ? AECableType.DENSE : AECableType.SMART : AECableType.NONE;
  // }
  // }
  //
  // @Override
  // @Method(modid = "appliedenergistics2")
  // public void securityBreak() {
  // }
  //
  // // OpenComputers
  //
  // @Override
  // @Method(modid = "OpenComputersAPI|Network")
  // public Node node() {
  // IOCConduit cond = getConduit(IOCConduit.class);
  // if (cond != null) {
  // return cond.node();
  // } else {
  // return null;
  // }
  // }
  //
  // @Override
  // @Method(modid = "OpenComputersAPI|Network")
  // public void onConnect(Node node) {
  // IOCConduit cond = getConduit(IOCConduit.class);
  // if (cond != null) {
  // cond.onConnect(node);
  // }
  // }
  //
  // @Override
  // @Method(modid = "OpenComputersAPI|Network")
  // public void onDisconnect(Node node) {
  // IOCConduit cond = getConduit(IOCConduit.class);
  // if (cond != null) {
  // cond.onDisconnect(node);
  // }
  // }
  //
  // @Override
  // @Method(modid = "OpenComputersAPI|Network")
  // public void onMessage(Message message) {
  // IOCConduit cond = getConduit(IOCConduit.class);
  // if (cond != null) {
  // cond.onMessage(message);
  // }
  // }
  //
  // @Override
  // @Method(modid = "OpenComputersAPI|Network")
  // public Node sidedNode(EnumFacing side) {
  // IOCConduit cond = getConduit(IOCConduit.class);
  // if (cond != null) {
  // return cond.sidedNode(side);
  // } else {
  // return null;
  // }
  // }
  //
  // @Override
  // @Method(modid = "OpenComputersAPI|Network")
  // @SideOnly(Side.CLIENT)
  // public boolean canConnect(EnumFacing side) {
  // IOCConduit cond = getConduit(IOCConduit.class);
  // if (cond != null) {
  // return cond.canConnect(side);
  // } else {
  // return false;
  // }
  // }

  @Override
  public void invalidate() {
    super.invalidate();
    if (world.isRemote) {
      return;
    }
    for (IConduit con : getServerConduits()) {
      ((IServerConduit) con).invalidate();
    }
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void hashCodeForModelCaching(IBlockStateWrapper wrapper, BlockStateWrapperConduitBundle.ConduitCacheKey hashCodes) {
    hashCodes.add(facadeType.ordinal() << 16 | getFacadeRenderedAs().ordinal() << 8 | wrapper.getYetaDisplayMode().getDisplayMode().ordinal() << 1
        | (wrapper.getYetaDisplayMode().isHideFacades() ? 1 : 0));
    makeConduitHashCode(getClientConduits(), hashCodes);
  }

  @SideOnly(Side.CLIENT)
  private static void makeConduitHashCode(Collection<? extends IClientConduit> conduits, BlockStateWrapperConduitBundle.ConduitCacheKey hashCodes) {
    for (IConduit conduit : conduits) {
      if (conduit instanceof IConduitComponent) {
        ((IConduitComponent) conduit).hashCodeForModelCaching(hashCodes);
      } else {
        hashCodes.add(conduit);
      }
    }
  }

  @Override
  public String toString() {
    return !hasWorld() ? super.toString() : world.isRemote ? toStringC(this) : toStringS(this);
  }

  @SideOnly(Side.CLIENT)
  public static String toStringC(TileConduitBundle self) {
    BlockStateWrapperConduitBundle bsw = new BlockStateWrapperConduitBundle(self.world.getBlockState(self.pos), self.world, self.pos,
        ConduitRenderMapper.instance);
    bsw.addCacheKey(self);
    return "CLIENT: TileConduitBundle [pos=" + self.pos + ", facade=" + self.getPaintSource() + ", facadeType=" + self.facadeType + ", conduits="
        + self.getClientConduits() + ", cachekey=" + bsw.getCachekey() + ", bsw=" + bsw + "]";
  }

  public static String toStringS(TileConduitBundle self) {
    return "SERVER: TileConduitBundle [pos=" + self.pos + ", conduits=" + self.getServerConduits() + "]";
  }

  // FILTERS

  @Override
  public IItemFilter getFilter(int filterId, int param1) {
    ItemConduit itemConduit = getConduit(ItemConduit.class);
    if (itemConduit != null) {
      if (filterId == FilterGuiUtil.INDEX_INPUT) {
        return itemConduit.getInputFilter(EnumFacing.getFront(param1));
      } else if (filterId == FilterGuiUtil.INDEX_OUTPUT) {
        return itemConduit.getOutputFilter(EnumFacing.getFront(param1));
      }
    }
    return null;
  }

  @Override
  public void setFilter(int filterId, int param1, @Nonnull IItemFilter filter) {
    ItemConduit itemConduit = getConduit(ItemConduit.class);
    if (itemConduit != null) {
      if (filterId == FilterGuiUtil.INDEX_INPUT) {
        itemConduit.setInputFilter(EnumFacing.getFront(param1), filter);
      } else if (filterId == FilterGuiUtil.INDEX_OUTPUT) {
        itemConduit.setOutputFilter(EnumFacing.getFront(param1), filter);
      }
    }
  }

  @Override
  public IItemHandler getInventoryForSnapshot(int filterId, int param1) {
    ItemConduit itemConduit = getConduit(ItemConduit.class);
    if (itemConduit != null) {
      ItemConduitNetwork icn = itemConduit.getNetwork();
      if (icn != null) {
        return icn.getInventory(itemConduit, EnumFacing.getFront(param1)).getInventory();
      }
    }
    return null;
  }

}
