package crazypants.enderio.conduit;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.conduit.facade.ItemConduitFacade.FacadeType;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.geom.Offset;
import crazypants.enderio.power.IInternalPowerHandler;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
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
    @Interface(iface = "li.cil.oc.api.network.SidedEnvironment", modid = "OpenComputersAPI|Network")
})
public interface IConduitBundle extends IInternalPowerHandler, IFluidHandler {

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

  void setLightOpacity(int opacity);

  boolean hasFacade();

  void setFacadeId(Block block);

  void setFacadeId(Block block, boolean triggerUpdate);

  void setFacadeMetadata(int meta);

  void setFacadeType(FacadeType type);

  Block getFacadeId();

  int getFacadeMetadata();

  FacadeType getFacadeType();

  World getWorld();

}
