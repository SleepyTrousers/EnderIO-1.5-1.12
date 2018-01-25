package crazypants.enderio.conduit.item;

import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.base.conduit.geom.Offset;
import crazypants.enderio.conduit.geom.ConnectionModeGeometry;
import crazypants.enderio.conduit.render.DefaultConduitRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
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
  protected void addConduitQuads(@Nonnull IConduitBundle bundle, @Nonnull IConduit conduit, @Nonnull TextureAtlasSprite tex,
      @Nonnull CollidableComponent component, float selfIllum, BlockRenderLayer layer, @Nonnull List<BakedQuad> quads) {
    super.addConduitQuads(bundle, conduit, tex, component, selfIllum, layer, quads);

    if (layer == null || component.dir == null) {
      return;
    }

    EnumFacing dir = component.dir;
    if (!conduit.getExternalConnections().contains(dir)) {
      return;
    }

    IItemConduit pc = (IItemConduit) conduit;
    DyeColor inChannel = null;
    DyeColor outChannel = null;
    TextureAtlasSprite inTex = null;
    TextureAtlasSprite outTex = null;
    boolean render = true;
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

    if (render) {
      Offset offset = bundle.getOffset(IItemConduit.class, dir);
      ConnectionModeGeometry.addModeConnectorQuads(dir, offset, pc.getTextureForInOutBackground(), null, quads);
      if (inChannel != null) {
        ConnectionModeGeometry.addModeConnectorQuads(dir, offset, inTex, ColorUtil.toFloat4(inChannel.getColor()), quads);
      }
      if (outChannel != null) {
        ConnectionModeGeometry.addModeConnectorQuads(dir, offset, outTex, ColorUtil.toFloat4(outChannel.getColor()), quads);
      }
    }
  }

  @Override
  public void initIcons() {
    ItemConduit.initIcons();
  }
}
