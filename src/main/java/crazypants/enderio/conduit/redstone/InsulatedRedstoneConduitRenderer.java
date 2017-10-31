package crazypants.enderio.conduit.redstone;

import java.util.List;

import com.enderio.core.client.render.ColorUtil;

import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.render.BakedQuadBuilder;
import crazypants.enderio.conduit.render.DefaultConduitRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockRenderLayer;

public class InsulatedRedstoneConduitRenderer extends DefaultConduitRenderer {

  @Override
  public boolean isRendererForConduit(IConduit conduit) {
    return conduit instanceof IRedstoneConduit;
  }

  @Override
  protected void addConduitQuads(IConduitBundle bundle, IConduit conduit, TextureAtlasSprite tex, CollidableComponent component, float selfIllum, BlockRenderLayer layer,
      List<BakedQuad> quads) {
    if (IRedstoneConduit.COLOR_CONTROLLER_ID.equals(component.data)) {
      if (conduit.containsExternalConnection(component.dir) && !((IRedstoneConduit) conduit).isSpecialConnection(component.dir)) {
        int c = ((IRedstoneConduit) conduit).getSignalColor(component.dir).getColor();
        BakedQuadBuilder.addBakedQuads(quads, component.bound, tex, ColorUtil.toFloat4(c));
      }
    } else {
      super.addConduitQuads(bundle, conduit, tex, component, selfIllum, layer, quads);
    }
  }

  @Override
  public void initIcons() {
    InsulatedRedstoneConduit.initIcons();
  }

}
