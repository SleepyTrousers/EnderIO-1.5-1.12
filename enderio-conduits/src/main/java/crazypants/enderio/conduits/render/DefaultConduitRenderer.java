package crazypants.enderio.conduits.render;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.render.VertexTransform;
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vector3d;
import com.enderio.core.common.vecmath.Vector4f;
import com.enderio.core.common.vecmath.Vertex;

import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.conduit.IClientConduit.WithDefaultRendering;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.base.conduit.IConduitRenderer;
import crazypants.enderio.base.conduit.IConduitTexture;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
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
      @Nonnull IClientConduit.WithDefaultRendering conduit, float brightness, @Nullable BlockRenderLayer layer, @Nonnull List<BakedQuad> quads) {

    Collection<CollidableComponent> components = conduit.getCollidableComponents();
    transmissionScaleFactor = conduit.getTransmitionGeometryScale();
    for (CollidableComponent component : components) {
      if (component != null && renderComponent(component)) {
        float selfIllum = Math.max(brightness, conduit.getSelfIlluminationForState(component));
        final IConduitTexture transmitionTextureForState = conduit.getTransmitionTextureForState(component);
        if (layer != null && component.isDirectional() && transmitionTextureForState != null) {
          Vector4f color = conduit.getTransmitionTextureColorForState(component);
          addTransmissionQuads(transmitionTextureForState, color, layer, conduit, component, selfIllum, quads);
        }
        IConduitTexture tex = conduit.getTextureForState(component);
        addConduitQuads(bundle, conduit, tex, component, selfIllum, layer, quads);
      }
    }
  }

  protected @Nonnull BlockRenderLayer getConduitQuadsLayer() {
    return BlockRenderLayer.CUTOUT;
  }

  protected @Nonnull BlockRenderLayer getTransmissionQuadsLayer() {
    return BlockRenderLayer.CUTOUT;
  }

  @Override
  public @Nonnull BlockRenderLayer getCoreLayer() {
    return getConduitQuadsLayer();
  }

  @Override
  public boolean canRenderInLayer(@Nonnull WithDefaultRendering con, @Nonnull BlockRenderLayer layer) {
    return layer == getConduitQuadsLayer() || layer == getTransmissionQuadsLayer();
  }

  private static final @Nonnull Vector4f COLOR_ERROR = new Vector4f(1, 0, 0, 1);

  protected void addConduitQuads(@Nonnull IConduitBundle bundle, @Nonnull IClientConduit conduit, @Nonnull IConduitTexture tex,
      @Nonnull CollidableComponent component, float selfIllum, BlockRenderLayer layer, @Nonnull List<BakedQuad> quads) {
    if (component.isDirectional()) {
      if (layer != getConduitQuadsLayer()) {
        return; // TODO? null is the blockbreaking animation
      }
      if (component.data != null) {
        return; // this is handled by ConduitInOutRenderer.addColorBand() or the conduits themselves
      }

      float shrink = 1 / 32f;
      final EnumFacing componentDirection = component.getDirection();
      float xLen = Math.abs(componentDirection.getFrontOffsetX()) == 1 ? 0 : shrink;
      float yLen = Math.abs(componentDirection.getFrontOffsetY()) == 1 ? 0 : shrink;
      float zLen = Math.abs(componentDirection.getFrontOffsetZ()) == 1 ? 0 : shrink;

      BoundingBox cube = component.bound;
      BoundingBox bb = cube.expand(-xLen, -yLen, -zLen);
      addQuadsForSection(bb, tex, componentDirection, quads, conduit.renderError() ? COLOR_ERROR : null);
      if (conduit.getConnectionMode(componentDirection) == ConnectionMode.DISABLED) {
        TextureAtlasSprite tex2 = ConduitBundleRenderManager.instance.getConnectorIcon(component.data);
        BakedQuadBuilder.addBakedQuadForFace(quads, bb, tex2, componentDirection);
      }
    } else {
      BakedQuadBuilder.addBakedQuads(quads, component.bound, tex.getSprite());
    }
  }

  protected void addQuadsForSection(@Nonnull BoundingBox bb, @Nonnull IConduitTexture tex, @Nonnull EnumFacing dir, @Nonnull List<BakedQuad> quads,
      @Nullable Vector4f color) {

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
        BakedQuadBuilder.addBakedQuadForFace(quads, bb, tex.getSprite(), face, tex.getUv(), doRotSides, doRotateTopBottom, color);
      }
    }
  }

  protected void addTransmissionQuads(@Nonnull IConduitTexture tex, Vector4f color, @Nonnull BlockRenderLayer layer, @Nonnull IConduit conduit,
      @Nonnull CollidableComponent component, float selfIllum, @Nonnull List<BakedQuad> quads) {

    if (layer != getTransmissionQuadsLayer()) {
      return;
    }

    float shrink = 1 / 32f;
    final EnumFacing componentDirection = component.getDirection();
    float xLen = Math.abs(componentDirection.getFrontOffsetX()) == 1 ? 0 : shrink;
    float yLen = Math.abs(componentDirection.getFrontOffsetY()) == 1 ? 0 : shrink;
    float zLen = Math.abs(componentDirection.getFrontOffsetZ()) == 1 ? 0 : shrink;

    BoundingBox cube = component.bound;
    BoundingBox bb = cube.expand(-xLen, -yLen, -zLen);
    addQuadsForSection(bb, tex, componentDirection, quads, color);
  }

  // ------------ Dynamic ---------------------------------------------

  @Override
  public void renderDynamicEntity(@Nonnull TileEntitySpecialRenderer<?> conduitBundleRenderer, @Nonnull IConduitBundle te,
      @Nonnull IClientConduit.WithDefaultRendering conduit, double x, double y, double z, float partialTick, float worldLight) {

    Collection<CollidableComponent> components = conduit.getCollidableComponents();
    transmissionScaleFactor = conduit.getTransmitionGeometryScale();
    for (CollidableComponent component : components) {
      if (component != null && renderComponent(component)) {
        float selfIllum = Math.max(worldLight, conduit.getSelfIlluminationForState(component));
        final IConduitTexture transmitionTextureForState = conduit.getTransmitionTextureForState(component);
        if (component.isDirectional() && transmitionTextureForState != null) {
          Vector4f color = conduit.getTransmitionTextureColorForState(component);
          renderTransmissionDynamic(conduit, transmitionTextureForState, color, component, selfIllum);
        }

        IConduitTexture tex = conduit.getTextureForState(component);
        renderConduitDynamic(tex, conduit, component, selfIllum);
      }
    }
  }

  protected void renderConduitDynamic(@Nonnull IConduitTexture tex, @Nonnull IClientConduit.WithDefaultRendering conduit,
      @Nonnull CollidableComponent component, float brightness) {
    GlStateManager.color(1, 1, 1);
    if (component.isDirectional() && component.data == null) {
      final EnumFacing componentDirection = component.getDirection();
      float scaleFactor = 0.75f;
      float xLen = Math.abs(componentDirection.getFrontOffsetX()) == 1 ? 1 : scaleFactor;
      float yLen = Math.abs(componentDirection.getFrontOffsetY()) == 1 ? 1 : scaleFactor;
      float zLen = Math.abs(componentDirection.getFrontOffsetZ()) == 1 ? 1 : scaleFactor;

      BoundingBox cube = component.bound;
      BoundingBox bb = cube.scale(xLen, yLen, zLen);
      TextureAtlasSprite sprite = tex.getSprite();
      drawDynamicSection(bb, sprite.getInterpolatedU(tex.getUv().x * 16), sprite.getInterpolatedU(tex.getUv().z * 16),
          sprite.getInterpolatedV(tex.getUv().y * 16), sprite.getInterpolatedV(tex.getUv().w * 16), componentDirection, false, conduit.shouldMirrorTexture());
      if (conduit.getConnectionMode(componentDirection) == ConnectionMode.DISABLED) {
        TextureAtlasSprite tex2 = ConduitBundleRenderManager.instance.getConnectorIcon(component.data);
        List<Vertex> corners = component.bound.getCornersWithUvForFace(componentDirection, tex2.getMinU(), tex2.getMaxU(), tex2.getMinV(), tex2.getMaxV());
        RenderUtil.addVerticesToTessellator(corners, DefaultVertexFormats.POSITION_TEX, false);
      }
    } else {
      // TODO: HL: I commented this out because component.getDirection() (the second to last parameter) is always null in
      // this else branch and drawDynamicSection() with isTransmission=true (last parameter) would NPE on it. (Not a
      // mistake in the component.dir encapsulation, this was that way before.)
      // drawDynamicSection(component.bound, tex.getMinU(), tex.getMaxU(), tex.getMinV(), tex.getMaxV(), dir, true);
    }

  }

  protected void renderTransmissionDynamic(@Nonnull IConduit conduit, @Nonnull IConduitTexture tex, @Nullable Vector4f color,
      @Nonnull CollidableComponent component, float selfIllum) {
    float scaleFactor = 0.6f;
    final EnumFacing componentDirection = component.getDirection();
    float xLen = Math.abs(componentDirection.getFrontOffsetX()) == 1 ? 1 : scaleFactor;
    float yLen = Math.abs(componentDirection.getFrontOffsetY()) == 1 ? 1 : scaleFactor;
    float zLen = Math.abs(componentDirection.getFrontOffsetZ()) == 1 ? 1 : scaleFactor;

    GlStateManager.color(1, 1, 1);
    BoundingBox cube = component.bound;
    BoundingBox bb = cube.scale(xLen, yLen, zLen);
    TextureAtlasSprite sprite = tex.getSprite();
    drawDynamicSection(bb, sprite.getInterpolatedU(tex.getUv().x * 16), sprite.getInterpolatedU(tex.getUv().z * 16),
        sprite.getInterpolatedV(tex.getUv().y * 16), sprite.getInterpolatedV(tex.getUv().w * 16), color, componentDirection, false);
  }

  protected void drawDynamicSection(@Nonnull BoundingBox bound, float minU, float maxU, float minV, float maxV, @Nonnull EnumFacing dir,
      boolean isTransmission) {
    drawDynamicSection(bound, minU, maxU, minV, maxV, null, dir, isTransmission, true);
  }

  protected void drawDynamicSection(@Nonnull BoundingBox bound, float minU, float maxU, float minV, float maxV, @Nonnull EnumFacing dir, boolean isTransmission,
      boolean mirrorTexture) {
    drawDynamicSection(bound, minU, maxU, minV, maxV, null, dir, isTransmission, mirrorTexture);
  }

  protected void drawDynamicSection(@Nonnull BoundingBox bound, float minU, float maxU, float minV, float maxV, @Nullable Vector4f color,
      @Nonnull EnumFacing dir, boolean isTransmission) {
    drawDynamicSection(bound, minU, maxU, minV, maxV, color, dir, isTransmission, true);
  }

  private static final @Nonnull Vector4f NONE = new Vector4f(1f, 1f, 1f, 1f);

  protected void drawDynamicSection(@Nonnull BoundingBox bound, float minU, float maxU, float minV, float maxV, @Nullable Vector4f color,
      @Nonnull EnumFacing dir, boolean isTransmission, boolean mirrorTexture) {

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

  protected void setVerticesForTransmission(@Nonnull BoundingBox bound, @Nonnull EnumFacing dir) {
    float xs = dir.getFrontOffsetX() == 0 ? transmissionScaleFactor : 1;
    float ys = dir.getFrontOffsetY() == 0 ? transmissionScaleFactor : 1;
    float zs = dir.getFrontOffsetZ() == 0 ? transmissionScaleFactor : 1;
    setupVertices(bound.scale(xs, ys, zs));
  }

  protected void addVecWithUV(@Nullable Vector3d vec, double u, double v, @Nonnull Vector4f color) {
    if (vec != null) {
      BufferBuilder tes = Tessellator.getInstance().getBuffer();
      tes.pos(vec.x, vec.y, vec.z).tex(u, v).color(color.x, color.y, color.z, color.w).endVertex();
    }
  }

  protected void setupVertices(@Nonnull BoundingBox bound) {
    setupVertices(bound, null);
  }

  protected void setupVertices(@Nonnull BoundingBox bound, @Nullable VertexTransform xForm) {
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
        if (vec != null) {
          xForm.apply(vec);
        }
      }
    }
  }

}
