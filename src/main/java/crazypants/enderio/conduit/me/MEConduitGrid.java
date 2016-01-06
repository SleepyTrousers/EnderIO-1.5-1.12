package crazypants.enderio.conduit.me;

import java.util.EnumSet;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import appeng.api.networking.GridFlags;
import appeng.api.networking.GridNotification;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridBlock;
import appeng.api.networking.IGridHost;
import appeng.api.util.AEColor;
import appeng.api.util.DimensionalCoord;

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
    return AEColor.Transparent;
  }

  @Override
  public EnumSet<ForgeDirection> getConnectableSides() {
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
    return conduit.getBundle();
  }

  @Override
  public void gridChanged() {
    conduit.onNeighborBlockChange(null);
  }
}
