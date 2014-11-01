package crazypants.enderio.conduit.me;

import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.render.DefaultConduitRenderer;

public class MEConduitRenderer extends DefaultConduitRenderer {
  
  @Override
  public boolean isRendererForConduit(IConduit conduit) {
    return conduit instanceof IMEConduit;
  }
}
