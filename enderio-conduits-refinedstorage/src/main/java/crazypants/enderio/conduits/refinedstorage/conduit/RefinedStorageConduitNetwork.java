package crazypants.enderio.conduits.refinedstorage.conduit;

import crazypants.enderio.conduits.conduit.AbstractConduitNetwork;

public class RefinedStorageConduitNetwork extends AbstractConduitNetwork<IRefinedStorageConduit, IRefinedStorageConduit> {

  protected RefinedStorageConduitNetwork() {
    super(IRefinedStorageConduit.class, IRefinedStorageConduit.class);
  }

}
