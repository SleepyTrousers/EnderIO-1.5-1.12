package crazypants.enderio.conduits.conduit.power;

import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.common.util.ForgeDirectionOffsets;
import com.enderio.core.common.vecmath.Vector3d;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.base.conduit.geom.Offset;
import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import crazypants.enderio.conduits.geom.ConnectionModeGeometry;
import crazypants.enderio.conduits.render.DefaultConduitRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;

public class PowerConduitRenderer extends DefaultConduitRenderer {

  @Override
  public boolean isRendererForConduit(@Nonnull IConduit conduit) {
    return conduit instanceof IPowerConduit;
  }

  @Override
  protected void addConduitQuads(@Nonnull IConduitBundle bundle, @Nonnull IClientConduit conduit, @Nonnull TextureAtlasSprite tex,
      @Nonnull CollidableComponent component, float selfIllum, BlockRenderLayer layer, @Nonnull List<BakedQuad> quads) {

    if (IPowerConduit.COLOR_CONTROLLER_ID.equals(component.data)) {
      IPowerConduit pc = (IPowerConduit) conduit;
      final EnumFacing componentDirection = component.getDirection();
      ConnectionMode conMode = pc.getConnectionMode(componentDirection);

      if (conduit.containsExternalConnection(componentDirection) && pc.getExtractionRedstoneMode(componentDirection) != RedstoneControlMode.IGNORE
          && conMode != ConnectionMode.DISABLED) {

        int cInt = ((IPowerConduit) conduit).getExtractionSignalColor(componentDirection).getColor();
        Vector4f col = ColorUtil.toFloat4(cInt);

        BoundingBox bound = component.bound;
        if (conMode != ConnectionMode.IN_OUT && conMode != ConnectionMode.NOT_SET) {
          Vector3d trans = ForgeDirectionOffsets.offsetScaled(componentDirection, -0.12);
          bound = bound.translate(trans);
        }
        addQuadsForSection(bound, tex, componentDirection, quads, col);
      }
      return;
    }

    super.addConduitQuads(bundle, conduit, tex, component, selfIllum, layer, quads);

    if (component.isCore()) {
      return;
    }
    IPowerConduit pc = (IPowerConduit) conduit;
    final EnumFacing componentDirection = component.getDirection();
    ConnectionMode mode = pc.getConnectionMode(componentDirection);
    if (mode != ConnectionMode.INPUT && mode != ConnectionMode.OUTPUT) {
      return;
    }

    if (mode == ConnectionMode.INPUT) {
      tex = pc.getTextureForInputMode();
    } else {
      tex = pc.getTextureForOutputMode();
    }
    Offset offset = bundle.getOffset(IPowerConduit.class, componentDirection);
    ConnectionModeGeometry.addModeConnectorQuads(componentDirection, offset, tex, null, quads);

  }
}
