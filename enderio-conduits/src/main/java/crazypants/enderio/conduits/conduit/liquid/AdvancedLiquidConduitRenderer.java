package crazypants.enderio.conduits.conduit.liquid;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vector3d;
import com.enderio.core.common.vecmath.Vector4f;
import com.enderio.core.common.vecmath.Vertex;

import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.base.conduit.geom.Offset;
import crazypants.enderio.conduits.geom.ConnectionModeGeometry;
import crazypants.enderio.conduits.render.BakedQuadBuilder;
import crazypants.enderio.conduits.render.ConduitBundleRenderManager;
import crazypants.enderio.conduits.render.DefaultConduitRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;

public class AdvancedLiquidConduitRenderer extends DefaultConduitRenderer {

  @Override
  public boolean isRendererForConduit(@Nonnull IConduit conduit) {
    return conduit instanceof AdvancedLiquidConduit;
  }

  @Override
  protected void addConduitQuads(@Nonnull IConduitBundle bundle, @Nonnull IConduit conduit, @Nonnull TextureAtlasSprite tex,
      @Nonnull CollidableComponent component, float selfIllum, BlockRenderLayer layer, @Nonnull List<BakedQuad> quads) {
    super.addConduitQuads(bundle, conduit, tex, component, selfIllum, layer, quads);

    if (!isNSEWUD(component.dir)) {
      return;
    }

    AdvancedLiquidConduit lc = (AdvancedLiquidConduit) conduit;

    for (EnumFacing dir : conduit.getExternalConnections()) {
      TextureAtlasSprite ioTex = null;
      if (conduit.getConnectionMode(dir) == ConnectionMode.INPUT) {
        ioTex = lc.getTextureForInputMode();
      } else if (conduit.getConnectionMode(dir) == ConnectionMode.OUTPUT) {
        ioTex = lc.getTextureForOutputMode();
      }
      if (ioTex != null) {
        Offset offset = bundle.getOffset(ILiquidConduit.class, dir);
        ConnectionModeGeometry.addModeConnectorQuads(dir, offset, ioTex, new Vector4f(1, 1, 1, 1), quads);
      }
    }

    FluidStack fluid = lc.getFluidType();
    @Nonnull
    TextureAtlasSprite texture = fluid != null ? RenderUtil.getStillTexture(fluid) : lc.getNotSetEdgeTexture();

    float scaleFactor = 0.75f;
    float xLen = Math.abs(component.dir.getFrontOffsetX()) == 1 ? 1 : scaleFactor;
    float yLen = Math.abs(component.dir.getFrontOffsetY()) == 1 ? 1 : scaleFactor;
    float zLen = Math.abs(component.dir.getFrontOffsetZ()) == 1 ? 1 : scaleFactor;

    BoundingBox cube = component.bound;
    BoundingBox bb = cube.scale(xLen, yLen, zLen);

    List<Vertex> vertices = new ArrayList<Vertex>();
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

        double sideScale = Math.max(bb.sizeX(), bb.sizeY()) * 2 / 16f;
        sideScale = Math.max(sideScale, bb.sizeZ() * 2 / 16f);
        double width = Math.min(bb.sizeX(), bb.sizeY()) * 15f / 16f;

        List<Vertex> corners = bb.getCornersWithUvForFace(d, minU, maxU, minV, maxV);
        moveEdgeCorners(corners, vDir, width);
        moveEdgeCorners(corners, component.dir.getOpposite(), sideScale);
        for (Vertex c : corners) {
          vertices.add(c);
        }

        corners = bb.getCornersWithUvForFace(d, minU, maxU, minV, maxV);
        moveEdgeCorners(corners, vDir.getOpposite(), width);
        moveEdgeCorners(corners, component.dir.getOpposite(), sideScale);
        for (Vertex c : corners) {
          vertices.add(c);
        }

      }
    }

    if (conduit.getConnectionMode(component.dir) == ConnectionMode.DISABLED) {
      tex = ConduitBundleRenderManager.instance.getConnectorIcon(component.data);
      List<Vertex> corners = component.bound.getCornersWithUvForFace(component.dir, tex.getMinU(), tex.getMaxU(), tex.getMinV(), tex.getMaxV());
      for (Vertex c : corners) {
        vertices.add(c);
      }
      // back face
      for (int i = corners.size() - 1; i >= 0; i--) {
        Vertex c = corners.get(i);
        vertices.add(c);
      }
    }

    BakedQuadBuilder.addBakedQuads(quads, vertices, texture, null);

  }

  private void moveEdgeCorners(List<Vertex> vertices, EnumFacing edge, double scaleFactor) {
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
