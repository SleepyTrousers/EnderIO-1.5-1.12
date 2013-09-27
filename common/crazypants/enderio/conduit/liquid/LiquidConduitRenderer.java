package crazypants.enderio.conduit.liquid;

import static crazypants.render.CubeRenderer.setupVertices;

import java.util.List;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.render.ConduitBundleRenderer;
import crazypants.enderio.conduit.render.DefaultConduitRenderer;
import crazypants.render.BoundingBox;
import crazypants.render.RenderUtil;
import crazypants.vecmath.Vector2d;
import crazypants.vecmath.Vector3d;
import crazypants.vecmath.Vector3f;

public class LiquidConduitRenderer extends DefaultConduitRenderer {

  private float downRatio;

  private float flatRatio;

  private float upRatio;

  @Override
  public boolean isRendererForConduit(IConduit conduit) {
    if(conduit instanceof ILiquidConduit) {
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
  protected void renderConduit(Icon tex, IConduit conduit, CollidableComponent component, float brightness) {
    if(isNSEWUP(component.dir)) {
      ILiquidConduit lc = (ILiquidConduit) conduit;
      FluidStack fluid = lc.getFluidType();
      if(fluid != null) {
        renderFluidOutline(component, fluid);
      }
      BoundingBox[] cubes = toCubes(component.bound);
      for (BoundingBox cube : cubes) {
        drawSection(cube, tex.getMinU(), tex.getMaxU(), tex.getMinV(), tex.getMaxV(), component.dir, false);
      }

    } else {
      drawSection(component.bound, tex.getMinU(), tex.getMaxU(), tex.getMinV(), tex.getMaxV(), component.dir, true);
    }
  }

  private void renderFluidOutline(CollidableComponent component, FluidStack fluid) {
    //TODO: Should cache these vertices as relatively heavy weight to calc each frame
    Icon texture = fluid.getFluid().getStillIcon();
    BoundingBox bbb = component.bound;
    for (ForgeDirection face : ForgeDirection.VALID_DIRECTIONS) {
      if(face != component.dir && face != component.dir.getOpposite()) {

        Tessellator tes = Tessellator.instance;
        tes.setNormal(face.offsetX, face.offsetY, face.offsetZ);

        float scaleFactor = 14f / 16f;
        Vector2d uv = new Vector2d();
        List<ForgeDirection> edges = RenderUtil.getEdgesForFace(face);
        for (ForgeDirection edge : edges) {
          if(edge != component.dir && edge != component.dir.getOpposite()) {
            float xLen = 1 - Math.abs(edge.offsetX) * scaleFactor;
            float yLen = 1 - Math.abs(edge.offsetY) * scaleFactor;
            float zLen = 1 - Math.abs(edge.offsetZ) * scaleFactor;
            BoundingBox bb = bbb.scale(xLen, yLen, zLen);

            List<Vector3f> corners = bb.getCornersForFace(face);

            for (Vector3f unitCorn : corners) {
              Vector3d corner = new Vector3d(unitCorn);

              corner.x += (float) (edge.offsetX * 0.5 * bbb.sizeX()) - (Math.signum(edge.offsetX) * xLen / 2f * bbb.sizeX()) * 2f;
              corner.y += (float) (edge.offsetY * 0.5 * bbb.sizeY()) - (Math.signum(edge.offsetY) * yLen / 2f * bbb.sizeY()) * 2f;
              corner.z += (float) (edge.offsetZ * 0.5 * bbb.sizeZ()) - (Math.signum(edge.offsetZ) * zLen / 2f * bbb.sizeZ()) * 2f;

              RenderUtil.getUvForCorner(uv, corner, 0, 0, 0, face, texture);

              tes.addVertexWithUV(corner.x, corner.y, corner.z, uv.x, uv.y);
            }
          }

        }
      }
    }
  }

  @Override
  protected void renderTransmission(Icon tex, CollidableComponent component, float brightness) {
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
    if(conduit.containsConduitConnection(ForgeDirection.UP) || conduit.containsExternalConnection(ForgeDirection.UP)) {
      upCapacity = ILiquidConduit.VOLUME_PER_CONNECTION;
    }
    int downCapacity = 0;
    if(conduit.containsConduitConnection(ForgeDirection.DOWN) || conduit.containsExternalConnection(ForgeDirection.DOWN)) {
      downCapacity = ILiquidConduit.VOLUME_PER_CONNECTION;
    }

    int flatCapacity = tank.getCapacity() - upCapacity - downCapacity;

    int usedCapacity = 0;
    if(downCapacity > 0) {
      int inDown = Math.min(totalAmount, downCapacity);
      usedCapacity += inDown;
      downRatio = (float) inDown / downCapacity;
    }
    if(flatCapacity > 0 && usedCapacity < totalAmount) {
      int inFlat = Math.min(flatCapacity, totalAmount - usedCapacity);
      usedCapacity += inFlat;
      flatRatio = (float) inFlat / flatCapacity;
    } else {
      flatRatio = 0;
    }
    if(upCapacity > 0 && usedCapacity < totalAmount) {
      int inUp = Math.min(upCapacity, totalAmount - usedCapacity);
      upRatio = (float) inUp / upCapacity;
    } else {
      upRatio = 0;
    }

  }

  private float getRatioForConnection(ForgeDirection id) {
    if(id == ForgeDirection.UP) {
      return upRatio;
    }
    if(id == ForgeDirection.DOWN) {
      return downRatio;
    }
    return flatRatio;
  }

}
