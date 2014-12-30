package crazypants.enderio.conduit.me;

import crazypants.enderio.conduit.AbstractConduitNetwork;

public class MEConduitNetwork extends AbstractConduitNetwork<IMEConduit, IMEConduit> {

  public MEConduitNetwork() {
    super(IMEConduit.class);
  }

  @Override
  public Class<IMEConduit> getBaseConduitType() {
    return IMEConduit.class;
  }
  
  @Override
  public void addConduit(IMEConduit con) {
    // TODO Auto-generated method stub
    super.addConduit(con);
  }
}