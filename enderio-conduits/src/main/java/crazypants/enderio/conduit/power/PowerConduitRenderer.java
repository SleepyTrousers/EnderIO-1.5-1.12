package crazypants.enderio.conduit.power;

import java.util.List;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.common.util.ForgeDirectionOffsets;
import com.enderio.core.common.vecmath.Vector3d;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.base.conduit.geom.Offset;
import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import crazypants.enderio.conduit.geom.ConnectionModeGeometry;
import crazypants.enderio.conduit.render.DefaultConduitRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockRenderLayer;

public class PowerConduitRenderer extends DefaultConduitRenderer {

  @Override
  public boolean isRendererForConduit(IConduit conduit) {
    return conduit instanceof IPowerConduit;
  }

  @Override
  protected void addConduitQuads(IConduitBundle bundle, IConduit conduit, TextureAtlasSprite tex, CollidableComponent component, float selfIllum, BlockRenderLayer layer, List<BakedQuad> quads) {

    if (IPowerConduit.COLOR_CONTROLLER_ID.equals(component.data)) {
      IPowerConduit pc = (IPowerConduit) conduit;
      ConnectionMode conMode = pc.getConnectionMode(component.dir);
      
      if (conduit.containsExternalConnection(component.dir) && pc.getExtractionRedstoneMode(component.dir) != RedstoneControlMode.IGNORE
          && conMode != ConnectionMode.DISABLED) {
        
        int cInt = ((IPowerConduit) conduit).getExtractionSignalColor(component.dir).getColor();
        Vector4f col = ColorUtil.toFloat4(cInt);               

        BoundingBox bound = component.bound;
        if (conMode != ConnectionMode.IN_OUT && conMode != ConnectionMode.NOT_SET) {
          Vector3d trans = ForgeDirectionOffsets.offsetScaled(component.dir, -0.12);
          bound = bound.translate(trans);
        }
        addQuadsForSection(bound,tex,component.dir, quads, col); 
      }
      return;
    } 
    
    super.addConduitQuads(bundle, conduit, tex, component, selfIllum, layer, quads);
    
    if(component.dir  == null) {
      return;
    }
    IPowerConduit pc = (IPowerConduit)conduit;
    ConnectionMode mode = pc.getConnectionMode(component.dir);
    if(mode != ConnectionMode.INPUT && mode != ConnectionMode.OUTPUT) {
      return;
    }
    
    if(mode == ConnectionMode.INPUT) {
      tex = pc.getTextureForInputMode();
    } else {
      tex = pc.getTextureForOutputMode();
    }
    Offset offset = bundle.getOffset(IPowerConduit.class, component.dir);
    ConnectionModeGeometry.addModeConnectorQuads(component.dir, offset, tex, null, quads);
    
  }

  @Override
  public void initIcons() {
    PowerConduit.initIcons();
  }
}
