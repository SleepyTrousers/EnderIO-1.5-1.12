package crazypants.enderio.conduit.liquid;

import static crazypants.render.CubeRenderer.addVecWithUV;
import static crazypants.render.CubeRenderer.setupVertices;

import java.util.Collection;
import java.util.List;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.render.ConduitBundleRenderer;
import crazypants.enderio.conduit.render.DefaultConduitRenderer;
import crazypants.render.BoundingBox;
import crazypants.render.RenderUtil;
import crazypants.util.ForgeDirectionOffsets;
import crazypants.vecmath.Vector2f;
import crazypants.vecmath.Vector3d;
import crazypants.vecmath.Vector3f;
import crazypants.vecmath.Vertex;

public class LiquidConduitRenderer extends DefaultConduitRenderer {

  private float downRatio;

  private float flatRatio;

  private float upRatio;

  @Override
  public boolean isRendererForConduit(IConduit conduit) {
    if(conduit instanceof LiquidConduit) {
      return true;
    }
    return false;
  }

  @Override
  public void renderEntity(ConduitBundleRenderer conduitBundleRenderer, IConduitBundle te, IConduit conduit, double x, double y, double z, float partialTick,
      float worldLight) {
    calculateRatios((LiquidConduit) conduit);
    super.renderEntity(conduitBundleRenderer, te, conduit, x, y, z, partialTick, worldLight);
  }

  @Override
  protected void renderConduit(Icon tex, IConduit conduit, CollidableComponent component, float brightness) {
    if(isNSEWUD(component.dir)) {
      LiquidConduit lc = (LiquidConduit) conduit;
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

    if(conduit.getConectionMode(component.dir) == ConnectionMode.DISABLED) {
      tex = EnderIO.blockConduitBundle.getConnectorIcon();
      List<Vertex> corners = component.bound.getCornersWithUvForFace(component.dir, tex.getMinU(), tex.getMaxU(), tex.getMinV(), tex.getMaxV());
      Tessellator tessellator = Tessellator.instance;
      for (Vertex c : corners) {
        addVecWithUV(c.xyz, c.uv.x, c.uv.y);
      }
      //back face
      for (int i = corners.size() - 1; i >= 0; i--) {
        Vertex c = corners.get(i);
        addVecWithUV(c.xyz, c.uv.x, c.uv.y);
      }
    }
  }

  public static void renderFluidOutline(CollidableComponent component, FluidStack fluid) {
    renderFluidOutline(component, fluid, 1, 13f / 16f);
  }

  public static void renderFluidOutline(CollidableComponent component, FluidStack fluid, double scaleFactor, float outlineWidth) {
    //TODO: Should cache these vertices as relatively heavy weight to calc each frame
    Icon texture = fluid.getFluid().getStillIcon();
    if(texture == null) {
      texture = fluid.getFluid().getIcon();
      if(texture == null) {
        return;
      }
    }

    BoundingBox bbb;
    if(scaleFactor == 1) {
      bbb = component.bound;
    } else {
      double xScale = Math.abs(component.dir.offsetX) == 1 ? 1 : scaleFactor;
      double yScale = Math.abs(component.dir.offsetY) == 1 ? 1 : scaleFactor;
      double zScale = Math.abs(component.dir.offsetZ) == 1 ? 1 : scaleFactor;
      bbb = component.bound.scale(xScale, yScale, zScale);
    }

    for (ForgeDirection face : ForgeDirection.VALID_DIRECTIONS) {
      if(face != component.dir && face != component.dir.getOpposite()) {

        Tessellator tes = Tessellator.instance;
        tes.setNormal(face.offsetX, face.offsetY, face.offsetZ);
        Vector3d offset = ForgeDirectionOffsets.offsetScaled(face, -0.005);

        Vector2f uv = new Vector2f();
        List<ForgeDirection> edges = RenderUtil.getEdgesForFace(face);
        for (ForgeDirection edge : edges) {
          if(edge != component.dir && edge != component.dir.getOpposite()) {
            float xLen = 1 - Math.abs(edge.offsetX) * outlineWidth;
            float yLen = 1 - Math.abs(edge.offsetY) * outlineWidth;
            float zLen = 1 - Math.abs(edge.offsetZ) * outlineWidth;
            BoundingBox bb = bbb.scale(xLen, yLen, zLen);

            List<Vector3f> corners = bb.getCornersForFace(face);

            for (Vector3f unitCorn : corners) {
              Vector3d corner = new Vector3d(unitCorn);
              corner.add(offset);

              corner.x += (float) (edge.offsetX * 0.5 * bbb.sizeX()) - (Math.signum(edge.offsetX) * xLen / 2f * bbb.sizeX()) * 2f;
              corner.y += (float) (edge.offsetY * 0.5 * bbb.sizeY()) - (Math.signum(edge.offsetY) * yLen / 2f * bbb.sizeY()) * 2f;
              corner.z += (float) (edge.offsetZ * 0.5 * bbb.sizeZ()) - (Math.signum(edge.offsetZ) * zLen / 2f * bbb.sizeZ()) * 2f;

              //polyOffset

              RenderUtil.getUvForCorner(uv, corner, 0, 0, 0, face, texture);

              tes.addVertexWithUV(corner.x, corner.y, corner.z, uv.x, uv.y);
            }
          }

        }
      }
    }
  }

  @Override
  protected void renderTransmission(IConduit con, Icon tex, CollidableComponent component, float brightness) {
    //done in the dynamic section
  }

  @Override
  public boolean isDynamic() {
    return true;
  }

  @Override
  public void renderDynamicEntity(ConduitBundleRenderer conduitBundleRenderer, IConduitBundle te, IConduit conduit, double x, double y, double z,
      float partialTick, float worldLight) {

    if(((LiquidConduit) conduit).getTank().getFilledRatio() <= 0) {
      return;
    }

    Collection<CollidableComponent> components = conduit.getCollidableComponents();
    Tessellator tessellator = Tessellator.instance;

    calculateRatios((LiquidConduit) conduit);
    transmissionScaleFactor = conduit.getTransmitionGeometryScale();

    Icon tex;
    for (CollidableComponent component : components) {
      if(renderComponent(component)) {
        float selfIllum = Math.max(worldLight, conduit.getSelfIlluminationForState(component));
        if(isNSEWUD(component.dir) &&
            conduit.getTransmitionTextureForState(component) != null) {

          tessellator.setColorOpaque_F(1, 1, 1);
          tex = conduit.getTransmitionTextureForState(component);

          BoundingBox[] cubes = toCubes(component.bound);
          for (BoundingBox cube : cubes) {
            drawSection(cube, tex.getMinU(), tex.getMaxU(), tex.getMinV(), tex.getMaxV(), component.dir, true);
          }
        }
      }
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

  private void calculateRatios(LiquidConduit conduit) {
    ConduitTank tank = conduit.getTank();
    int totalAmount = tank.getFluidAmount();

    int upCapacity = 0;
    if(conduit.containsConduitConnection(ForgeDirection.UP) || conduit.containsExternalConnection(ForgeDirection.UP)) {
      upCapacity = LiquidConduit.VOLUME_PER_CONNECTION;
    }
    int downCapacity = 0;
    if(conduit.containsConduitConnection(ForgeDirection.DOWN) || conduit.containsExternalConnection(ForgeDirection.DOWN)) {
      downCapacity = LiquidConduit.VOLUME_PER_CONNECTION;
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
