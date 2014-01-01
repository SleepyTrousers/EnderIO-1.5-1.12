package crazypants.enderio.conduit.power;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.geom.ConnectionModeGeometry;
import crazypants.enderio.conduit.geom.Offset;
import crazypants.enderio.conduit.render.ConduitBundleRenderer;
import crazypants.enderio.conduit.render.DefaultConduitRenderer;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;
import crazypants.util.ForgeDirectionOffsets;
import crazypants.vecmath.Vector3d;

public class PowerConduitRenderer extends DefaultConduitRenderer {

  @Override
  public boolean isRendererForConduit(IConduit conduit) {
    return conduit instanceof IPowerConduit;
  }

  @Override
  public void renderEntity(ConduitBundleRenderer conduitBundleRenderer, IConduitBundle te, IConduit conduit, double x, double y, double z, float partialTick,
      float worldLight) {
    super.renderEntity(conduitBundleRenderer, te, conduit, x, y, z, partialTick, worldLight);

    if(!conduit.hasConnectionMode(ConnectionMode.INPUT) && !conduit.hasConnectionMode(ConnectionMode.OUTPUT)) {
      return;
    }
    IPowerConduit pc = (IPowerConduit) conduit;
    for (ForgeDirection dir : conduit.getExternalConnections()) {
      Icon tex = null;
      if(conduit.getConectionMode(dir) == ConnectionMode.INPUT) {
        tex = pc.getTextureForInputMode();
      } else if(conduit.getConectionMode(dir) == ConnectionMode.OUTPUT) {
        tex = pc.getTextureForOutputMode();
      }
      if(tex != null) {
        Offset offset = te.getOffset(IPowerConduit.class, dir);
        ConnectionModeGeometry.renderModeConnector(dir, offset, tex, true);
      }
    }

  }

  @Override
  protected void renderConduit(Icon tex, IConduit conduit, CollidableComponent component, float selfIllum) {
    if(IPowerConduit.COLOR_CONTROLLER_ID.equals(component.data)) {
      IPowerConduit pc = (IPowerConduit) conduit;
      ConnectionMode conMode = pc.getConectionMode(component.dir);
      if(conduit.containsExternalConnection(component.dir) && pc.getRedstoneMode(component.dir) != RedstoneControlMode.IGNORE
          && conMode != ConnectionMode.DISABLED) {
        int c = ((IPowerConduit) conduit).getSignalColor(component.dir).getColor();
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
