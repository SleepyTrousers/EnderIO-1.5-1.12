package crazypants.enderio.conduits.me.conduit;

import com.enderio.core.common.transform.SimpleMixin;

import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.conduits.conduit.TileConduitBundle;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.common.Optional.Method;

@SimpleMixin(TileConduitBundle.class)
@Interface(iface = "appeng.api.networking.IGridHost", modid = "appliedenergistics2")
public interface MEMixin extends IConduitBundle, IGridHost {

  @Override
  @Method(modid = "appliedenergistics2")
  public default IGridNode getGridNode(AEPartLocation loc) {
    IMEConduit cond = getConduit(IMEConduit.class);
    if (cond != null) {
      if (loc == null || loc == AEPartLocation.INTERNAL || cond.getConnectionMode(loc.getOpposite().getFacing()) == ConnectionMode.IN_OUT) {
        return cond.getGridNode();
      }
    }
    return null;
  }

  @Override
  @Method(modid = "appliedenergistics2")
  public default AECableType getCableConnectionType(AEPartLocation loc) {
    IMEConduit cond = getConduit(IMEConduit.class);
    if (cond == null || loc == AEPartLocation.INTERNAL) {
      return AECableType.NONE;
    } else {
      return cond.isConnectedTo(loc.getFacing()) ? cond.isDense() ? AECableType.DENSE_SMART : AECableType.SMART : AECableType.NONE;
    }
  }

  @Override
  @Method(modid = "appliedenergistics2")
  public default void securityBreak() {
  }

}
