package crazypants.enderio.conduit.item;

import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.geom.ConnectionModeGeometry;
import crazypants.enderio.conduit.geom.Offset;
import crazypants.enderio.conduit.render.ConduitBundleRenderer;
import crazypants.enderio.conduit.render.DefaultConduitRenderer;

public class ItemConduitRenderer extends DefaultConduitRenderer {

  @Override
  public boolean isRendererForConduit(IConduit conduit) {
    if(conduit instanceof IItemConduit) {
      return true;
    }
    return false;
  }

  @Override
  public void renderEntity(ConduitBundleRenderer conduitBundleRenderer, IConduitBundle te, IConduit conduit, double x, double y, double z, float partialTick,
      float worldLight) {
    super.renderEntity(conduitBundleRenderer, te, conduit, x, y, z, partialTick, worldLight);

    IItemConduit pc = (IItemConduit) conduit;
    for (ForgeDirection dir : conduit.getExternalConnections()) {
      Icon tex = null;
      if(conduit.getConectionMode(dir) == ConnectionMode.INPUT) {
        tex = pc.getTextureForInputMode();
      } else if(conduit.getConectionMode(dir) == ConnectionMode.OUTPUT) {
        tex = pc.getTextureForOutputMode();
      } else if(conduit.getConectionMode(dir) == ConnectionMode.IN_OUT) {
        tex = pc.getTextureForInOutMode();
      }
      if(tex != null) {
        Offset offset = te.getOffset(IItemConduit.class, dir);
        ConnectionModeGeometry.renderModeConnector(dir, offset, tex);
      }
    }

  }

}
