package crazypants.enderio.conduit.power;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.CubeRenderer;
import com.enderio.core.common.util.ForgeDirectionOffsets;
import com.enderio.core.common.vecmath.Vector3d;

import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.geom.ConnectionModeGeometry;
import crazypants.enderio.conduit.geom.Offset;
import crazypants.enderio.conduit.render.ConduitBundleRenderer;
import crazypants.enderio.conduit.render.DefaultConduitRenderer;
import crazypants.enderio.machine.RedstoneControlMode;

public class PowerConduitRenderer extends DefaultConduitRenderer {

  @Override
  public boolean isRendererForConduit(IConduit conduit) {
    return conduit instanceof IPowerConduit;
  }

  @Override
  public void renderEntity(ConduitBundleRenderer conduitBundleRenderer, IConduitBundle te, IConduit conduit, double x, double y, double z, float partialTick,
      float worldLight, RenderBlocks rb) {
    super.renderEntity(conduitBundleRenderer, te, conduit, x, y, z, partialTick, worldLight, rb);

    if(!conduit.hasConnectionMode(ConnectionMode.INPUT) && !conduit.hasConnectionMode(ConnectionMode.OUTPUT)) {
      return;
    }
    IPowerConduit pc = (IPowerConduit) conduit;
    for (ForgeDirection dir : conduit.getExternalConnections()) {
      IIcon tex = null;
      if(conduit.getConnectionMode(dir) == ConnectionMode.INPUT) {
        tex = pc.getTextureForInputMode();
      } else if(conduit.getConnectionMode(dir) == ConnectionMode.OUTPUT) {
        tex = pc.getTextureForOutputMode();
      }
      if(tex != null) {
        Offset offset = te.getOffset(IPowerConduit.class, dir);
        ConnectionModeGeometry.renderModeConnector(dir, offset, tex, true);
      }
    }

  }

  @Override
  protected void renderConduit(IIcon tex, IConduit conduit, CollidableComponent component, float selfIllum) {
    if(IPowerConduit.COLOR_CONTROLLER_ID.equals(component.data)) {
      IPowerConduit pc = (IPowerConduit) conduit;
      ConnectionMode conMode = pc.getConnectionMode(component.dir);
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
