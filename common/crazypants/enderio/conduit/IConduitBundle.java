package crazypants.enderio.conduit;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;
import appeng.api.me.tiles.IGridMachine;
import cofh.api.transport.IItemConduit;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.geom.Offset;
import crazypants.enderio.power.IInternalPowerReceptor;
import crazypants.util.BlockCoord;

public interface IConduitBundle extends IInternalPowerReceptor, IFluidHandler, IItemConduit, IGridMachine {

  TileEntity getEntity();

  BlockCoord getBlockCoord();

  // conduits

  boolean hasType(Class<? extends IConduit> type);

  <T extends IConduit> T getConduit(Class<T> type);

  void addConduit(IConduit conduit);

  void removeConduit(IConduit conduit);

  Collection<IConduit> getConduits();

  Offset getOffset(Class<? extends IConduit> type, ForgeDirection dir);

  // connections

  Set<ForgeDirection> getConnections(Class<? extends IConduit> type);

  boolean containsConnection(Class<? extends IConduit> type, ForgeDirection west);

  Set<ForgeDirection> getAllConnections();

  boolean containsConnection(ForgeDirection dir);

  //geometry

  List<CollidableComponent> getCollidableComponents();

  List<CollidableComponent> getConnectors();

  // events

  void onNeighborBlockChange(int blockId);

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

  void setFacadeId(int blockID);

  void setFacadeId(int blockID, boolean triggerUpdate);

  int getFacadeId();

  void setFacadeMetadata(int meta);

  int getFacadeMetadata();

}
