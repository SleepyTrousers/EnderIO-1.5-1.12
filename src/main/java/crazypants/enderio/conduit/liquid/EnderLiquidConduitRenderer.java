package crazypants.enderio.conduit.liquid;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.geom.ConnectionModeGeometry;
import crazypants.enderio.conduit.geom.Offset;
import crazypants.enderio.conduit.render.ConduitBundleRenderer;
import crazypants.enderio.conduit.render.DefaultConduitRenderer;

public class EnderLiquidConduitRenderer extends DefaultConduitRenderer {

  @Override
  public boolean isRendererForConduit(IConduit conduit) {
    if(conduit instanceof EnderLiquidConduit) {      
      return true;
    }
    return false;
  }

  
  @Override
  public void renderEntity(ConduitBundleRenderer conduitBundleRenderer, IConduitBundle te, IConduit conduit, double x, double y, double z, float partialTick,
      float worldLight, RenderBlocks rb) {
    super.renderEntity(conduitBundleRenderer, te, conduit, x, y, z, partialTick, worldLight, rb);
    EnderLiquidConduit pc = (EnderLiquidConduit) conduit;
    for (ForgeDirection dir : conduit.getExternalConnections()) {
      IIcon tex = null;
      if(conduit.getConnectionMode(dir) == ConnectionMode.INPUT) {
        tex = pc.getTextureForInputMode();
      } else if(conduit.getConnectionMode(dir) == ConnectionMode.OUTPUT) {
        tex = pc.getTextureForOutputMode();
      } else if(conduit.getConnectionMode(dir) == ConnectionMode.IN_OUT) {
        tex = pc.getTextureForInOutMode();
      }
      if(tex != null) {
        Offset offset = te.getOffset(ILiquidConduit.class, dir);
        ConnectionModeGeometry.renderModeConnector(dir, offset, tex, true);
      }
    }
  }

  

}
