package crazypants.enderio.conduit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.TileEntityEio;
import crazypants.enderio.base.conduit.ConduitDisplayMode;
import crazypants.enderio.base.conduit.ConduitUtil;
import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.base.conduit.facade.EnumFacadeType;
import crazypants.enderio.base.conduit.geom.CollidableCache;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.base.conduit.geom.ConduitConnectorType;
import crazypants.enderio.base.conduit.geom.ConduitGeometryUtil;
import crazypants.enderio.base.conduit.geom.Offset;
import crazypants.enderio.base.conduit.geom.Offsets;
import crazypants.enderio.base.conduit.registry.ConduitRegistry;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.paint.YetaUtil;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.conduit.redstone.InsulatedRedstoneConduit;
import crazypants.enderio.conduit.render.BlockStateWrapperConduitBundle;
import crazypants.enderio.conduit.render.ConduitRenderMapper;
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

import static crazypants.enderio.base.config.Config.transparentFacadesLetThroughBeaconBeam;

public class TileConduitBundle extends TileEntityEio implements IConduitBundle, IConduitComponent {

  public static final short NBT_VERSION = 1;

  // TODO Fix duct-tape
  // TODO Check store
  @Store(handler = ConduitHandler.ConduitCopyOnWriteArrayListHandler.class)
  private final List<IConduit> conduits = new CopyOnWriteArrayList<IConduit>(); // <- duct-tape fix

  @Store
  private IBlockState facade = null;
  private EnumFacadeType facadeType = EnumFacadeType.BASIC;

  private final List<CollidableComponent> cachedCollidables = new CopyOnWriteArrayList<CollidableComponent>(); // <- duct-tape fix

  private final List<CollidableComponent> cachedConnectors = new CopyOnWriteArrayList<CollidableComponent>(); // <- duct-tape fix

  private boolean conduitsDirty = true;
  private boolean collidablesDirty = true;
  private boolean connectorsDirty = true;

  private boolean clientUpdated = false;

  private int lightOpacityOverride = -1;

  @SideOnly(Side.CLIENT)
  private FacadeRenderState facadeRenderAs;

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
    if (facade != null && facade.isOpaqueCube() && !YetaUtil.isFacadeHidden(this, EnderIO.proxy.getClientPlayer())) {
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
  //
  // @Override
  // protected void writeCustomNBT(NBTTagCompound nbtRoot) {
  // NBTTagList conduitTags = new NBTTagList();
  // for (IConduit conduit : conduits) {
  // NBTTagCompound conduitRoot = new NBTTagCompound();
  // ConduitUtil.writeToNBT(conduit, conduitRoot);
  // conduitTags.appendTag(conduitRoot);
  // }
  // nbtRoot.setTag("conduits", conduitTags);
  // if (facade != null) {
  // PaintUtil.writeNbt(nbtRoot, facade);
  // nbtRoot.setString("facadeType", facadeType.name());
  // }
  //
  // nbtRoot.setShort("nbtVersion", NBT_VERSION);
  // }
  //
  // @Override
  // public synchronized void readCustomNBT(NBTTagCompound nbtRoot) {
  // short nbtVersion = nbtRoot.getShort("nbtVersion");
  //
  // conduits.clear();
  // cachedCollidables.clear();
  // NBTTagList conduitTags = (NBTTagList) nbtRoot.getTag("conduits");
  // if (conduitTags != null) {
  // for (int i = 0; i < conduitTags.tagCount(); i++) {
  // NBTTagCompound conduitTag = conduitTags.getCompoundTagAt(i);
  // IConduit conduit = ConduitUtil.readConduitFromNBT(conduitTag, nbtVersion);
  // if (conduit != null) {
  // conduit.setBundle(this);
  // conduits.add(conduit);
  // // keep conduits sorted so the client side cache key is stable
  // ConduitRegistry.sort(conduits);
  // }
  // }
  // }
  // facade = PaintUtil.readNbt(nbtRoot);
  // if (facade != null) {
  // if (nbtRoot.hasKey("facadeType")) { // backwards compat, never true in freshly placed bundles
  // facadeType = EnumFacadeType.valueOf(nbtRoot.getString("facadeType"));
  // } else {
  // facadeType = EnumFacadeType.BASIC;
  // }
  // } else {
  // facade = null;
  // facadeType = EnumFacadeType.BASIC;
  // }
  //
  // if (world != null && world.isRemote) {
  // clientUpdated = true;
  // }
  // }

  @Override
  public boolean hasFacade() {
    return facade != null;
  }

  @Override
  public void setPaintSource(@Nullable IBlockState paintSource) {
    facade = paintSource;
    markDirty();
    // force re-calc of lighting for both client and server
    IBlockState bs = world.getBlockState(pos);
    IBlockState newBs = bs.withProperty(BlockConduitBundle.OPAQUE, getLightOpacity() > 0);
    if (bs == newBs) {
      world.setBlockState(getPos(), newBs.cycleProperty(BlockConduitBundle.OPAQUE));
    }
    world.setBlockState(getPos(), newBs);
  }

  @Override
  public IBlockState getPaintSource() {
    return facade;
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
    if (facadeRenderAs == null) {
      facadeRenderAs = FacadeRenderState.NONE;
    }
    return facadeRenderAs;
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
    if (facade != null) {
      if (getFacadeType().isTransparent() && transparentFacadesLetThroughBeaconBeam) {
        return Math.min(facade.getLightOpacity(), 14);
      } else {
        return facade.getLightOpacity();
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
    for (IConduit conduit : conduits) {
      conduit.onChunkUnload();
    }
  }

  @Override
  public void doUpdate() {
    getWorld().profiler.startSection("conduitBundle");
    getWorld().profiler.startSection("tick");

    for (IConduit conduit : conduits) {
      getWorld().profiler.startSection(conduit.getClass().toString());
      conduit.updateEntity(world);
      getWorld().profiler.endSection();
    }

    if (conduitsDirty) {
      getWorld().profiler.startSection("neigborUpdate");
      doConduitsDirty();
      getWorld().profiler.endSection();
    }
    getWorld().profiler.endSection();

    // client side only, check for changes in rendering of the bundle
    if (world.isRemote) {
      getWorld().profiler.startSection("clientTick");
      updateEntityClient();
      getWorld().profiler.endSection();
    }

    getWorld().profiler.endSection();
  }

  private void doConduitsDirty() {
    if (!world.isRemote) {
      IBlockState bs = world.getBlockState(pos);
      world.notifyBlockUpdate(pos, bs, bs, 3);
      world.neighborChanged(pos, getBlockType(), pos);
      markDirty();
    } else {
      geometryChanged(); // Q&D
    }
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
    FacadeRenderState rs = ConduitUtil.getRequiredFacadeRenderState(this, EnderIO.proxy.getClientPlayer());

    if (Config.updateLightingWhenHidingFacades) {
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
      geometryChanged(); // Q&D
      IBlockState bs = world.getBlockState(pos);
      world.notifyBlockUpdate(pos, bs, bs, 3);
    }
  }

  @Override
  public void onNeighborBlockChange(@Nonnull Block blockId) {
    boolean needsUpdate = false;
    for (IConduit conduit : conduits) {
      needsUpdate |= conduit.onNeighborBlockChange(blockId);
    }
    if (needsUpdate) {
      dirty();
    }
  }

  @Override
  public void onNeighborChange(@Nonnull IBlockAccess worldIn, @Nonnull BlockPos posIn, @Nonnull BlockPos neighbor) {
    boolean needsUpdate = false;
    for (IConduit conduit : conduits) {
      needsUpdate |= conduit.onNeighborChange(neighbor);
    }
    if (needsUpdate) {
      dirty();
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
    for (IConduit conduit : conduits) {
      if (type.isInstance(conduit)) {
        return (T) conduit;
      }
    }
    return null;
  }

  @Override
  public void addConduit(IConduit conduit) {
    if (world.isRemote) {
      return;
    }
    conduits.add(conduit);
    conduit.setBundle(this);
    conduit.onAddedToBundle();
    dirty();
  }

  @Override
  public void removeConduit(IConduit conduit) {
    if (conduit != null) {
      removeConduit(conduit, true);
    }
  }

  public void removeConduit(IConduit conduit, boolean notify) {
    if (world.isRemote) {
      return;
    }
    conduit.onRemovedFromBundle();
    conduits.remove(conduit);
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

  // Geometry

  @Override
  public @Nonnull Offset getOffset(@Nonnull Class<? extends IConduit> type, @Nonnull EnumFacing dir) {
    if (getConnectionCount(dir) < 2) {
      return Offset.NONE;
    }
    return Offsets.get(type, dir);
  }

  @Override
  public List<CollidableComponent> getCollidableComponents() {

    for (IConduit con : conduits) {
      collidablesDirty = collidablesDirty || con.haveCollidablesChangedSinceLastCall();
    }
    if (collidablesDirty) {
      connectorsDirty = true;
    }
    if (!collidablesDirty && !cachedCollidables.isEmpty()) {
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

  @SuppressWarnings("unchecked")
  private void addConnectors(List<CollidableComponent> result) {

    if (conduits.isEmpty()) {
      return;
    }

    for (IConduit con : conduits) {
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
    for (IConduit con : conduits) {
      addConduitCores(coreBounds, con);
    }
    cachedConnectors.addAll(coreBounds);
    result.addAll(coreBounds);

    // 1st algorithm
    List<CollidableComponent> conduitsBounds = new ArrayList<CollidableComponent>();
    for (IConduit con : conduits) {
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
    for (IConduit con : conduits) {

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
    for (IConduit con : conduits) {
      Set<EnumFacing> extCons = con.getExternalConnections();
      if (extCons != null) {
        for (EnumFacing dir : extCons) {
          if (con.getConnectionMode(dir) != ConnectionMode.DISABLED) {
            externalDirs.add(dir);
          }
        }
      }
    }
    for (EnumFacing dir : externalDirs) {
      BoundingBox bb = ConduitGeometryUtil.instance.getExternalConnectorBoundingBox(dir);
      CollidableComponent cc = new CollidableComponent(null, bb, dir, ConduitConnectorType.EXTERNAL);
      result.add(cc);
      cachedConnectors.add(cc);
    }

    connectorsDirty = false;
  }

  private void addConduitCores(List<CollidableComponent> result, IConduit con) {
    CollidableCache cc = CollidableCache.instance;
    Class<? extends IConduit> type = con.getCollidableType();
    if (con.hasConnections()) {
      for (EnumFacing dir : con.getExternalConnections()) {
        result.addAll(cc.getCollidables(cc.createKey(type, getOffset(con.getBaseConduitType(), dir), dir, false), con));
      }
      for (EnumFacing dir : con.getConduitConnections()) {
        result.addAll(cc.getCollidables(cc.createKey(type, getOffset(con.getBaseConduitType(), dir), dir, false), con));
      }
    } else {
      result.addAll(cc.getCollidables(cc.createKey(type, getOffset(con.getBaseConduitType(), EnumFacing.DOWN /* FIXME what to do here? */), EnumFacing.DOWN, false), con));
    }
  }

  private int getConnectionCount(@Nonnull EnumFacing dir) {
    if (dir == null) {
      return conduits.size();
    }
    int result = 0;
    for (IConduit con : conduits) {
      if (con.containsConduitConnection(dir) || con.containsExternalConnection(dir)) {
        result++;
      }
    }
    return result;
  }

  // ------------ Capabilities ----------------------

  @Override
  public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
    for (IConduit conduit : getConduits()) {
      if (conduit.hasCapability(capability, facing))
        return true; // TODO is this right?
    }
    return super.hasCapability(capability, facing);
  }

  @Nullable
  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
    for (IConduit conduit : getConduits()) {
      if (conduit.hasCapability(capability, facing))
        return conduit.getCapability(capability, facing);
    }
    return super.getCapability(capability, facing);
  }

  @Override
  public void geometryChanged() {
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
    List<IConduit> copy = new ArrayList<IConduit>(conduits);
    for (IConduit con : copy) {
      con.invalidate();
    }
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void hashCodeForModelCaching(IBlockStateWrapper wrapper, BlockStateWrapperConduitBundle.ConduitCacheKey hashCodes) {
    hashCodes.add(facadeType.ordinal() << 16 | getFacadeRenderedAs().ordinal() << 8 | wrapper.getYetaDisplayMode().getDisplayMode().ordinal() << 1
        | (wrapper.getYetaDisplayMode().isHideFacades() ? 1 : 0));
    for (IConduit conduit : conduits) {
      if (conduit instanceof IConduitComponent) {
        ((IConduitComponent) conduit).hashCodeForModelCaching(wrapper, hashCodes);
      } else {
        hashCodes.add(conduit);
      }
    }
  }

  @Override
  public String toString() {
    return world == null ? super.toString() : world.isRemote ? toStringC(this) : toStringS(this);
  }

  @SideOnly(Side.CLIENT)
  public static String toStringC(TileConduitBundle self) {
    BlockStateWrapperConduitBundle bsw = new BlockStateWrapperConduitBundle(self.world.getBlockState(self.pos), self.world, self.pos,
        ConduitRenderMapper.instance);
    bsw.addCacheKey(self);
    return "CLIENT: TileConduitBundle [pos=" + self.pos + ", facade=" + self.facade + ", facadeType=" + self.facadeType + ", conduits=" + self.conduits
        + ", cachekey=" + bsw.getCachekey() + ", bsw=" + bsw + "]";
  }

  public static String toStringS(TileConduitBundle self) {
    return "SERVER: TileConduitBundle [pos=" + self.pos + ", conduits=" + self.conduits + "]";
  }

}
