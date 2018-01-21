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

@InterfaceList({ @Interface(iface = "appeng.api.networking.IGridHost", modid = "appliedenergistics2"),
    @Interface(iface = "li.cil.oc.api.network.Environment", modid = "OpenComputersAPI|Network"),
    @Interface(iface = "li.cil.oc.api.network.SidedEnvironment", modid = "OpenComputersAPI|Network"), })
public interface IConduitBundle extends IPaintable.IPaintableTileEntity, Environment, SidedEnvironment, IGridHost, IHasConduitProbeData {

  /**
   * @return Tile Entity of the Conduit Bundle
   */
  @Nonnull
  TileEntity getEntity();

  /**
   * Location of the Bundle
   * @return
   */
  @Nonnull
  BlockPos getLocation();

  // conduits

  /**
   * Checks if the bundle contains the given conduit type
   * @param type Class of the conduit to check for the type of
   * @return true if the bundle has the given type of conduit
   */
  boolean hasType(Class<? extends IConduit> type);

  /**
   * Gets a conduit of the given conduit type
   * @param type the type of conduit to get
   * @param <T> the conduit type to return
   * @return the conduit of the given type
   */
  <T extends IConduit> T getConduit(Class<T> type);

  /**
   * Adds a conduit to the bundle
   * @param conduit the conduit to add
   */
  void addConduit(IConduit conduit);

  /**
   * removes a conduit from the bundle
   * @param conduit the conduit to remove
   */
  void removeConduit(IConduit conduit);

  /**
   * @return Collection of all the conduits in the bundle
   */
  Collection<IConduit> getConduits();

  // geometry

  Offset getOffset(Class<? extends IConduit> type, EnumFacing dir);

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

  // TODO find out what this does
  int getInternalRedstoneSignalForColor(DyeColor col);

  boolean handleFacadeClick(World world, BlockPos placeAt, EntityPlayer player, EnumFacing opposite, ItemStack stack, EnumHand hand, float hitX, float hitY,
      float hitZ);
}
