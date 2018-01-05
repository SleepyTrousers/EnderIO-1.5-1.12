package crazypants.enderio.conduit.me;

import crazypants.enderio.conduit.AbstractConduitNetwork;

public class MEConduitNetwork extends AbstractConduitNetwork<IMEConduit, IMEConduit> {

  public MEConduitNetwork() {
    super(IMEConduit.class, IMEConduit.class);
  }

}