package crazypants.enderio.conduit;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import appeng.api.networking.IGridHost;
import com.enderio.core.common.util.BlockCoord;

import cofh.api.energy.IEnergyReceiver;
import crazypants.enderio.conduit.facade.EnumFacadeType;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.geom.Offset;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.power.IInternalPowerReceiver;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.SidedEnvironment;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.common.Optional.InterfaceList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@InterfaceList({
    @Interface(iface = "appeng.api.networking.IGridHost", modid = "appliedenergistics2"),
    @Interface(iface = "mekanism.api.gas.IGasHandler", modid = "MekanismAPI|gas"),
    @Interface(iface = "mods.immibis.microblocks.api.IMicroblockSupporterTile", modid = "ImmibisMicroblocks"),
    @Interface(iface = "li.cil.oc.api.network.Environment", modid = "OpenComputersAPI|Network"),
    @Interface(iface = "li.cil.oc.api.network.SidedEnvironment", modid = "OpenComputersAPI|Network"),
    @Interface(iface = "cofh.api.energy.IEnergyReceiver", modid = "CoFHAPI|energy")
})
public interface IConduitBundle extends IInternalPowerReceiver, IFluidHandler, IPaintable.IPaintableTileEntity, Environment, SidedEnvironment, IEnergyReceiver, IGridHost {

  TileEntity getEntity();

  @Override
  BlockCoord getLocation();

  // conduits

  boolean hasType(Class<? extends IConduit> type);

  <T extends IConduit> T getConduit(Class<T> type);

  void addConduit(IConduit conduit);

  void removeConduit(IConduit conduit);

  Collection<IConduit> getConduits();

  Offset getOffset(Class<? extends IConduit> type, EnumFacing dir);

  // connections

  Set<EnumFacing> getConnections(Class<? extends IConduit> type);

  boolean containsConnection(Class<? extends IConduit> type, EnumFacing west);

  Set<EnumFacing> getAllConnections();

  boolean containsConnection(EnumFacing dir);

  //geometry

  List<CollidableComponent> getCollidableComponents();

  List<CollidableComponent> getConnectors();

  // events

  void onNeighborBlockChange(Block blockId);
  
  void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor);

  void onBlockRemoved();

  void dirty();

  // Facade

  enum FacadeRenderState {
    NONE,
    FULL,
    WIRE_FRAME
  }

  @SideOnly(Side.CLIENT)
  FacadeRenderState getFacadeRenderedAs();

  @SideOnly(Side.CLIENT)
  void setFacadeRenderAs(FacadeRenderState state);

  int getLightOpacity();

  void setLightOpacityOverride(int opacity);

  boolean hasFacade();

  void setFacadeType(EnumFacadeType type);

  EnumFacadeType getFacadeType();

  //NB: this has to be named differently to the TE method due to obf
  World getBundleWorldObj();

  /**
   * A callback for conduits to tell the conduit bundle that they have just changed in a way that changes the way the conduit bundle is rendered.
   * <p>
   * Safe to call server-side, but ignored there.
   */
  void geometryChanged();

  void setGridNode(Object node);
}
