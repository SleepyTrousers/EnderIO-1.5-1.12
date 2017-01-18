package crazypants.enderio.conduit.oc;

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
import net.minecraftforge.client.model.ModelLoader.White;

public class OCConduitRenderer extends DefaultConduitRenderer {

  @Override
  public boolean isRendererForConduit(IConduit conduit) {
    return conduit instanceof IOCConduit;
  }

  @Override
  protected void addConduitQuads(IConduitBundle bundle, IConduit conduit, TextureAtlasSprite tex, CollidableComponent component, float selfIllum, BlockRenderLayer layer,
      List<BakedQuad> quads) {
    if (IOCConduit.COLOR_CONTROLLER_ID.equals(component.data)) {
      if (conduit.containsExternalConnection(component.dir)) {
        int c = ((IOCConduit) conduit).getSignalColor(component.dir).getColor();
        BakedQuadBuilder.addBakedQuads(quads, component.bound, White.INSTANCE, ColorUtil.toFloat4(c));
      }
    } else {
      super.addConduitQuads(bundle, conduit, tex, component, selfIllum, layer, quads);
    }
  }

}
