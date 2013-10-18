package crazypants.enderio.conduit.redstone;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.render.DefaultConduitRenderer;
import crazypants.render.CubeRenderer;

public class InsulatedRedstoneConduitRenderer extends DefaultConduitRenderer {

  @Override
  public boolean isRendererForConduit(IConduit conduit) {
    return conduit instanceof IInsulatedRedstoneConduit;
  }

  @Override
  protected void renderConduit(Icon tex, IConduit conduit, CollidableComponent component, float selfIllum) {
    if(IInsulatedRedstoneConduit.COLOR_CONTROLLER_ID.equals(component.data)) {
      if(conduit.containsExternalConnection(component.dir)) {
        int c = ((IInsulatedRedstoneConduit) conduit).getSignalColor(component.dir).getColor();
        Tessellator tessellator = Tessellator.instance;
        tessellator.setColorOpaque_I(c);
        CubeRenderer.render(component.bound, tex);
        tessellator.setColorOpaque(255, 255, 255);
      }
    } else {
      super.renderConduit(tex, conduit, component, selfIllum);
    }
  }

}
