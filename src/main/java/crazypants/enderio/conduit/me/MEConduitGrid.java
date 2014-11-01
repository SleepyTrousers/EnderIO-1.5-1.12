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
import appeng.me.helpers.AENetworkProxy;

public class MEConduitGrid implements IGridBlock {

  private IMEConduit conduit;

  private AENetworkProxy proxy;

  public MEConduitGrid(IMEConduit conduit) {
    this.conduit = conduit;
    proxy = new AENetworkProxy(conduit.getBundle(), "enderio:MEConduit", conduit.createItem(), true);
  }

  @Override
  public double getIdlePowerUsage() {
    return 0; // TODO Balance
  }

  @Override
  public EnumSet<GridFlags> getFlags() {
    return EnumSet.noneOf(GridFlags.class);
  }

  @Override
  public boolean isWorldAccessable() {
    return true;
  }

  @Override
  public DimensionalCoord getLocation() {
    return new DimensionalCoord(conduit.getBundle().getEntity());
  }

  @Override
  public AEColor getGridColor() {
    return AEColor.Transparent;
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
  public EnumSet<ForgeDirection> getConnectableSides() {
    return proxy.getConnectableSides();
  }

  @Override
  public IGridHost getMachine() {
    return conduit.getBundle();
  }

  @Override
  public void gridChanged() {
    ;
  }

  @Override
  public ItemStack getMachineRepresentation() {
    return conduit.createItem();
  }

  public AENetworkProxy getProxy() {
    return proxy;
  }
}
