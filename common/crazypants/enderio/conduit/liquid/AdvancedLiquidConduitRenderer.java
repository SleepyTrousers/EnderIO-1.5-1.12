package crazypants.enderio.conduit.liquid;

import static crazypants.render.CubeRenderer.addVecWithUV;

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
import crazypants.enderio.conduit.geom.ConnectionModeGeometry;
import crazypants.enderio.conduit.geom.Offset;
import crazypants.enderio.conduit.render.ConduitBundleRenderer;
import crazypants.enderio.conduit.render.DefaultConduitRenderer;
import crazypants.render.BoundingBox;
import crazypants.render.RenderUtil;
import crazypants.vecmath.Vector3d;
import crazypants.vecmath.Vertex;

public class AdvancedLiquidConduitRenderer extends DefaultConduitRenderer {

  @Override
  public boolean isRendererForConduit(IConduit conduit) {
    return conduit instanceof AdvancedLiquidConduit;
  }

  @Override
  public void renderEntity(ConduitBundleRenderer conduitBundleRenderer, IConduitBundle te, IConduit conduit, double x, double y, double z, float partialTick,
      float worldLight) {
    super.renderEntity(conduitBundleRenderer, te, conduit, x, y, z, partialTick, worldLight);

    if(!conduit.hasConnectionMode(ConnectionMode.INPUT) && !conduit.hasConnectionMode(ConnectionMode.OUTPUT)) {
      return;
    }
    AdvancedLiquidConduit pc = (AdvancedLiquidConduit) conduit;
    for (ForgeDirection dir : conduit.getExternalConnections()) {
      Icon tex = null;
      if(conduit.getConectionMode(dir) == ConnectionMode.INPUT) {
        tex = pc.getTextureForInputMode();
      } else if(conduit.getConectionMode(dir) == ConnectionMode.OUTPUT) {
        tex = pc.getTextureForOutputMode();
      }
      if(tex != null) {
        Offset offset = te.getOffset(ILiquidConduit.class, dir);
        ConnectionModeGeometry.renderModeConnector(dir, offset, tex, true);
      }
    }
  }

  @Override
  protected void renderConduit(Icon tex, IConduit conduit, CollidableComponent component, float brightness) {
    super.renderConduit(tex, conduit, component, brightness);

    if(isNSEWUD(component.dir)) {
      AdvancedLiquidConduit lc = (AdvancedLiquidConduit) conduit;

      FluidStack fluid = lc.getFluidType();
      Icon texture = null;
      if(fluid != null) {
        texture = fluid.getFluid().getStillIcon();
        if(texture == null) {
          texture = fluid.getFluid().getIcon();
        }
      }

      if(texture == null) {
        texture = lc.getNotSetEdgeTexture();
      }

      float scaleFactor = 0.75f;
      float xLen = Math.abs(component.dir.offsetX) == 1 ? 1 : scaleFactor;
      float yLen = Math.abs(component.dir.offsetY) == 1 ? 1 : scaleFactor;
      float zLen = Math.abs(component.dir.offsetZ) == 1 ? 1 : scaleFactor;

      BoundingBox cube = component.bound;
      BoundingBox bb = cube.scale(xLen, yLen, zLen);

      for (ForgeDirection d : ForgeDirection.VALID_DIRECTIONS) {
        if(d != component.dir && d != component.dir.getOpposite()) {

          ForgeDirection vDir = RenderUtil.getVDirForFace(d);
          if(component.dir == ForgeDirection.UP || component.dir == ForgeDirection.DOWN) {
            vDir = RenderUtil.getUDirForFace(d);
          } else if((component.dir == ForgeDirection.NORTH || component.dir == ForgeDirection.SOUTH) && d.offsetY != 0) {
            vDir = RenderUtil.getUDirForFace(d);
          }

          float minU = texture.getMinU();
          float maxU = texture.getMaxU();
          float minV = texture.getMinV();
          float maxV = texture.getMaxV();

          float sideScale = Math.max(bb.sizeX(), bb.sizeY()) * 2 / 16f;
          sideScale = Math.max(sideScale, bb.sizeZ() * 2 / 16f);
          float width = Math.min(bb.sizeX(), bb.sizeY()) * 15f / 16f;

          List<Vertex> corners = bb.getCornersWithUvForFace(d, minU, maxU, minV, maxV);
          moveEdgeCorners(corners, vDir, width);
          moveEdgeCorners(corners, component.dir.getOpposite(), sideScale);
          for (Vertex c : corners) {
            addVecWithUV(c.xyz, c.uv.x, c.uv.y);
          }

          corners = bb.getCornersWithUvForFace(d, minU, maxU, minV, maxV);
          moveEdgeCorners(corners, vDir.getOpposite(), width);
          moveEdgeCorners(corners, component.dir.getOpposite(), sideScale);
          for (Vertex c : corners) {
            addVecWithUV(c.xyz, c.uv.x, c.uv.y);
          }

        }
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

  }

  @Override
  protected void renderTransmission(IConduit conduit, Icon tex, CollidableComponent component, float selfIllum) {
    super.renderTransmission(conduit, tex, component, selfIllum);
  }

  private void moveEdgeCorners(List<Vertex> vertices, ForgeDirection edge, float scaleFactor) {
    int[] indices = getClosest(edge, vertices);
    vertices.get(indices[0]).xyz.x -= scaleFactor * edge.offsetX;
    vertices.get(indices[1]).xyz.x -= scaleFactor * edge.offsetX;
    vertices.get(indices[0]).xyz.y -= scaleFactor * edge.offsetY;
    vertices.get(indices[1]).xyz.y -= scaleFactor * edge.offsetY;
    vertices.get(indices[0]).xyz.z -= scaleFactor * edge.offsetZ;
    vertices.get(indices[1]).xyz.z -= scaleFactor * edge.offsetZ;
  }

  private int[] getClosest(ForgeDirection edge, List<Vertex> vertices) {
    int[] res = new int[] { -1, -1 };
    boolean highest = edge.offsetX > 0 || edge.offsetY > 0 || edge.offsetZ > 0;
    double minMax = highest ? -Double.MAX_VALUE : Double.MAX_VALUE;
    int index = 0;
    for (Vertex v : vertices) {
      double val = get(v.xyz, edge);
      if(highest ? val >= minMax : val <= minMax) {
        if(val != minMax) {
          res[0] = index;
        } else {
          res[1] = index;
        }
        minMax = val;
      }
      index++;
    }
    return res;
  }

  private double get(Vector3d xyz, ForgeDirection edge) {
    if(edge == ForgeDirection.EAST || edge == ForgeDirection.WEST) {
      return xyz.x;
    }
    if(edge == ForgeDirection.UP || edge == ForgeDirection.DOWN) {
      return xyz.y;
    }
    return xyz.z;
  }

}
