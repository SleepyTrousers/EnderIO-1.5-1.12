package crazypants.enderio.conduit.render;

import com.enderio.core.client.render.BoundingBox;

import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.geom.CollidableComponent;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

public class DefaultConduitRenderer implements ConduitRenderer {

  protected float transmissionScaleFactor;

  @Override
  public boolean isRendererForConduit(IConduit conduit) {
    return true;
  }

  @Override
  public void renderDynamicEntity(ConduitBundleRenderer conduitBundleRenderer, IConduitBundle te, IConduit con, double x, double y, double z,
      float partialTick, float worldLight) {

  }

  protected void renderConduit(TextureAtlasSprite tex, IConduit conduit, CollidableComponent component, float brightness) {

    if(isNSEWUD(component.dir)) {

      float scaleFactor = 0.75f;
      float xLen = Math.abs(component.dir.getFrontOffsetX()) == 1 ? 1 : scaleFactor;
      float yLen = Math.abs(component.dir.getFrontOffsetY()) == 1 ? 1 : scaleFactor;
      float zLen = Math.abs(component.dir.getFrontOffsetZ()) == 1 ? 1 : scaleFactor;

      BoundingBox cube = component.bound;
      BoundingBox bb = cube.scale(xLen, yLen, zLen);
      drawSection(bb, tex.getMinU(), tex.getMaxU(), tex.getMinV(), tex.getMaxV(), component.dir, false, conduit.shouldMirrorTexture());

      if(conduit.getConnectionMode(component.dir) == ConnectionMode.DISABLED) {
//        tex = EnderIO.blockConduitBundle.getConnectorIcon(component.data);
//        List<Vertex> corners = component.bound.getCornersWithUvForFace(component.dir, tex.getMinU(), tex.getMaxU(), tex.getMinV(), tex.getMaxV());
//        Tessellator tessellator = Tessellator.instance;
//        for (Vertex c : corners) {
//          CubeRenderer.addVecWithUV(c.xyz, c.uv.x, c.uv.y);
//        }
      }

    } else {
      drawSection(component.bound, tex.getMinU(), tex.getMaxU(), tex.getMinV(), tex.getMaxV(), component.dir, true);
    }

  }

  protected void renderTransmission(IConduit conduit, TextureAtlasSprite tex, CollidableComponent component, float selfIllum) {
    //    RoundedSegmentRenderer.renderSegment(component.dir, component.bound, tex.getMinU(), tex.getMaxU(), tex.getMinV(), tex.getMaxV(),
    //        conduit.getConectionMode(component.dir) == ConnectionMode.DISABLED);

    float scaleFactor = 0.6f;
    float xLen = Math.abs(component.dir.getFrontOffsetX()) == 1 ? 1 : scaleFactor;
    float yLen = Math.abs(component.dir.getFrontOffsetY()) == 1 ? 1 : scaleFactor;
    float zLen = Math.abs(component.dir.getFrontOffsetZ()) == 1 ? 1 : scaleFactor;

    BoundingBox cube = component.bound;
    BoundingBox bb = cube.scale(xLen, yLen, zLen);
    drawSection(bb, tex.getMinU(), tex.getMaxU(), tex.getMinV(), tex.getMaxV(), component.dir, false);
  }

  protected boolean renderComponent(CollidableComponent component) {
    return true;
  }

  protected boolean isNSEWUD(EnumFacing dir) {
    return dir == EnumFacing.NORTH || dir == EnumFacing.SOUTH || dir == EnumFacing.EAST || dir == EnumFacing.WEST || dir == EnumFacing.UP || dir == EnumFacing.DOWN;
  }

  protected void drawSection(BoundingBox bound, float minU, float maxU, float minV, float maxV, EnumFacing dir, boolean isTransmission) {
    drawSection(bound, minU, maxU, minV, maxV, dir, isTransmission, true);
  }

  protected void drawSection(BoundingBox bound, float minU, float maxU, float minV, float maxV, EnumFacing dir,
      boolean isTransmission, boolean mirrorTexture) {

//    Tessellator tessellator = Tessellator.instance;
//
//    if(isTransmission) {
//      setVerticesForTransmission(bound, dir);
//    } else {
//      CubeRenderer.setupVertices(bound);
//    }
//
//    if (mirrorTexture && (dir == NORTH || dir == UP || dir == EAST)) {
//      // maintain consistent texture dir relative to the center of the conduit
//      float tmp = minU;
//      minU = maxU;
//      maxU = tmp;
//    }
//
//    boolean rotateSides = dir == UP || dir == DOWN;
//    boolean rotateTopBottom = dir == NORTH || dir == SOUTH;
//    float cm;
//    if(dir != NORTH && dir != SOUTH) {
//      tessellator.setNormal(0, 0, -1);
//      if(!isTransmission) {
//        cm = RenderUtil.getColorMultiplierForFace(EnumFacing.NORTH);
//        tessellator.setColorOpaque_F(cm, cm, cm);
//      }
//      if(rotateSides) {
//        addVecWithUV(verts[1], maxU, maxV);
//        addVecWithUV(verts[0], maxU, minV);
//        addVecWithUV(verts[3], minU, minV);
//        addVecWithUV(verts[2], minU, maxV);
//      } else {
//        addVecWithUV(verts[1], minU, minV);
//        addVecWithUV(verts[0], maxU, minV);
//        addVecWithUV(verts[3], maxU, maxV);
//        addVecWithUV(verts[2], minU, maxV);
//      }
//      if(dir == WEST || dir == EAST) {
//        float tmp = minU;
//        minU = maxU;
//        maxU = tmp;
//      }
//      tessellator.setNormal(0, 0, 1);
//      if(!isTransmission) {
//        cm = RenderUtil.getColorMultiplierForFace(EnumFacing.SOUTH);
//        tessellator.setColorOpaque_F(cm, cm, cm);
//      }
//      if(rotateSides) {
//        addVecWithUV(verts[4], maxU, maxV);
//        addVecWithUV(verts[5], maxU, minV);
//        addVecWithUV(verts[6], minU, minV);
//        addVecWithUV(verts[7], minU, maxV);
//      } else {
//        addVecWithUV(verts[4], minU, minV);
//        addVecWithUV(verts[5], maxU, minV);
//        addVecWithUV(verts[6], maxU, maxV);
//        addVecWithUV(verts[7], minU, maxV);
//      }
//      if(dir == WEST || dir == EAST) {
//        float tmp = minU;
//        minU = maxU;
//        maxU = tmp;
//      }
//    }
//
//    if(dir != UP && dir != DOWN) {
//
//      tessellator.setNormal(0, 1, 0);
//      if(!isTransmission) {
//        cm = RenderUtil.getColorMultiplierForFace(EnumFacing.UP);
//        tessellator.setColorOpaque_F(cm, cm, cm);
//      }
//      if(rotateTopBottom) {
//        addVecWithUV(verts[6], maxU, maxV);
//        addVecWithUV(verts[2], minU, maxV);
//        addVecWithUV(verts[3], minU, minV);
//        addVecWithUV(verts[7], maxU, minV);
//      } else {
//        addVecWithUV(verts[6], minU, minV);
//        addVecWithUV(verts[2], minU, maxV);
//        addVecWithUV(verts[3], maxU, maxV);
//        addVecWithUV(verts[7], maxU, minV);
//      }
//
//      tessellator.setNormal(0, -1, 0);
//      if(!isTransmission) {
//        cm = RenderUtil.getColorMultiplierForFace(EnumFacing.DOWN);
//        tessellator.setColorOpaque_F(cm, cm, cm);
//      }
//      if(rotateTopBottom) {
//        addVecWithUV(verts[0], minU, minV);
//        addVecWithUV(verts[1], minU, maxV);
//        addVecWithUV(verts[5], maxU, maxV);
//        addVecWithUV(verts[4], maxU, minV);
//      } else {
//        addVecWithUV(verts[0], maxU, maxV);
//        addVecWithUV(verts[1], minU, maxV);
//        addVecWithUV(verts[5], minU, minV);
//        addVecWithUV(verts[4], maxU, minV);
//      }
//    }
//
//    if(dir != EAST && dir != WEST) {
//
//      tessellator.setNormal(1, 0, 0);
//      if(!isTransmission) {
//        cm = RenderUtil.getColorMultiplierForFace(EnumFacing.EAST);
//        tessellator.setColorOpaque_F(cm, cm, cm);
//      }
//      if(rotateSides) {
//        addVecWithUV(verts[2], minU, maxV);
//        addVecWithUV(verts[6], minU, minV);
//        addVecWithUV(verts[5], maxU, minV);
//        addVecWithUV(verts[1], maxU, maxV);
//      } else {
//        addVecWithUV(verts[2], minU, maxV);
//        addVecWithUV(verts[6], maxU, maxV);
//        addVecWithUV(verts[5], maxU, minV);
//        addVecWithUV(verts[1], minU, minV);
//      }
//
//      tessellator.setNormal(-1, 0, 0);
//      if(!isTransmission) {
//        cm = RenderUtil.getColorMultiplierForFace(EnumFacing.WEST);
//        tessellator.setColorOpaque_F(cm, cm, cm);
//      }
//      if(rotateSides) {
//        addVecWithUV(verts[0], maxU, maxV);
//        addVecWithUV(verts[4], maxU, minV);
//        addVecWithUV(verts[7], minU, minV);
//        addVecWithUV(verts[3], minU, maxV);
//      } else {
//        addVecWithUV(verts[0], minU, minV);
//        addVecWithUV(verts[4], maxU, minV);
//        addVecWithUV(verts[7], maxU, maxV);
//        addVecWithUV(verts[3], minU, maxV);
//      }
//    }
//    tessellator.setColorOpaque_F(1, 1, 1);
  }

  protected void setVerticesForTransmission(BoundingBox bound, EnumFacing dir) {
    float xs = dir.getFrontOffsetX() == 0 ? transmissionScaleFactor : 1;
    float ys = dir.getFrontOffsetY() == 0 ? transmissionScaleFactor : 1;
    float zs = dir.getFrontOffsetZ() == 0 ? transmissionScaleFactor : 1;
//    CubeRenderer.setupVertices(bound.scale(xs, ys, zs));
  }

  // TODO: This is a really hacky, imprecise and slow way to do this
  public BoundingBox[] toCubes(BoundingBox bb) {

    // NB This on handles the really simple conduit case!

    float width = bb.maxX - bb.minX;
    float height = bb.maxY - bb.minY;
    float depth = bb.maxZ - bb.minZ;

    if(width > 0 && height > 0 && depth > 0) {
      if(width / depth > 1.5f || depth / width > 1.5f) {
        // split horizontally
        if(width > depth) {
          int numSplits = Math.round(width / depth);
          float newWidth = width / numSplits;
          BoundingBox[] result = new BoundingBox[numSplits];
          float lastMax = bb.minX;
          for (int i = 0; i < numSplits; i++) {
            float max = lastMax + newWidth;
            result[i] = new BoundingBox(lastMax, bb.minY, bb.minZ, max, bb.maxY, bb.maxZ);
            lastMax = max;
          }
          return result;

        } else {

          int numSplits = Math.round(depth / width);
          float newWidth = depth / numSplits;
          BoundingBox[] result = new BoundingBox[numSplits];
          float lastMax = bb.minZ;
          for (int i = 0; i < numSplits; i++) {
            float max = lastMax + newWidth;
            result[i] = new BoundingBox(bb.minX, bb.minY, lastMax, bb.maxX, bb.maxY, max);
            lastMax = max;
          }
          return result;

        }

      } else if(height / width > 1.5) {

        int numSplits = Math.round(height / width);
        float newWidth = height / numSplits;
        BoundingBox[] result = new BoundingBox[numSplits];
        float lastMax = bb.minY;
        for (int i = 0; i < numSplits; i++) {
          float max = lastMax + newWidth;
          result[i] = new BoundingBox(bb.minX, lastMax, bb.minZ, bb.maxX, max, bb.maxZ);
          lastMax = max;
        }
        return result;

      }
    }

    return new BoundingBox[] { bb };
  }

  @Override
  public boolean isDynamic() {
    return false;
  }

}
