package crazypants.enderio.conduits.me.conduit;

import java.util.EnumSet;

import appeng.api.networking.GridFlags;
import appeng.api.networking.GridNotification;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridBlock;
import appeng.api.networking.IGridHost;
import appeng.api.util.AEColor;
import appeng.api.util.DimensionalCoord;
import crazypants.enderio.base.conduit.IConduitBundle;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MEConduitGrid implements IGridBlock {

  private final IMEConduit conduit;

  public MEConduitGrid(IMEConduit conduit) {
    this.conduit = conduit;
  }

  @Override
  public double getIdlePowerUsage() {
    return 0;
  }

  @Override
  public AEColor getGridColor() {
    return AEColor.TRANSPARENT;
  }

  @Override
  public EnumSet<EnumFacing> getConnectableSides() {
    return conduit.getConnections();
  }

  @Override
  public ItemStack getMachineRepresentation() {
    return conduit.createItem();
  }

  @Override
  public EnumSet<GridFlags> getFlags() {
    return conduit.isDense() ? EnumSet.of(GridFlags.DENSE_CAPACITY) : EnumSet.noneOf(GridFlags.class);
  }

  @Override
  public boolean isWorldAccessible() {
    return true;
  }

  @Override
  public DimensionalCoord getLocation() {
    return new DimensionalCoord(conduit.getBundle().getEntity());
  }

  @Override
  public void onGridNotification(GridNotification notification) {
    ;
  }

  @Override
  public void setNetworkStatus(IGrid grid, int channelsInUse) {
    ;
  }

  @Override
  public IGridHost getMachine() {
    IConduitBundle bundle = conduit.getBundle();
    if (bundle instanceof IGridHost) {
      return ((IGridHost) bundle);
    }
    throw new IllegalStateException("Bundle was null or not an IGridHost. Maybe a mixin issue? Bundle: " + bundle);
  }

  @Override
  public void gridChanged() {
    World world = conduit.getBundle().getBundleworld();
    BlockPos pos = conduit.getBundle().getLocation();
    conduit.onNeighborBlockChange(world.getBlockState(pos).getBlock());
  }
}
