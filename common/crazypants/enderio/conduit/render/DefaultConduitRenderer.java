package crazypants.enderio.conduit.render;

import static crazypants.render.CubeRenderer.addVecWithUV;
import static crazypants.render.CubeRenderer.setupVertices;
import static crazypants.render.CubeRenderer.verts;
import static net.minecraftforge.common.ForgeDirection.DOWN;
import static net.minecraftforge.common.ForgeDirection.EAST;
import static net.minecraftforge.common.ForgeDirection.NORTH;
import static net.minecraftforge.common.ForgeDirection.SOUTH;
import static net.minecraftforge.common.ForgeDirection.UP;
import static net.minecraftforge.common.ForgeDirection.WEST;

import java.util.Collection;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.render.BoundingBox;

public class DefaultConduitRenderer implements ConduitRenderer {

  protected float transmissionScaleFactor;

  @Override
  public boolean isRendererForConduit(IConduit conduit) {
    return true;
  }

  @Override
  public void renderEntity(ConduitBundleRenderer conduitBundleRenderer, IConduitBundle te, IConduit conduit, double x, double y, double z, float partialTick,
      float worldLight) {

    Collection<CollidableComponent> components = conduit.getCollidableComponents();
    Tessellator tessellator = Tessellator.instance;

    transmissionScaleFactor = conduit.getTransmitionGeometryScale();

    Icon tex;
    boolean active = conduit.isActive();
    for (CollidableComponent component : components) {
      if(renderComponent(component)) {
        float selfIllum = Math.max(worldLight, conduit.getSelfIlluminationForState(component));
        if(active && isNSEWUP(component.dir) &&
            conduit.getTransmitionTextureForState(component) != null) {
          tessellator.setColorRGBA_F(selfIllum + 0.1f, selfIllum + 0.1f,
              selfIllum + 0.1f, 0.75f);
          tex = conduit.getTransmitionTextureForState(component);
          renderTransmission(tex, component, selfIllum);
        }

        tex = conduit.getTextureForState(component);
        if(tex != null) {
          tessellator.setColorOpaque_F(selfIllum, selfIllum, selfIllum);
          renderConduit(tex, conduit, component, selfIllum);
        }
      }

    }

  }

  protected void renderConduit(Icon tex, IConduit conduit, CollidableComponent component, float selfIllum) {
    if(isNSEWUP(component.dir)) {
      RoundedSegmentRenderer.renderSegment(component.dir, component.bound, tex.getMinU(), tex.getMaxU(), tex.getMinV(), tex.getMaxV());
    } else {
      drawSection(component.bound, tex.getMinU(), tex.getMaxU(), tex.getMinV(), tex.getMaxV(), component.dir, true);
    }
  }

  protected void renderTransmission(Icon tex, CollidableComponent component, float selfIllum) {
    RoundedSegmentRenderer.renderSegment(component.dir, component.bound, tex.getMinU(), tex.getMaxU(), tex.getMinV(), tex.getMaxV());
  }

  protected boolean renderComponent(CollidableComponent component) {
    return true;
  }

  protected boolean isNSEWUP(ForgeDirection dir) {
    return dir == NORTH || dir == SOUTH || dir == EAST || dir == WEST || dir == UP || dir == DOWN;
  }

  protected void drawSection(BoundingBox bound, float minU, float maxU, float minV, float maxV, ForgeDirection dir, boolean isTransmission) {

    setupVertices(bound);

    Tessellator tessellator = Tessellator.instance;

    if(isTransmission) {
      setVerticesForTransmission(bound, dir);
    }

    if(dir == NORTH || dir == UP || dir == EAST) { // maintain consistent
                                                   // texture
      // dir relative to the cneter
      // of the conduit
      float tmp = minU;
      minU = maxU;
      maxU = tmp;
    }

    boolean rotateSides = dir == UP || dir == DOWN;
    boolean rotateTopBottom = dir == NORTH || dir == SOUTH;

    if(dir != NORTH && dir != SOUTH) {
      tessellator.setNormal(0, 0, -1);
      if(rotateSides) {
        addVecWithUV(verts[1], maxU, maxV);
        addVecWithUV(verts[0], maxU, minV);
        addVecWithUV(verts[3], minU, minV);
        addVecWithUV(verts[2], minU, maxV);
      } else {
        addVecWithUV(verts[1], minU, minV);
        addVecWithUV(verts[0], maxU, minV);
        addVecWithUV(verts[3], maxU, maxV);
        addVecWithUV(verts[2], minU, maxV);
      }
      if(dir == WEST || dir == EAST) {
        float tmp = minU;
        minU = maxU;
        maxU = tmp;
      }
      tessellator.setNormal(0, 0, 1);
      if(rotateSides) {
        addVecWithUV(verts[4], maxU, maxV);
        addVecWithUV(verts[5], maxU, minV);
        addVecWithUV(verts[6], minU, minV);
        addVecWithUV(verts[7], minU, maxV);
      } else {
        addVecWithUV(verts[4], minU, minV);
        addVecWithUV(verts[5], maxU, minV);
        addVecWithUV(verts[6], maxU, maxV);
        addVecWithUV(verts[7], minU, maxV);
      }
      if(dir == WEST || dir == EAST) {
        float tmp = minU;
        minU = maxU;
        maxU = tmp;
      }
    }

    if(dir != UP && dir != DOWN) {

      tessellator.setNormal(0, 1, 0);
      if(rotateTopBottom) {
        addVecWithUV(verts[6], maxU, maxV);
        addVecWithUV(verts[2], minU, maxV);
        addVecWithUV(verts[3], minU, minV);
        addVecWithUV(verts[7], maxU, minV);
      } else {
        addVecWithUV(verts[6], minU, minV);
        addVecWithUV(verts[2], minU, maxV);
        addVecWithUV(verts[3], maxU, maxV);
        addVecWithUV(verts[7], maxU, minV);
      }

      tessellator.setNormal(0, -1, 0);
      if(rotateTopBottom) {
        addVecWithUV(verts[0], minU, minV);
        addVecWithUV(verts[1], minU, maxV);
        addVecWithUV(verts[5], maxU, maxV);
        addVecWithUV(verts[4], maxU, minV);
      } else {
        addVecWithUV(verts[0], maxU, maxV);
        addVecWithUV(verts[1], minU, maxV);
        addVecWithUV(verts[5], minU, minV);
        addVecWithUV(verts[4], maxU, minV);
      }
    }

    if(dir != EAST && dir != WEST) {

      // if(id == NORTH) {
      // float tmp = minU;
      // minU = maxU;
      // maxU = tmp;
      // }

      tessellator.setNormal(1, 0, 0);
      if(rotateSides) {
        addVecWithUV(verts[2], minU, maxV);
        addVecWithUV(verts[6], minU, minV);
        addVecWithUV(verts[5], maxU, minV);
        addVecWithUV(verts[1], maxU, maxV);
      } else {
        addVecWithUV(verts[2], minU, maxV);
        addVecWithUV(verts[6], maxU, maxV);
        addVecWithUV(verts[5], maxU, minV);
        addVecWithUV(verts[1], minU, minV);
      }

      tessellator.setNormal(-1, 0, 0);
      if(rotateSides) {
        addVecWithUV(verts[0], maxU, maxV);
        addVecWithUV(verts[4], maxU, minV);
        addVecWithUV(verts[7], minU, minV);
        addVecWithUV(verts[3], minU, maxV);
      } else {
        addVecWithUV(verts[0], minU, minV);
        addVecWithUV(verts[4], maxU, minV);
        addVecWithUV(verts[7], maxU, maxV);
        addVecWithUV(verts[3], minU, maxV);
      }
    }
  }

  protected void setVerticesForTransmission(BoundingBox bound, ForgeDirection dir) {
    float xs = dir.offsetX == 0 ? transmissionScaleFactor : 1;
    float ys = dir.offsetY == 0 ? transmissionScaleFactor : 1;
    float zs = dir.offsetZ == 0 ? transmissionScaleFactor : 1;
    setupVertices(bound.scale(xs, ys, zs));
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

}
