package crazypants.enderio.conduit.render;

import java.util.List;

import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.BlockRenderLayer;

public interface ConduitRenderer {

  boolean isRendererForConduit(IConduit conduit);

  boolean isDynamic();

  void renderDynamicEntity(ConduitBundleRenderer conduitBundleRenderer, IConduitBundle te, IConduit con, double x, double y, double z, float partialTick,
      float worldLight);

  void addBakedQuads(ConduitBundleRenderer conduitBundleRenderer, IConduitBundle bundle, IConduit con, float brightness, BlockRenderLayer layer, List<BakedQuad> quads);

}
