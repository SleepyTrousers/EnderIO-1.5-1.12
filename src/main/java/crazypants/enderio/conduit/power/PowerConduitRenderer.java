package crazypants.enderio.conduit.power;

import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.render.DefaultConduitRenderer;
import crazypants.enderio.machine.RedstoneControlMode;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class PowerConduitRenderer extends DefaultConduitRenderer {

  @Override
  public boolean isRendererForConduit(IConduit conduit) {
    return conduit instanceof IPowerConduit;
  }

  @Override
  protected void renderConduit(TextureAtlasSprite tex, IConduit conduit, CollidableComponent component, float selfIllum) {
    if(IPowerConduit.COLOR_CONTROLLER_ID.equals(component.data)) {
      IPowerConduit pc = (IPowerConduit) conduit;
      ConnectionMode conMode = pc.getConnectionMode(component.dir);
      if(conduit.containsExternalConnection(component.dir) && pc.getExtractionRedstoneMode(component.dir) != RedstoneControlMode.IGNORE
          && conMode != ConnectionMode.DISABLED) {
        int c = ((IPowerConduit) conduit).getExtractionSignalColor(component.dir).getColor();
//        Tessellator tessellator = Tessellator.instance;
//        tessellator.setColorOpaque_I(c);
//
//        Offset offset = conduit.getBundle().getOffset(IPowerConduit.class, component.dir);
//        BoundingBox bound = component.bound;
//        if(conMode != ConnectionMode.IN_OUT) {
//          Vector3d trans = ForgeDirectionOffsets.offsetScaled(component.dir, -0.075);
//          bound = bound.translate(trans);
//        }
//        CubeRenderer.render(bound, tex);
//        tessellator.setColorOpaque(255, 255, 255);
      }
    } else {
      super.renderConduit(tex, conduit, component, selfIllum);
    }
  }
}
