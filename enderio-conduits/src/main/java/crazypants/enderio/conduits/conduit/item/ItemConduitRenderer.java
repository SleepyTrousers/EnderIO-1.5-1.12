package crazypants.enderio.conduits.conduit.item;

import java.util.List;

import javax.annotation.Nonnull;

import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.base.conduit.IConduitTexture;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.conduits.render.ConduitInOutRenderer;
import crazypants.enderio.conduits.render.DefaultConduitRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;

public class ItemConduitRenderer extends DefaultConduitRenderer {

  @Override
  public boolean isRendererForConduit(@Nonnull IConduit conduit) {
    if (conduit instanceof IItemConduit) {
      return true;
    }
    return false;
  }

  @Override
  protected void addConduitQuads(@Nonnull IConduitBundle bundle, @Nonnull IClientConduit conduit, @Nonnull IConduitTexture tex,
      @Nonnull CollidableComponent component, float selfIllum, BlockRenderLayer layer, @Nonnull List<BakedQuad> quads) {
    super.addConduitQuads(bundle, conduit, tex, component, selfIllum, layer, quads);

    IItemConduit pc = (IItemConduit) conduit;
    EnumFacing dir = component.getDirection();
    ConduitInOutRenderer.renderIO(bundle, conduit, component, layer, quads, pc.getInputColor(dir), pc.getOutputColor(dir));
  }

}
