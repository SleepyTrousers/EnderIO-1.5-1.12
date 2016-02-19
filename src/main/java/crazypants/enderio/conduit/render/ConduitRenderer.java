package crazypants.enderio.conduit.render;

import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;

public interface ConduitRenderer {

  boolean isRendererForConduit(IConduit conduit);

  boolean isDynamic();

  void renderDynamicEntity(ConduitBundleRenderer conduitBundleRenderer, IConduitBundle te, IConduit con, double x, double y, double z, float partialTick,
      float worldLight);

}
