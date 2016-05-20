package crazypants.enderio.conduit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.EnderIO;
import crazypants.enderio.TileEntityEio;
import crazypants.enderio.conduit.facade.EnumFacadeType;
import crazypants.enderio.conduit.geom.CollidableCache;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.geom.ConduitConnectorType;
import crazypants.enderio.conduit.geom.ConduitGeometryUtil;
import crazypants.enderio.conduit.geom.Offset;
import crazypants.enderio.conduit.geom.Offsets;
import crazypants.enderio.conduit.liquid.ILiquidConduit;
import crazypants.enderio.conduit.power.IPowerConduit;
import crazypants.enderio.conduit.redstone.InsulatedRedstoneConduit;
import crazypants.enderio.config.Config;
import crazypants.enderio.paint.PainterUtil2;

public class TileConduitBundle extends TileEntityEio implements IConduitBundle {

  public static final short NBT_VERSION = 1;

  private final List<IConduit> conduits = new CopyOnWriteArrayList<IConduit>(); // <- duct-tape fix

  private IBlockState facade = null;  
  private EnumFacadeType facadeType = EnumFacadeType.BASIC;

  private boolean facadeChanged;

  private final List<CollidableComponent> cachedCollidables = new CopyOnWriteArrayList<CollidableComponent>(); // <- duct-tape fix

  private final List<CollidableComponent> cachedConnectors = new CopyOnWriteArrayList<CollidableComponent>(); // <- duct-tape fix

  private boolean conduitsDirty = true;
  private boolean collidablesDirty = true;
  private boolean connectorsDirty = true;

  private boolean clientUpdated = false;

  private int lightOpacity = -1;

  @SideOnly(Side.CLIENT)
  private FacadeRenderState facadeRenderAs;

  private ConduitDisplayMode lastMode = ConduitDisplayMode.ALL;
    
  Object covers;
  
  public TileConduitBundle() {
    this.blockType = EnderIO.blockConduitBundle;
//    initMicroblocks();    
  }

  @Override
  public void dirty() {
    conduitsDirty = true;
    collidablesDirty = true;
  }

  @Override
  public boolean shouldRenderInPass(int arg0) {
    if(facade != null && facade.getBlock().isOpaqueCube(facade) && !ConduitUtil.isFacadeHidden(this, EnderIO.proxy.getClientPlayer())) {
      return false;
    }
    return super.shouldRenderInPass(arg0);
  }
  
  @Override
  public World getBundleWorldObj() {
    return getWorld();
  }

  @Override
  public void writeCustomNBT(NBTTagCompound nbtRoot) {
    NBTTagList conduitTags = new NBTTagList();
    for (IConduit conduit : conduits) {
      NBTTagCompound conduitRoot = new NBTTagCompound();
      ConduitUtil.writeToNBT(conduit, conduitRoot);
      conduitTags.appendTag(conduitRoot);
    }
    nbtRoot.setTag("conduits", conduitTags);
    if(facade != null) {
      PainterUtil2.writeNbt(nbtRoot, facade);
      nbtRoot.setString("facadeType", facadeType.name());
    }
    
    nbtRoot.setShort("nbtVersion", NBT_VERSION);
    
//    if (MicroblocksUtil.supportMicroblocks()) {
//      writeMicroblocksToNBT(nbtRoot);
//    }
  }

  @Override
  public void readCustomNBT(NBTTagCompound nbtRoot) {
    short nbtVersion = nbtRoot.getShort("nbtVersion");

    conduits.clear();
    cachedCollidables.clear();
    NBTTagList conduitTags = (NBTTagList) nbtRoot.getTag("conduits");
    if(conduitTags != null) {
      for (int i = 0; i < conduitTags.tagCount(); i++) {
        NBTTagCompound conduitTag = conduitTags.getCompoundTagAt(i);
        IConduit conduit = ConduitUtil.readConduitFromNBT(conduitTag, nbtVersion);
        if(conduit != null) {
          conduit.setBundle(this);
          conduits.add(conduit);
        }
      }
    }
    facade = PainterUtil2.readNbt(nbtRoot);
    if (facade != null) {
      if (nbtRoot.hasKey("facadeType")) { // backwards compat, never true in freshly placed bundles
        facadeType = EnumFacadeType.valueOf(nbtRoot.getString("facadeType"));
      } else {
        facadeType = EnumFacadeType.BASIC;
      }
    } else {
      facade = null;
      facadeType = EnumFacadeType.BASIC;
    }

    if(worldObj != null && worldObj.isRemote) {
      clientUpdated = true;
    }

//    if (MicroblocksUtil.supportMicroblocks()) {
//      readMicroblocksFromNBT(nbtRoot);
//    }
  }

  @Override
  public boolean hasFacade() {
    return facade != null;
  }

  public void setFacadeNoUpdate(IBlockState paintSource) { // TODO: check if needed
    facade = paintSource;
  }

  @Override
  public void setPaintSource(IBlockState paintSource) {
    facade = paintSource;
    facadeChanged = true;
    markDirty();
    updateBlock();
  }

  @Override
  public IBlockState getPaintSource() {
    return facade;
  }

  @Override
  public void setFacadeType(EnumFacadeType type) {
    facadeType = type;
    markDirty();
  }

  @Override
  public EnumFacadeType getFacadeType() {
    return facadeType;
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
    if((worldObj != null && !worldObj.isRemote) || lightOpacity == -1) {
      return facade != null ? facade.getBlock().getLightOpacity(facade) : 0;
    }
    return lightOpacity;
  }

  @Override
  public void setLightOpacity(int opacity) {
    lightOpacity = opacity;
  }

  @Override
  public void onChunkUnload() {
    for (IConduit conduit : conduits) {
      conduit.onChunkUnload(worldObj);
    }
  }

  @Override
  public void doUpdate() {
    for (IConduit conduit : conduits) {
      conduit.updateEntity(worldObj);
    }

    if(conduitsDirty) {
      doConduitsDirty();
    }

    if(facadeChanged) {
      doFacadeChanged();
    }

    //client side only, check for changes in rendering of the bundle
    if(worldObj.isRemote) {
      updateEntityClient();
    }
  }

  private void doConduitsDirty() {
    if(!worldObj.isRemote) {
      IBlockState bs = worldObj.getBlockState(pos);
      worldObj.notifyBlockUpdate(pos, bs, bs, 3);      
      markDirty();
    } else {
      geometryChanged(); // Q&D
    }
    conduitsDirty = false;
  }

  private void doFacadeChanged() {
    //force re-calc of lighting for both client and server
    ConduitUtil.forceSkylightRecalculation(worldObj, getPos());
    worldObj.checkLight(getPos());    
    IBlockState bs = worldObj.getBlockState(pos);
    worldObj.notifyBlockUpdate(pos, bs, bs, 3);
    worldObj.notifyNeighborsOfStateChange(getPos(), EnderIO.blockConduitBundle);
    facadeChanged = false;
  }

  private void updateEntityClient() {
    boolean markForUpdate = false;
    if(clientUpdated) {
      //TODO: This is not the correct solution here but just marking the block for a render update server side
      //seems to get out of sync with the client sometimes so connections are not rendered correctly
      markForUpdate = true;
      clientUpdated = false;
    }

    FacadeRenderState curRS = getFacadeRenderedAs();
    FacadeRenderState rs = ConduitUtil.getRequiredFacadeRenderState(this, EnderIO.proxy.getClientPlayer());

    if(Config.updateLightingWhenHidingFacades) {
      int curLO = getLightOpacity();
      int shouldBeLO = rs == FacadeRenderState.FULL ? 255 : 0;
      if(curLO != shouldBeLO) {
        setLightOpacity(shouldBeLO);
        //worldObj.updateAllLightTypes(xCoord, yCoord, zCoord);
        worldObj.checkLight(getPos());
      }
    }

    if(curRS != rs) {
      setFacadeRenderAs(rs);
      if(!ConduitUtil.forceSkylightRecalculation(worldObj, getPos())) {
        markForUpdate = true;
      }
    } else { //can do the else as only need to update once
      ConduitDisplayMode curMode = ConduitDisplayMode.getDisplayMode(EnderIO.proxy.getClientPlayer().getHeldItemMainhand());
      if(curMode != lastMode) {
        markForUpdate = true;
        lastMode = curMode;
      }

    }
    if(markForUpdate) {
      geometryChanged(); // Q&D
      IBlockState bs = worldObj.getBlockState(pos);
      worldObj.notifyBlockUpdate(pos, bs, bs, 3);      
    }
  }

  @Override
  public void onNeighborBlockChange(Block blockId) {
    boolean needsUpdate = false;
    for (IConduit conduit : conduits) {
      needsUpdate |= conduit.onNeighborBlockChange(blockId);
    }
    if(needsUpdate) {
      dirty();
    }
  }
  
  @Override
  public void onNeighborChange(IBlockAccess world, BlockPos posIn, BlockPos neighbor) {
    boolean needsUpdate = false;
    for (IConduit conduit : conduits) {
      needsUpdate |= conduit.onNeighborChange(world, posIn, neighbor);
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
  public Set<EnumFacing> getConnections(Class<? extends IConduit> type) {
    IConduit con = getConduit(type);
    if(con != null) {
      return con.getConduitConnections();
    }
    return null;
  }

  @Override
  public boolean containsConnection(Class<? extends IConduit> type, EnumFacing dir) {
    IConduit con = getConduit(type);
    if(con != null) {
      return con.containsConduitConnection(dir);
    }
    return false;
  }

  @Override
  public boolean containsConnection(EnumFacing dir) {
    for (IConduit con : conduits) {
      if(con.containsConduitConnection(dir)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Set<EnumFacing> getAllConnections() {
    EnumSet<EnumFacing> result = EnumSet.noneOf(EnumFacing.class);
    for (IConduit con : conduits) {
      result.addAll(con.getConduitConnections());
    }
    return result;
  }

  // Geometry

  @Override
  public Offset getOffset(Class<? extends IConduit> type, EnumFacing dir) {
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

  
  @SuppressWarnings("unchecked")
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
        CollidableComponent cc = new CollidableComponent(null, bb, null, ConduitConnectorType.INTERNAL);
        result.add(cc);
        cachedConnectors.add(cc);
      }
    }

    //2nd algorithm
    for (IConduit con : conduits) {

      if(con.hasConnections()) {
        List<CollidableComponent> cores = new ArrayList<CollidableComponent>();
        addConduitCores(cores, con);
        if(cores.size() > 1) {
          BoundingBox bb = cores.get(0).bound;
          float area = bb.getArea();
          for (CollidableComponent cc : cores) {
            bb = bb.expandBy(cc.bound);
          }
          if(bb.getArea() > area * 1.5f) {
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

    if(conBB != null) {
      CollidableComponent cc = new CollidableComponent(null, conBB, null, ConduitConnectorType.INTERNAL);
      result.add(cc);
      cachedConnectors.add(cc);
    }

    // External Connectors
    EnumSet<EnumFacing> externalDirs = EnumSet.noneOf(EnumFacing.class);
    for (IConduit con : conduits) {
      Set<EnumFacing> extCons = con.getExternalConnections();
      if(extCons != null) {
        for (EnumFacing dir : extCons) {
          if(con.getConnectionMode(dir) != ConnectionMode.DISABLED) {
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
    if(con.hasConnections()) {
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

  private int getConnectionCount(EnumFacing dir) {
    if(dir == null) {
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
  public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
    IPowerConduit pc = getConduit(IPowerConduit.class);
    if(pc != null) {
      return pc.receiveEnergy(from, maxReceive, simulate);
    }
    return 0;
  }

  @Override
  public boolean canConnectEnergy(EnumFacing from) {
    IPowerConduit pc = getConduit(IPowerConduit.class);
    if(pc != null) {
      return pc.canConnectEnergy(from);
    }
    return false;
  }

  @Override
  public int getEnergyStored(EnumFacing from) {
    IPowerConduit pc = getConduit(IPowerConduit.class);
    if(pc != null) {
      return pc.getEnergyStored(from);
    }
    return 0;
  }

  @Override
  public int getMaxEnergyStored(EnumFacing from) {
    IPowerConduit pc = getConduit(IPowerConduit.class);
    if(pc != null) {
      return pc.getMaxEnergyStored(from);
    }
    return 0;
  }

  @Override
  public int getMaxEnergyRecieved(EnumFacing dir) {
    IPowerConduit pc = getConduit(IPowerConduit.class);
    if(pc != null) {
      return pc.getMaxEnergyRecieved(dir);
    }
    return 0;
  }

  @Override
  public int getEnergyStored() {
    IPowerConduit pc = getConduit(IPowerConduit.class);
    if(pc != null) {
      return pc.getEnergyStored();
    }
    return 0;
  }

  @Override
  public int getMaxEnergyStored() {
    IPowerConduit pc = getConduit(IPowerConduit.class);
    if(pc != null) {
      return pc.getMaxEnergyStored();
    }
    return 0;
  }

  @Override
  public void setEnergyStored(int stored) {
    IPowerConduit pc = getConduit(IPowerConduit.class);
    if(pc != null) {
      pc.setEnergyStored(stored);
    }
    
  }

//------- Liquids -----------------------------
  
  @Override
  public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
    ILiquidConduit lc = getConduit(ILiquidConduit.class);
    if(lc != null) {
      return lc.fill(from, resource, doFill);
    }
    return 0;
  }

  @Override
  public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
    ILiquidConduit lc = getConduit(ILiquidConduit.class);
    if(lc != null) {
      return lc.drain(from, resource, doDrain);
    }
    return null;
  }

  @Override
  public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
    ILiquidConduit lc = getConduit(ILiquidConduit.class);
    if(lc != null) {
      return lc.drain(from, maxDrain, doDrain);
    }
    return null;
  }

  @Override
  public boolean canFill(EnumFacing from, Fluid fluid) {
    ILiquidConduit lc = getConduit(ILiquidConduit.class);
    if(lc != null) {
      return lc.canFill(from, fluid);
    }
    return false;
  }

  @Override
  public boolean canDrain(EnumFacing from, Fluid fluid) {
    ILiquidConduit lc = getConduit(ILiquidConduit.class);
    if(lc != null) {
      return lc.canDrain(from, fluid);
    }
    return false;
  }

  @Override
  public FluidTankInfo[] getTankInfo(EnumFacing from) {
    ILiquidConduit lc = getConduit(ILiquidConduit.class);
    if(lc != null) {
      return lc.getTankInfo(from);
    }
    return null;
  }

  // ---- TE Item Conduits

//  @Override
//  public ItemStack insertItem(EnumFacing from, ItemStack item) {
//    IItemConduit ic = getConduit(IItemConduit.class);
//    if(ic != null) {
//      return ic.insertItem(from, item);
//    }
//    return item;
//  }

  // ---- Mekanism Gas Tubes

//  @Override
//  @Method(modid = "MekanismAPI|gas")
//  public int receiveGas(EnumFacing side, GasStack stack) {
//    return receiveGas(side, stack, true);
//  }
//
//  @Override
//  @Method(modid = "MekanismAPI|gas")
//  public int receiveGas(EnumFacing side, GasStack stack, boolean doTransfer) {
//    IGasConduit gc = getConduit(IGasConduit.class);
//    if(gc != null) {
//      return gc.receiveGas(side, stack, doTransfer);
//    }
//    return 0;
//  }
//
//  @Override
//  @Method(modid = "MekanismAPI|gas")
//  public GasStack drawGas(EnumFacing side, int amount) {
//    return drawGas(side, amount, true);
//  }
//
//  @Override
//  @Method(modid = "MekanismAPI|gas")
//  public GasStack drawGas(EnumFacing side, int amount, boolean doTransfer) {
//    IGasConduit gc = getConduit(IGasConduit.class);
//    if(gc != null) {
//      return gc.drawGas(side, amount, doTransfer);
//    }
//    return null;
//  }
//
//  @Override
//  @Method(modid = "MekanismAPI|gas")
//  public boolean canReceiveGas(EnumFacing side, Gas type) {
//    IGasConduit gc = getConduit(IGasConduit.class);
//    if(gc != null) {
//      return gc.canReceiveGas(side, type);
//    }
//    return false;
//  }
//
//  @Override
//  @Method(modid = "MekanismAPI|gas")
//  public boolean canDrawGas(EnumFacing side, Gas type) {
//    IGasConduit gc = getConduit(IGasConduit.class);
//    if(gc != null) {
//      return gc.canDrawGas(side, type);
//    }
//    return false;
//  }
 
//  private Object node; // IGridNode object, untyped to avoid crash w/o AE2
//  
//  @Override
//  @Method(modid = "appliedenergistics2")
//  public IGridNode getGridNode(EnumFacing dir) {
//    if (dir == null || dir == EnumFacing.UNKNOWN) {
//      return (IGridNode) node;
//    } else {
//      IMEConduit cond = getConduit(IMEConduit.class);
//      if (cond != null) {
//        if (cond.getConnectionMode(dir.getOpposite()) == ConnectionMode.IN_OUT) {
//          return (IGridNode) node;
//        } else {
//          return null;
//        }
//      }
//    }
//    return (IGridNode) node;
//  }
//  
//  @SuppressWarnings("cast")
//  @Override
//  @Method(modid = "appliedenergistics2")
//  public void setGridNode(Object node) {
//    this.node = node;
//  }
//
//  @Override
//  @Method(modid = "appliedenergistics2")
//  public AECableType getCableConnectionType(EnumFacing dir) {
//    IMEConduit cond = getConduit(IMEConduit.class);
//    if (cond == null) {
//      return AECableType.NONE;
//    } else {
//      return cond.isConnectedTo(dir) ? AECableType.SMART : AECableType.NONE;
//    }
//  }
//  
//  @Override
//  @Method(modid = "appliedenergistics2")
//  public void securityBreak() {
//  }

  @Override
  public boolean displayPower() {
    return true;
  }

  @Override
  public BlockCoord getLocation() {
    return new BlockCoord(getPos());
  }

  private int serial = 0;

  @Override
  public void geometryChanged() {
    serial++;
  }

  /**
   * @return An integer value that is guaranteed to change whenever the conduit bundle's rendering changes.
   */
  public int getSerial() {
    return serial;
  }

  // Immibis Microblocks

//  private void initMicroblocks() {
//    if (MicroblocksUtil.supportMicroblocks()) {
//      createCovers();
//    }
//  }

//  @Method(modid = "ImmibisMicroblocks")
//  private void createCovers() {
//    IMicroblockSystem ims = MicroblockAPIUtils.getMicroblockSystem();
//    if (ims != null) {
//      covers = ims.createMicroblockCoverSystem(this);
//    }
//  }
//
//  @Override
//  @Method(modid = "ImmibisMicroblocks")
//  public boolean isPlacementBlocked(PartType<?> part, EnumPosition pos) {
//    EnumPartClass type = part.getPartClass();
//    // Let's do some cheaper checks first
//    if (type == EnumPartClass.Strip) {
//      // No pillars
//      if (pos == EnumPosition.PostX || pos == EnumPosition.PostY || pos == EnumPosition.PostZ) {
//        return true;
//      }
//    } else if (part.getSize() < 0.25) {
//      // Anything this small can never intersect conduits
//      return false;
//    }
//    // Finally just check core BB intersections
//    // Ignore anything that is not a core to allow blocking of connections
//    else if (type == EnumPartClass.Panel) {
//      List<CollidableComponent> boxes = getCollidableComponents();
//      BoundingBox bb = new BoundingBox(Part.getBoundingBoxFromPool(pos, part.getSize()));
//      for (CollidableComponent c : boxes) {
//        if (c.dir == EnumFacing.UNKNOWN && c.bound.intersects(bb)) {
//          return true;
//        }
//      }
//    }
//    return false;
//  }
//
//  @Override
//  @Method(modid = "ImmibisMicroblocks")
//  public IMicroblockCoverSystem getCoverSystem() {
//    return (IMicroblockCoverSystem) covers;
//  }
//
//  @Method(modid = "ImmibisMicroblocks")
//  private void writeMicroblocksToNBT(NBTTagCompound tag) {
//    if (covers != null) {
//      ((IMicroblockCoverSystem) covers).writeToNBT(tag);
//    }
//  }
//
//  @Method(modid = "ImmibisMicroblocks")
//  private void readMicroblocksFromNBT(NBTTagCompound tag) {
//    if (covers != null) {
//      ((IMicroblockCoverSystem) covers).readFromNBT(tag);
//    }
//  }
//
//  @Override
//  @Method(modid = "ImmibisMicroblocks")
//  public Packet getDescriptionPacket() {
//    if (covers == null) {
//      return super.getDescriptionPacket();
//    }
//
//    NBTTagCompound tag = new NBTTagCompound();
//    tag.setByteArray("C", ((IMicroblockCoverSystem) covers).writeDescriptionBytes());
//    writeCustomNBT(tag);
//    return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
//  }
//
//  @Override
//  @Method(modid = "ImmibisMicroblocks")
//  public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
//    super.onDataPacket(net, pkt);
//    if (covers != null) {
//      ((IMicroblockCoverSystem) covers).readDescriptionBytes(pkt.func_148857_g().getByteArray("C"), 0);
//    }
//  }
//
//  @Override
//  @Method(modid = "ImmibisMicroblocks")
//  public void onMicroblocksChanged() {
//    Set<EnumFacing> needUpdates = EnumSet.allOf(EnumFacing.class);
//    needUpdates.remove(EnumFacing.UNKNOWN);
//    for (Part p : getCoverSystem().getAllParts()) {
//      if (p.type.getPartClass() == EnumPartClass.Panel) {
//        EnumFacing dir = MicroblocksUtil.posToDir(p.pos);
//        updateConnections(dir, true);
//        needUpdates.remove(dir);
//      }
//    }
//    for (EnumFacing dir : needUpdates) {
//      updateConnections(dir, false);
//    }
//    
//    worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
//    updateBlock();
//  }
//  
//  @Method(modid = "ImmibisMicroblocks")
//  private void updateConnections(EnumFacing dir, boolean remove) {
//    TileEntity neighbor = getLocation().getLocation(dir).getTileEntity(worldObj);
//    IConduitBundle neighborBundle = (IConduitBundle) (neighbor instanceof IConduitBundle ? neighbor : null);
//    for (IConduit c : getConduits()) {
//      if (remove) {
//        removeConnection(dir, c);
//      } else if (neighborBundle != null) {
//        addConnection(dir, c, neighborBundle.getConduit(c.getBaseConduitType()));
//      }
//      c.connectionsChanged();
//    }
//    dir = dir.getOpposite();
//    if (neighbor instanceof IConduitBundle) {
//      for (IConduit c : ((TileConduitBundle) neighbor).getConduits()) {
//        if (remove) {
//          removeConnection(dir, c);
//        } else if (neighborBundle != null) {
//          addConnection(dir, c, getConduit(c.getBaseConduitType()));
//        }
//        c.connectionsChanged();
//      }
//    }
//  }
//
//  @Method(modid = "ImmibisMicroblocks")
//  private void removeConnection(EnumFacing dir, IConduit c) {
//    if (c.getConduitConnections().contains(dir)) {
//      c.conduitConnectionRemoved(dir);
//    }
//  }
//
//  @Method(modid = "ImmibisMicroblocks")
//  private void addConnection(EnumFacing dir, IConduit c, IConduit connectingTo) {
//    if (connectingTo != null) {
//      if (!c.getConduitConnections().contains(dir) && connectingTo.canConnectToConduit(dir, c)) {
//        c.conduitConnectionAdded(dir);
//      }
//    }
//  }

  // OpenComputers

//  @Override
//  @Method(modid = "OpenComputersAPI|Network")
//  public Node node() {
//    IOCConduit cond = getConduit(IOCConduit.class);
//    if (cond != null) {
//      return cond.node();
//    } else {
//      return null;
//    }
//  }
//
//  @Override
//  @Method(modid = "OpenComputersAPI|Network")
//  public void onConnect(Node node) {
//    IOCConduit cond = getConduit(IOCConduit.class);
//    if (cond != null) {
//      cond.onConnect(node);
//    }
//  }
//
//  @Override
//  @Method(modid = "OpenComputersAPI|Network")
//  public void onDisconnect(Node node) {
//    IOCConduit cond = getConduit(IOCConduit.class);
//    if (cond != null) {
//      cond.onDisconnect(node);
//    }
//  }
//
//  @Override
//  @Method(modid = "OpenComputersAPI|Network")
//  public void onMessage(Message message) {
//    IOCConduit cond = getConduit(IOCConduit.class);
//    if (cond != null) {
//      cond.onMessage(message);
//    }
//  }
//
//  @Override
//  @Method(modid = "OpenComputersAPI|Network")
//  public Node sidedNode(EnumFacing side) {
//    IOCConduit cond = getConduit(IOCConduit.class);
//    if (cond != null) {
//      return cond.sidedNode(side);
//    } else {
//      return null;
//    }
//  }
//
//  @Override
//  @Method(modid = "OpenComputersAPI|Network")
//  @SideOnly(Side.CLIENT)
//  public boolean canConnect(EnumFacing side) {
//    IOCConduit cond = getConduit(IOCConduit.class);
//    if (cond != null) {
//      return cond.canConnect(side);
//    } else {
//      return false;
//    }
//  }

//  @Override
//  public void invalidate() {
//    super.invalidate();
//    IOCConduit cond = getConduit(IOCConduit.class);
//    if (cond != null) {
//      cond.invalidate();
//    }
//  }

}
