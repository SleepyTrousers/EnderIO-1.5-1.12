package crazypants.enderio.conduits.render;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.reflect.ConstructorUtils;

import com.enderio.core.api.client.render.VertexTransform;
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vector3d;
import com.enderio.core.common.vecmath.Vector4f;
import com.enderio.core.common.vecmath.Vertex;

import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.base.conduit.IConduitRenderer;
import crazypants.enderio.base.conduit.IClientConduit.WithDefaultRendering;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;

import static net.minecraft.util.EnumFacing.DOWN;
import static net.minecraft.util.EnumFacing.EAST;
import static net.minecraft.util.EnumFacing.NORTH;
import static net.minecraft.util.EnumFacing.SOUTH;
import static net.minecraft.util.EnumFacing.UP;
import static net.minecraft.util.EnumFacing.WEST;

public class DefaultConduitRenderer implements IConduitRenderer {

  static final Vector3d[] verts = new Vector3d[8];

  static {
    for (int i = 0; i < verts.length; i++) {
      verts[i] = new Vector3d();
    }
  }

  protected float transmissionScaleFactor;

  @Override
  public boolean isRendererForConduit(@Nonnull IConduit conduit) {
    return true;
  }

  protected boolean renderComponent(CollidableComponent component) {
    return true;
  }

  // ------------ Static Model ---------------------------------------------

  @Override
  public void addBakedQuads(@Nonnull TileEntitySpecialRenderer<?> conduitBundleRenderer, @Nonnull IConduitBundle bundle,
      @Nonnull IClientConduit.WithDefaultRendering conduit, float brightness, @Nullable BlockRenderLayer layer, List<BakedQuad> quads) {

    Collection<CollidableComponent> components = conduit.getCollidableComponents();
    transmissionScaleFactor = conduit.getTransmitionGeometryScale();
    for (CollidableComponent component : components) {
      if (renderComponent(component)) {
        float selfIllum = Math.max(brightness, conduit.getSelfIlluminationForState(component));
        final TextureAtlasSprite transmitionTextureForState = conduit.getTransmitionTextureForState(component);
        if (layer != null && isNSEWUD(component.dir) && transmitionTextureForState != null) {
          Vector4f color = conduit.getTransmitionTextureColorForState(component);
          addTransmissionQuads(transmitionTextureForState, color, layer, conduit, component, selfIllum, quads);
        }
        TextureAtlasSprite tex = conduit.getTextureForState(component);
        if (tex == null) {
          tex = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
        }
        addConduitQuads(bundle, conduit, tex, component, selfIllum, layer, quads);
      }
    }
  }
  
  protected BlockRenderLayer getConduitQuadsLayer() {
    return BlockRenderLayer.CUTOUT;
  }
  
  protected BlockRenderLayer getTransmissionQuadsLayer() {
    return BlockRenderLayer.CUTOUT;
  }
  
  @Override
  public BlockRenderLayer getCoreLayer() {
    return getConduitQuadsLayer();
  }
  
  @Override
  public boolean canRenderInLayer(WithDefaultRendering con, BlockRenderLayer layer) {
    return layer == getConduitQuadsLayer() || layer == getTransmissionQuadsLayer();
  }

  protected void addConduitQuads(@Nonnull IConduitBundle bundle, @Nonnull IConduit conduit, @Nonnull TextureAtlasSprite tex,
      @Nonnull CollidableComponent component, float selfIllum, BlockRenderLayer layer, @Nonnull List<BakedQuad> quads) {
    if (isNSEWUD(component.dir)) {
      if (layer != getConduitQuadsLayer()) {
        return; // TODO? null is the blockbreaking animation
      }

      float shrink = 1 / 32f;
      float xLen = Math.abs(component.dir.getFrontOffsetX()) == 1 ? 0 : shrink;
      float yLen = Math.abs(component.dir.getFrontOffsetY()) == 1 ? 0 : shrink;
      float zLen = Math.abs(component.dir.getFrontOffsetZ()) == 1 ? 0 : shrink;

      BoundingBox cube = component.bound;
      BoundingBox bb = cube.expand(-xLen, -yLen, -zLen);
      addQuadsForSection(bb, tex, component.dir, quads);
      if (conduit.getConnectionMode(component.dir) == ConnectionMode.DISABLED) {
        tex = ConduitBundleRenderManager.instance.getConnectorIcon(component.data);
        BakedQuadBuilder.addBakedQuadForFace(quads, bb, tex, component.dir);
      }
    } else {
      BakedQuadBuilder.addBakedQuads(quads, component.bound, tex);
    }
  }

  protected void addQuadsForSection(BoundingBox bb, TextureAtlasSprite tex, EnumFacing dir, List<BakedQuad> quads) {
    addQuadsForSection(bb, tex, dir, quads, null);
  }

  protected void addQuadsForSection(BoundingBox bb, TextureAtlasSprite tex, EnumFacing dir, List<BakedQuad> quads, Vector4f color) {

    boolean rotateSides = dir == UP || dir == DOWN;
    boolean rotateTopBottom = dir == DOWN || dir == EAST || dir == SOUTH;

    for (EnumFacing face : EnumFacing.VALUES) {
      if (face != dir && face.getOpposite() != dir) {
        boolean doRotSides = rotateSides;
        boolean doRotateTopBottom = rotateTopBottom;
        if (face == UP || face == DOWN) {
          doRotSides = dir == SOUTH || dir == NORTH;
        }
        if (dir.getAxis().isVertical() && (face == NORTH || face == EAST)) {
          doRotateTopBottom = !doRotateTopBottom;
        }
        if (dir.getAxis() == Axis.Z && face == DOWN) {
          doRotateTopBottom = !doRotateTopBottom;
        }
        float maxU = 13 / 16f, maxV = 4 / 16f;
        Vector4f uvs = new Vector4f(0, 0, maxU, maxV);
        BakedQuadBuilder.addBakedQuadForFace(quads, bb, tex, face, uvs, doRotSides, doRotateTopBottom, color);
      }
    }
  }

  protected void addTransmissionQuads(TextureAtlasSprite tex, Vector4f color, BlockRenderLayer layer, IConduit conduit, CollidableComponent component, float selfIllum,
      List<BakedQuad> quads) {
    
    if (layer != getTransmissionQuadsLayer()) {
      return;
    }

    float shrink = 1 / 32f;
    float xLen = Math.abs(component.dir.getFrontOffsetX()) == 1 ? 0 : shrink;
    float yLen = Math.abs(component.dir.getFrontOffsetY()) == 1 ? 0 : shrink;
    float zLen = Math.abs(component.dir.getFrontOffsetZ()) == 1 ? 0 : shrink;

    BoundingBox cube = component.bound;
    BoundingBox bb = cube.expand(-xLen, -yLen, -zLen);
    addQuadsForSection(bb, tex, component.dir, quads, color);
  }

  // ------------ Dynamic ---------------------------------------------

  @Override
  public void renderDynamicEntity(@Nonnull TileEntitySpecialRenderer conduitBundleRenderer, @Nonnull IConduitBundle te,
      @Nonnull IClientConduit.WithDefaultRendering conduit, double x, double y, double z, float partialTick, float worldLight) {

    Collection<CollidableComponent> components = conduit.getCollidableComponents();
    transmissionScaleFactor = conduit.getTransmitionGeometryScale();
    for (CollidableComponent component : components) {
      if (renderComponent(component)) {
        float selfIllum = Math.max(worldLight, conduit.getSelfIlluminationForState(component));
        final TextureAtlasSprite transmitionTextureForState = conduit.getTransmitionTextureForState(component);
        if (isNSEWUD(component.dir) && transmitionTextureForState != null) {
          Vector4f color = conduit.getTransmitionTextureColorForState(component);
          renderTransmissionDynamic(conduit, transmitionTextureForState, color, component, selfIllum);
        }

        TextureAtlasSprite tex = conduit.getTextureForState(component);
        if (tex == null) {
          tex = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
        }
        renderConduitDynamic(tex, conduit, component, selfIllum);
      }
    }
  }

  protected void renderConduitDynamic(TextureAtlasSprite tex, IClientConduit.WithDefaultRendering conduit, CollidableComponent component, float brightness) {
    GlStateManager.color(1, 1, 1);
    if (isNSEWUD(component.dir)) {
      float scaleFactor = 0.75f;
      float xLen = Math.abs(component.dir.getFrontOffsetX()) == 1 ? 1 : scaleFactor;
      float yLen = Math.abs(component.dir.getFrontOffsetY()) == 1 ? 1 : scaleFactor;
      float zLen = Math.abs(component.dir.getFrontOffsetZ()) == 1 ? 1 : scaleFactor;

      BoundingBox cube = component.bound;
      BoundingBox bb = cube.scale(xLen, yLen, zLen);
      drawDynamicSection(bb, tex.getMinU(), tex.getMaxU(), tex.getMinV(), tex.getMaxV(), component.dir, false, conduit.shouldMirrorTexture());
      if (conduit.getConnectionMode(component.dir) == ConnectionMode.DISABLED) {
        tex = ConduitBundleRenderManager.instance.getConnectorIcon(component.data);
        List<Vertex> corners = component.bound.getCornersWithUvForFace(component.dir, tex.getMinU(), tex.getMaxU(), tex.getMinV(), tex.getMaxV());
        RenderUtil.addVerticesToTessellator(corners, DefaultVertexFormats.POSITION_TEX, false);
      }
    } else {
      drawDynamicSection(component.bound, tex.getMinU(), tex.getMaxU(), tex.getMinV(), tex.getMaxV(), component.dir, true);
    }

  }

  protected void renderTransmissionDynamic(IConduit conduit, TextureAtlasSprite tex, Vector4f color, CollidableComponent component, float selfIllum) {
    float scaleFactor = 0.6f;
    float xLen = Math.abs(component.dir.getFrontOffsetX()) == 1 ? 1 : scaleFactor;
    float yLen = Math.abs(component.dir.getFrontOffsetY()) == 1 ? 1 : scaleFactor;
    float zLen = Math.abs(component.dir.getFrontOffsetZ()) == 1 ? 1 : scaleFactor;

    GlStateManager.color(1, 1, 1);
    BoundingBox cube = component.bound;
    BoundingBox bb = cube.scale(xLen, yLen, zLen);
    drawDynamicSection(bb, tex.getMinU(), tex.getMaxU(), tex.getMinV(), tex.getMaxV(), color, component.dir, false);
  }

  protected boolean isNSEWUD(EnumFacing dir) {
    return dir != null;
  }

  protected void drawDynamicSection(BoundingBox bound, float minU, float maxU, float minV, float maxV, EnumFacing dir, boolean isTransmission) {
    drawDynamicSection(bound, minU, maxU, minV, maxV, null, dir, isTransmission, true);
  }

  protected void drawDynamicSection(BoundingBox bound, float minU, float maxU, float minV, float maxV, EnumFacing dir, boolean isTransmission,
      boolean mirrorTexture) {
    drawDynamicSection(bound, minU, maxU, minV, maxV, null, dir, isTransmission, mirrorTexture);
  }

  protected void drawDynamicSection(BoundingBox bound, float minU, float maxU, float minV, float maxV, Vector4f color, EnumFacing dir, boolean isTransmission) {
    drawDynamicSection(bound, minU, maxU, minV, maxV, color, dir, isTransmission, true);
  }

  private static final Vector4f NONE = new Vector4f(1f, 1f, 1f, 1f);

  protected void drawDynamicSection(BoundingBox bound, float minU, float maxU, float minV, float maxV, Vector4f color, EnumFacing dir, boolean isTransmission,
      boolean mirrorTexture) {

    if (isTransmission) {
      setVerticesForTransmission(bound, dir);
    } else {
      setupVertices(bound);
    }

    if (mirrorTexture && (dir == EnumFacing.NORTH || dir == UP || dir == EAST)) {
      // maintain consistent texture dir relative to the center of the conduit
      float tmp = minU;
      minU = maxU;
      maxU = tmp;
    }

    if (color == null) {
      color = NONE;
    }

    boolean rotateSides = dir == UP || dir == DOWN;
    boolean rotateTopBottom = dir == NORTH || dir == SOUTH;
    // float cm;
    if (dir != NORTH && dir != SOUTH) {
      // tessellator.setNormal(0, 0, -1);
      if (!isTransmission) {
        // cm = RenderUtil.getColorMultiplierForFace(EnumFacing.NORTH);
        // tessellator.setColorOpaque_F(cm, cm, cm);
      }
      if (rotateSides) {
        addVecWithUV(verts[1], maxU, maxV, color);
        addVecWithUV(verts[0], maxU, minV, color);
        addVecWithUV(verts[3], minU, minV, color);
        addVecWithUV(verts[2], minU, maxV, color);
      } else {
        addVecWithUV(verts[1], minU, minV, color);
        addVecWithUV(verts[0], maxU, minV, color);
        addVecWithUV(verts[3], maxU, maxV, color);
        addVecWithUV(verts[2], minU, maxV, color);
      }
      if (dir == WEST || dir == EAST) {
        float tmp = minU;
        minU = maxU;
        maxU = tmp;
      }
      // tessellator.setNormal(0, 0, 1);
      if (!isTransmission) {
        // cm = RenderUtil.getColorMultiplierForFace(EnumFacing.SOUTH);
        // tessellator.setColorOpaque_F(cm, cm, cm);
      }
      if (rotateSides) {
        addVecWithUV(verts[4], maxU, maxV, color);
        addVecWithUV(verts[5], maxU, minV, color);
        addVecWithUV(verts[6], minU, minV, color);
        addVecWithUV(verts[7], minU, maxV, color);
      } else {
        addVecWithUV(verts[4], minU, minV, color);
        addVecWithUV(verts[5], maxU, minV, color);
        addVecWithUV(verts[6], maxU, maxV, color);
        addVecWithUV(verts[7], minU, maxV, color);
      }
      if (dir == WEST || dir == EAST) {
        float tmp = minU;
        minU = maxU;
        maxU = tmp;
      }
    }

    if (dir != UP && dir != DOWN) {

      // tessellator.setNormal(0, 1, 0);
      if (!isTransmission) {
        // cm = RenderUtil.getColorMultiplierForFace(EnumFacing.UP);
        // tessellator.setColorOpaque_F(cm, cm, cm);
      }
      if (rotateTopBottom) {
        addVecWithUV(verts[6], maxU, maxV, color);
        addVecWithUV(verts[2], minU, maxV, color);
        addVecWithUV(verts[3], minU, minV, color);
        addVecWithUV(verts[7], maxU, minV, color);
      } else {
        addVecWithUV(verts[6], minU, minV, color);
        addVecWithUV(verts[2], minU, maxV, color);
        addVecWithUV(verts[3], maxU, maxV, color);
        addVecWithUV(verts[7], maxU, minV, color);
      }

      // tessellator.setNormal(0, -1, 0);
      if (!isTransmission) {
        // cm = RenderUtil.getColorMultiplierForFace(EnumFacing.DOWN);
        // tessellator.setColorOpaque_F(cm, cm, cm);
      }
      if (rotateTopBottom) {
        addVecWithUV(verts[0], minU, minV, color);
        addVecWithUV(verts[1], minU, maxV, color);
        addVecWithUV(verts[5], maxU, maxV, color);
        addVecWithUV(verts[4], maxU, minV, color);
      } else {
        addVecWithUV(verts[0], maxU, maxV, color);
        addVecWithUV(verts[1], minU, maxV, color);
        addVecWithUV(verts[5], minU, minV, color);
        addVecWithUV(verts[4], maxU, minV, color);
      }
    }

    if (dir != EAST && dir != WEST) {

      // tessellator.setNormal(1, 0, 0);
      if (!isTransmission) {
        // cm = RenderUtil.getColorMultiplierForFace(EnumFacing.EAST);
        // tessellator.setColorOpaque_F(cm, cm, cm);
      }
      if (rotateSides) {
        addVecWithUV(verts[2], minU, maxV, color);
        addVecWithUV(verts[6], minU, minV, color);
        addVecWithUV(verts[5], maxU, minV, color);
        addVecWithUV(verts[1], maxU, maxV, color);
      } else {
        addVecWithUV(verts[2], minU, maxV, color);
        addVecWithUV(verts[6], maxU, maxV, color);
        addVecWithUV(verts[5], maxU, minV, color);
        addVecWithUV(verts[1], minU, minV, color);
      }

      // tessellator.setNormal(-1, 0, 0);
      if (!isTransmission) {
        // cm = RenderUtil.getColorMultiplierForFace(EnumFacing.WEST);
        // tessellator.setColorOpaque_F(cm, cm, cm);
      }
      if (rotateSides) {
        addVecWithUV(verts[0], maxU, maxV, color);
        addVecWithUV(verts[4], maxU, minV, color);
        addVecWithUV(verts[7], minU, minV, color);
        addVecWithUV(verts[3], minU, maxV, color);
      } else {
        addVecWithUV(verts[0], minU, minV, color);
        addVecWithUV(verts[4], maxU, minV, color);
        addVecWithUV(verts[7], maxU, maxV, color);
        addVecWithUV(verts[3], minU, maxV, color);
      }
    }
    // tessellator.setColorOpaque_F(1, 1, 1);

  }

  // This is a really hacky, imprecise and slow way to do this
  public BoundingBox[] toCubes(BoundingBox bb) {

    // NB This on handles the really simple conduit case!

    double width = bb.maxX - bb.minX;
    double height = bb.maxY - bb.minY;
    double depth = bb.maxZ - bb.minZ;

    if (width > 0 && height > 0 && depth > 0) {
      if (width / depth > 1.5f || depth / width > 1.5f) {
        // split horizontally
        if (width > depth) {
          int numSplits = (int) Math.round(width / depth);
          double newWidth = width / numSplits;
          BoundingBox[] result = new BoundingBox[numSplits];
          double lastMax = bb.minX;
          for (int i = 0; i < numSplits; i++) {
            double max = lastMax + newWidth;
            result[i] = new BoundingBox(lastMax, bb.minY, bb.minZ, max, bb.maxY, bb.maxZ);
            lastMax = max;
          }
          return result;

        } else {

          int numSplits = (int) Math.round(depth / width);
          double newWidth = depth / numSplits;
          BoundingBox[] result = new BoundingBox[numSplits];
          double lastMax = bb.minZ;
          for (int i = 0; i < numSplits; i++) {
            double max = lastMax + newWidth;
            result[i] = new BoundingBox(bb.minX, bb.minY, lastMax, bb.maxX, bb.maxY, max);
            lastMax = max;
          }
          return result;

        }

      } else if (height / width > 1.5) {

        int numSplits = (int) Math.round(height / width);
        double newWidth = height / numSplits;
        BoundingBox[] result = new BoundingBox[numSplits];
        double lastMax = bb.minY;
        for (int i = 0; i < numSplits; i++) {
          double max = lastMax + newWidth;
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

  protected void setVerticesForTransmission(BoundingBox bound, EnumFacing dir) {
    float xs = dir.getFrontOffsetX() == 0 ? transmissionScaleFactor : 1;
    float ys = dir.getFrontOffsetY() == 0 ? transmissionScaleFactor : 1;
    float zs = dir.getFrontOffsetZ() == 0 ? transmissionScaleFactor : 1;
    setupVertices(bound.scale(xs, ys, zs));
  }

  protected void addVecWithUV(Vector3d vec, double u, double v, Vector4f color) {
    BufferBuilder tes = Tessellator.getInstance().getBuffer();
    tes.pos(vec.x, vec.y, vec.z).tex(u, v).color(color.x, color.y, color.z, color.w).endVertex();
  }

  protected void setupVertices(BoundingBox bound) {
    setupVertices(bound, null);
  }

  protected void setupVertices(BoundingBox bound, VertexTransform xForm) {
    verts[0].set(bound.minX, bound.minY, bound.minZ);
    verts[1].set(bound.maxX, bound.minY, bound.minZ);
    verts[2].set(bound.maxX, bound.maxY, bound.minZ);
    verts[3].set(bound.minX, bound.maxY, bound.minZ);
    verts[4].set(bound.minX, bound.minY, bound.maxZ);
    verts[5].set(bound.maxX, bound.minY, bound.maxZ);
    verts[6].set(bound.maxX, bound.maxY, bound.maxZ);
    verts[7].set(bound.minX, bound.maxY, bound.maxZ);

    if (xForm != null) {
      for (Vector3d vec : verts) {
        xForm.apply(vec);
      }
    }
  }

}
