package crazypants.enderio.conduit.me.conduit;

import java.util.EnumSet;

import javax.annotation.Nonnull;

import appeng.api.networking.GridFlags;
import appeng.api.networking.GridNotification;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridBlock;
import appeng.api.networking.IGridHost;
import appeng.api.util.AEColor;
import appeng.api.util.DimensionalCoord;
import crazypants.enderio.base.conduit.IConduitBundle;
import net.minecraft.item.ItemStack;
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
  public @Nonnull AEColor getGridColor() {
    return AEColor.TRANSPARENT;
  }

  @Override
  public @Nonnull EnumSet<EnumFacing> getConnectableSides() {
    return conduit.getConnections();
  }

  @Override
  public ItemStack getMachineRepresentation() {
    return conduit.createItem();
  }

  @Override
  public @Nonnull EnumSet<GridFlags> getFlags() {
    return conduit.isDense() ? EnumSet.of(GridFlags.DENSE_CAPACITY) : EnumSet.noneOf(GridFlags.class);
  }

  @Override
  public boolean isWorldAccessible() {
    return true;
  }

  @Override
  public @Nonnull DimensionalCoord getLocation() {
    return new DimensionalCoord(conduit.getBundle().getEntity());
  }

  @Override
  public void onGridNotification(@Nonnull GridNotification notification) {
    ;
  }

  @Override
  public void setNetworkStatus(IGrid grid, int channelsInUse) {
    ;
  }

  @Override
  public @Nonnull IGridHost getMachine() {
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
