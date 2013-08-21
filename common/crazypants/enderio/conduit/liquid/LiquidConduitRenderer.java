package crazypants.enderio.conduit.liquid;

import static crazypants.render.CubeRenderer.setupVertices;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.render.ConduitBundleRenderer;
import crazypants.enderio.conduit.render.DefaultConduitRenderer;
import crazypants.render.BoundingBox;
import crazypants.vecmath.Vector3d;

public class LiquidConduitRenderer extends DefaultConduitRenderer {

  private float downRatio;

  private float flatRatio;

  private float upRatio;

  @Override
  public boolean isRendererForConduit(IConduit conduit) {
    if (conduit instanceof ILiquidConduit) {
      return true;
    }
    return false;
  }

  @Override
  public void renderEntity(ConduitBundleRenderer conduitBundleRenderer, IConduitBundle te, IConduit conduit, double x, double y, double z, float partialTick,
      float worldLight) {
    calculateRatios((ILiquidConduit) conduit);
    super.renderEntity(conduitBundleRenderer, te, conduit, x, y, z, partialTick, worldLight);
  }

  @Override
  protected void renderConduit(Icon tex, CollidableComponent component) {
    if (isNSEWUP(component.dir)) {
      BoundingBox[] cubes = toCubes(component.bound);
      for (BoundingBox cube : cubes) {
        drawSection(cube, tex.getMinU(), tex.getMaxU(), tex.getMinV(), tex.getMaxV(), component.dir, false);
      }
    } else {
      drawSection(component.bound, tex.getMinU(), tex.getMaxU(), tex.getMinV(), tex.getMaxV(), component.dir, true);
    }
  }

  @Override
  protected void renderTransmission(Icon tex, CollidableComponent component) {
    BoundingBox[] cubes = toCubes(component.bound);
    for (BoundingBox cube : cubes) {
      drawSection(cube, tex.getMinU(), tex.getMaxU(), tex.getMinV(), tex.getMaxV(), component.dir, true);
    }
  }

  @Override
  protected void setVerticesForTransmission(BoundingBox bound, ForgeDirection id) {

    float yScale = getRatioForConnection(id);

    float xs = id.offsetX == 0 ? 0.9f : 1;
    float ys = id.offsetY == 0 ? Math.min(yScale, 0.9f) : yScale;
    float zs = id.offsetZ == 0 ? 0.9f : 1;

    float sizeY = bound.sizeY();
    bound = bound.scale(xs, ys, zs);
    float transY = (bound.sizeY() - sizeY) / 2;

    Vector3d translation = new Vector3d(0, transY, 0);
    setupVertices(bound.translate(translation));
  }

  private void calculateRatios(ILiquidConduit conduit) {
    ConduitTank tank = conduit.getTank();
    int totalAmount = tank.getFluidAmount();

    int upCapacity = 0;
    if (conduit.containsConduitConnection(ForgeDirection.UP) || conduit.containsExternalConnection(ForgeDirection.UP)) {
      upCapacity = ILiquidConduit.VOLUME_PER_CONNECTION;
    }
    int downCapacity = 0;
    if (conduit.containsConduitConnection(ForgeDirection.DOWN) || conduit.containsExternalConnection(ForgeDirection.DOWN)) {
      downCapacity = ILiquidConduit.VOLUME_PER_CONNECTION;
    }

    int flatCapacity = tank.getCapacity() - upCapacity - downCapacity;

    int usedCapacity = 0;
    if (downCapacity > 0) {
      int inDown = Math.min(totalAmount, downCapacity);
      usedCapacity += inDown;
      downRatio = (float) inDown / downCapacity;
    }
    if (flatCapacity > 0 && usedCapacity < totalAmount) {
      int inFlat = Math.min(flatCapacity, totalAmount - usedCapacity);
      usedCapacity += inFlat;
      flatRatio = (float) inFlat / flatCapacity;
    } else {
      flatRatio = 0;
    }
    if (upCapacity > 0 && usedCapacity < totalAmount) {
      int inUp = Math.min(upCapacity, totalAmount - usedCapacity);
      upRatio = (float) inUp / upCapacity;
    } else {
      upRatio = 0;
    }

  }

  private float getRatioForConnection(ForgeDirection id) {
    if (id == ForgeDirection.UP) {
      return upRatio;
    }
    if (id == ForgeDirection.DOWN) {
      return downRatio;
    }
    return flatRatio;
  }

}
