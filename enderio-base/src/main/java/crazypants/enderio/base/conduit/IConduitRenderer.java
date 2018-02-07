package crazypants.enderio.base.conduit;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.BlockRenderLayer;

public interface IConduitRenderer {

  boolean isRendererForConduit(@Nonnull IConduit conduit);

  void addBakedQuads(@Nonnull TileEntitySpecialRenderer<?> conduitBundleRenderer, @Nonnull IConduitBundle bundle,
      @Nonnull IConduit.WithDefaultRendering conduit, float brightness, @Nonnull BlockRenderLayer layer, List<BakedQuad> quads);

  // -----------------------
  // DYNAMIC
  // -----------------------

  void renderDynamicEntity(@Nonnull TileEntitySpecialRenderer<?> conduitBundleRenderer, @Nonnull IConduitBundle te,
      @Nonnull IConduit.WithDefaultRendering conduit, double x, double y, double z, float partialTick, float worldLight);

  default boolean isDynamic() {
    return false;
  }

}
