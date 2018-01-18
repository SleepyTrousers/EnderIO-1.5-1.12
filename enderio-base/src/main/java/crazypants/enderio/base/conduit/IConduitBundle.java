package crazypants.enderio.base.conduit;

import appeng.api.networking.IGridHost;
import com.enderio.core.common.util.DyeColor;
import crazypants.enderio.base.conduit.facade.EnumFacadeType;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.base.conduit.geom.Offset;
import crazypants.enderio.base.item.conduitprobe.PacketConduitProbe.IHasConduitProbeData;
import crazypants.enderio.base.paint.IPaintable;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.SidedEnvironment;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.common.Optional.InterfaceList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@InterfaceList({ @Interface(iface = "appeng.api.networking.IGridHost", modid = "appliedenergistics2"),
    @Interface(iface = "li.cil.oc.api.network.Environment", modid = "OpenComputersAPI|Network"),
    @Interface(iface = "li.cil.oc.api.network.SidedEnvironment", modid = "OpenComputersAPI|Network"), })
public interface IConduitBundle extends IPaintable.IPaintableTileEntity, Environment, SidedEnvironment, IGridHost, IHasConduitProbeData {

  @Nonnull
  TileEntity getEntity();

  @Nonnull
  BlockPos getLocation();

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

  // geometry

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

  // NB: this has to be named differently to the TE method due to obf
  World getBundleworld();

  /**
   * A callback for conduits to tell the conduit bundle that they have just changed in a way that changes the way the conduit bundle is rendered.
   * <p>
   * Safe to call server-side, but ignored there.
   */
  void geometryChanged();

  void setGridNode(Object node);

  int getInternalRedstoneSignalForColor(DyeColor col);

  boolean handleFacadeClick(World world, BlockPos placeAt, EntityPlayer player, EnumFacing opposite, ItemStack stack, EnumHand hand, float hitX, float hitY,
      float hitZ);
}
