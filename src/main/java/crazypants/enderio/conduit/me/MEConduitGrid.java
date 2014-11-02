package crazypants.enderio.conduit.me;

import java.util.EnumSet;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import appeng.api.util.AEColor;
import appeng.me.helpers.AENetworkProxy;

public class MEConduitGrid extends AENetworkProxy {

  private IMEConduit conduit;

  public MEConduitGrid(IMEConduit conduit) {
    super(conduit.getBundle(), "EnderIO:conduit", conduit.createItem(), true);
    this.conduit = conduit;
  }

  @Override
  public double getIdlePowerUsage() {
    return 0; // TODO Balance
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
}
