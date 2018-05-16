package crazypants.enderio.conduits.me.conduit;

import crazypants.enderio.conduits.conduit.AbstractConduitNetwork;

public class MEConduitNetwork extends AbstractConduitNetwork<IMEConduit, IMEConduit> {

  public MEConduitNetwork() {
    super(IMEConduit.class, IMEConduit.class);
  }

}