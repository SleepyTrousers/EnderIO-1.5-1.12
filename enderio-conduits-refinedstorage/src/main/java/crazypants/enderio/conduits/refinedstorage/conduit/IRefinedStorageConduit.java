package crazypants.enderio.conduits.refinedstorage.conduit;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeProxy;

import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.conduit.IServerConduit;

public interface IRefinedStorageConduit extends IClientConduit, IServerConduit, INetworkNodeProxy<ConduitRefinedStorageNode> {

  public static final String ICON_KEY = "blocks/refined_storage_conduit";
  public static final String ICON_CORE_KEY = "blocks/refined_storage_conduit_core";

}
