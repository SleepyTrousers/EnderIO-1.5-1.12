package crazypants.enderio.conduits.refinedstorage;

import com.raoulvdberge.refinedstorage.api.IRSAPI;
import com.raoulvdberge.refinedstorage.api.RSAPIInject;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeProxy;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class RSHelper {
  @RSAPIInject
  public static IRSAPI API;

  @CapabilityInject(INetworkNodeProxy.class)
  public static final Capability<INetworkNodeProxy> NETWORK_NODE_PROXY_CAPABILITY = null;
}
