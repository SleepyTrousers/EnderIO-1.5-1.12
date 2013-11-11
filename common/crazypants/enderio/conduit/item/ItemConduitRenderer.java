package crazypants.enderio.conduit.item;

import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.render.DefaultConduitRenderer;
import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;

public class ItemConduitRenderer extends DefaultConduitRenderer {

  @Override
  public boolean isRendererForConduit(IConduit conduit) {
    if(conduit instanceof IItemConduit) {
      return true;
    }
    return false;
  }

  //  @Override
  //  public void renderEntity(ConduitBundleRenderer conduitBundleRenderer, IConduitBundle te, IConduit conduit, double x, double y, double z, float partialTick,
  //      float worldLight) {
  //    super.renderEntity(conduitBundleRenderer, te, conduit, x, y, z, partialTick, worldLight);
  //
  //    //    IItemConduit pc = (IItemConduit) conduit;
  //    //    for (ForgeDirection dir : conduit.getExternalConnections()) {
  //    //      Icon tex = null;
  //    //      if(conduit.getConectionMode(dir) == ConnectionMode.INPUT) {
  //    //        tex = pc.getTextureForInputMode();
  //    //      } else if(conduit.getConectionMode(dir) == ConnectionMode.OUTPUT) {
  //    //        tex = pc.getTextureForOutputMode();
  //    //      } else if(conduit.getConectionMode(dir) == ConnectionMode.IN_OUT) {
  //    //        tex = pc.getTextureForInOutMode();
  //    //      }
  //    //      if(tex != null) {
  //    //        Offset offset = te.getOffset(IItemConduit.class, dir);
  //    //        ConnectionModeGeometry.renderModeConnector(dir, offset, tex);
  //    //      }
  //    //    }
  //
  //  }

  @Override
  protected void renderConduit(Icon tex, IConduit conduit, CollidableComponent component, float brightness) {
    if(ItemConduit.EXTERNAL_INTERFACE_GEOM.equals(component.data)) {
      CubeRenderer.render(component.bound, tex);
    } else if(isNSEWUP(component.dir)) {
      IItemConduit lc = (IItemConduit) conduit;

      float scaleFactor = 0.6f;
      float xLen = Math.abs(component.dir.offsetX) == 1 ? 1 : scaleFactor;
      float yLen = Math.abs(component.dir.offsetY) == 1 ? 1 : scaleFactor;
      float zLen = Math.abs(component.dir.offsetZ) == 1 ? 1 : scaleFactor;

      scaleFactor -= 0.1f;
      float xLen2 = Math.abs(component.dir.offsetX) == 1 ? 1 : scaleFactor;
      float yLen2 = Math.abs(component.dir.offsetY) == 1 ? 1 : scaleFactor;
      float zLen2 = Math.abs(component.dir.offsetZ) == 1 ? 1 : scaleFactor;

      BoundingBox[] cubes = toCubes(component.bound);
      for (BoundingBox cube : cubes) {

        BoundingBox bb = cube.scale(xLen, yLen, zLen);
        drawSection(bb, tex.getMinU(), tex.getMaxU(), tex.getMinV(), tex.getMaxV(), component.dir, false);
        bb = cube.scale(xLen2, yLen2, zLen2);
        CubeRenderer.render(bb, ((IItemConduit) conduit).getEnderIcon());
      }

    } else {
      drawSection(component.bound, tex.getMinU(), tex.getMaxU(), tex.getMinV(), tex.getMaxV(), component.dir, true);
    }

  }

  private void renderFluidCenter(CollidableComponent component, IItemConduit conduit) {

    if(component == null || component.dir == ForgeDirection.UNKNOWN) {
      return;
    }

    BoundingBox bb = component.bound;
    float scaleFactor = 0.6f;
    float xLen = Math.abs(component.dir.offsetX) == 1 ? 1 : scaleFactor;
    float yLen = Math.abs(component.dir.offsetY) == 1 ? 1 : scaleFactor;
    float zLen = Math.abs(component.dir.offsetZ) == 1 ? 1 : scaleFactor;
    bb = bb.scale(xLen, yLen, zLen);
    CubeRenderer.render(bb, conduit.getEnderIcon());

  }
}
