package crazypants.enderio.conduits.conduit.liquid;

import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.base.conduit.geom.Offset;
import crazypants.enderio.conduits.geom.ConnectionModeGeometry;
import crazypants.enderio.conduits.render.DefaultConduitRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;

public class EnderLiquidConduitRenderer extends DefaultConduitRenderer {

  @Override
  public boolean isRendererForConduit(@Nonnull IConduit conduit) {
    if (conduit instanceof EnderLiquidConduit) {
      return true;
    }
    return false;
  }

  @Override
  protected void addConduitQuads(@Nonnull IConduitBundle bundle, @Nonnull IConduit conduit, @Nonnull TextureAtlasSprite tex,
      @Nonnull CollidableComponent component, float selfIllum, BlockRenderLayer layer, @Nonnull List<BakedQuad> quads) {
    super.addConduitQuads(bundle, conduit, tex, component, selfIllum, layer, quads);

    if (layer == null || component.dir == null) {
      return;
    }

    EnumFacing renderDir = component.dir;
    if (!conduit.getExternalConnections().contains(renderDir)) {
      return;
    }

    EnderLiquidConduit pc = (EnderLiquidConduit) conduit;
    DyeColor inChannel = null;
    DyeColor outChannel = null;
    TextureAtlasSprite inTex = null;
    TextureAtlasSprite outTex = null;
    boolean render = true;
    for (EnumFacing dir : conduit.getExternalConnections()) {

      if (conduit.getConnectionMode(dir) == ConnectionMode.INPUT) {
        inTex = pc.getTextureForInputMode();
        inChannel = pc.getInputColor(dir);
      } else if (conduit.getConnectionMode(dir) == ConnectionMode.OUTPUT) {
        outTex = pc.getTextureForOutputMode();
        outChannel = pc.getOutputColor(dir);
      } else if (conduit.getConnectionMode(dir) == ConnectionMode.IN_OUT) {
        inTex = pc.getTextureForInOutMode(true);
        outTex = pc.getTextureForInOutMode(false);
        inChannel = pc.getInputColor(dir);
        outChannel = pc.getOutputColor(dir);
      } else {
        render = false;
      }
    }

    if (render) {
      Offset offset = bundle.getOffset(EnderLiquidConduit.class, renderDir);
      ConnectionModeGeometry.addModeConnectorQuads(renderDir, offset, pc.getTextureForInOutBackground(), null, quads);
      if (inChannel != null) {
        ConnectionModeGeometry.addModeConnectorQuads(renderDir, offset, inTex, ColorUtil.toFloat4(inChannel.getColor()), quads);
      }
      if (outChannel != null) {
        ConnectionModeGeometry.addModeConnectorQuads(renderDir, offset, outTex, ColorUtil.toFloat4(outChannel.getColor()), quads);
      }
    }
  }
}
