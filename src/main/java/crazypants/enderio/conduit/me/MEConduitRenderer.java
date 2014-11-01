package crazypants.enderio.conduit.me;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.geom.Offset;
import crazypants.enderio.conduit.power.IPowerConduit;
import crazypants.enderio.conduit.render.DefaultConduitRenderer;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;
import crazypants.util.ForgeDirectionOffsets;
import crazypants.vecmath.Vector3d;

public class MEConduitRenderer extends DefaultConduitRenderer {
  
  @Override
  public boolean isRendererForConduit(IConduit conduit) {
    return conduit instanceof IMEConduit;
  }

  @Override
  protected void renderConduit(IIcon tex, IConduit conduit, CollidableComponent component, float selfIllum) {
    if(IPowerConduit.COLOR_CONTROLLER_ID.equals(component.data)) {
      IPowerConduit pc = (IPowerConduit) conduit;
      ConnectionMode conMode = pc.getConectionMode(component.dir);
      if(conduit.containsExternalConnection(component.dir) && pc.getExtractionRedstoneMode(component.dir) != RedstoneControlMode.IGNORE
          && conMode != ConnectionMode.DISABLED) {
        int c = ((IPowerConduit) conduit).getExtractionSignalColor(component.dir).getColor();
        Tessellator tessellator = Tessellator.instance;
        tessellator.setColorOpaque_I(c);

        Offset offset = conduit.getBundle().getOffset(IPowerConduit.class, component.dir);
        BoundingBox bound = component.bound;
        if(conMode != ConnectionMode.IN_OUT) {
          Vector3d trans = ForgeDirectionOffsets.offsetScaled(component.dir, -0.075);
          bound = bound.translate(trans);
        }
        CubeRenderer.render(bound, tex);
        tessellator.setColorOpaque(255, 255, 255);
      }
    } else {
      super.renderConduit(tex, conduit, component, selfIllum);
    }
  }

}
