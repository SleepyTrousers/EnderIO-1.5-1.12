package crazypants.enderio.conduit.liquid;

import java.util.List;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vector3d;
import com.enderio.core.common.vecmath.Vertex;

import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.render.DefaultConduitRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;

public class AdvancedLiquidConduitRenderer extends DefaultConduitRenderer {

  @Override
  public boolean isRendererForConduit(IConduit conduit) {
    return conduit instanceof AdvancedLiquidConduit;
  }

  @Override
  protected void renderConduit(TextureAtlasSprite tex, IConduit conduit, CollidableComponent component, float brightness) {
    super.renderConduit(tex, conduit, component, brightness);

    if (!isNSEWUD(component.dir)) {
      return;
    }
    
      AdvancedLiquidConduit lc = (AdvancedLiquidConduit) conduit;

      FluidStack fluid = lc.getFluidType();
      TextureAtlasSprite texture = RenderUtil.getStillTexture(fluid);      
      if (texture == null) {
        texture = lc.getNotSetEdgeTexture();
      }

      float scaleFactor = 0.75f;
      float xLen = Math.abs(component.dir.getFrontOffsetX()) == 1 ? 1 : scaleFactor;
      float yLen = Math.abs(component.dir.getFrontOffsetY()) == 1 ? 1 : scaleFactor;
      float zLen = Math.abs(component.dir.getFrontOffsetZ()) == 1 ? 1 : scaleFactor;

      BoundingBox cube = component.bound;
      BoundingBox bb = cube.scale(xLen, yLen, zLen);

      for (EnumFacing d : EnumFacing.VALUES) {
        if (d != component.dir && d != component.dir.getOpposite()) {

          EnumFacing vDir = RenderUtil.getVDirForFace(d);
          if (component.dir == EnumFacing.UP || component.dir == EnumFacing.DOWN) {
            vDir = RenderUtil.getUDirForFace(d);
          } else if ((component.dir == EnumFacing.NORTH || component.dir == EnumFacing.SOUTH) && d.getFrontOffsetY() != 0) {
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

      if (conduit.getConnectionMode(component.dir) == ConnectionMode.DISABLED) {
//        tex = EnderIO.blockConduitBundle.getConnectorIcon(component.data);
//        List<Vertex> corners = component.bound.getCornersWithUvForFace(component.dir, tex.getMinU(), tex.getMaxU(), tex.getMinV(), tex.getMaxV());
//        Tessellator tessellator = Tessellator.instance;
//        for (Vertex c : corners) {
//          addVecWithUV(c.xyz, c.uv.x, c.uv.y);
//        }
//        //back face
//        for (int i = corners.size() - 1; i >= 0; i--) {
//          Vertex c = corners.get(i);
//          addVecWithUV(c.xyz, c.uv.x, c.uv.y);
//        }
      }

  }

  @Override
  protected void renderTransmission(IConduit conduit, TextureAtlasSprite tex, CollidableComponent component, float selfIllum) {
    super.renderTransmission(conduit, tex, component, selfIllum);
  }

  private void moveEdgeCorners(List<Vertex> vertices, EnumFacing edge, float scaleFactor) {
    int[] indices = getClosest(edge, vertices);
    vertices.get(indices[0]).xyz.x -= scaleFactor * edge.getFrontOffsetX();
    vertices.get(indices[1]).xyz.x -= scaleFactor * edge.getFrontOffsetX();
    vertices.get(indices[0]).xyz.y -= scaleFactor * edge.getFrontOffsetY();
    vertices.get(indices[1]).xyz.y -= scaleFactor * edge.getFrontOffsetY();
    vertices.get(indices[0]).xyz.z -= scaleFactor * edge.getFrontOffsetZ();
    vertices.get(indices[1]).xyz.z -= scaleFactor * edge.getFrontOffsetZ();
  }

  private int[] getClosest(EnumFacing edge, List<Vertex> vertices) {
    int[] res = new int[] { -1, -1 };
    boolean highest = edge.getFrontOffsetX() > 0 || edge.getFrontOffsetY() > 0 || edge.getFrontOffsetZ() > 0;
    double minMax = highest ? -Double.MAX_VALUE : Double.MAX_VALUE;
    int index = 0;
    for (Vertex v : vertices) {
      double val = get(v.xyz, edge);
      if (highest ? val >= minMax : val <= minMax) {
        if (val != minMax) {
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

  private double get(Vector3d xyz, EnumFacing edge) {
    if (edge == EnumFacing.EAST || edge == EnumFacing.WEST) {
      return xyz.x;
    }
    if (edge == EnumFacing.UP || edge == EnumFacing.DOWN) {
      return xyz.y;
    }
    return xyz.z;
  }

}
