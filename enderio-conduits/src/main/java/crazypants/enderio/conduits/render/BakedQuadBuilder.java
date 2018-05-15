package crazypants.enderio.conduits.render;

import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vector4f;
import com.enderio.core.common.vecmath.Vertex;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

//I am using this class instead of RenderUtil directly in case I decide to cache the BakedQuads
public class BakedQuadBuilder {

  public static void addBakedQuads(@Nonnull List<BakedQuad> quads, @Nonnull BoundingBox bound, @Nonnull TextureAtlasSprite tex) {
    RenderUtil.addBakedQuads(quads, bound, tex);
  }
  
  public static void addBakedQuads(@Nonnull List<BakedQuad> quads, @Nonnull BoundingBox bound, @Nonnull Vector4f uvs, @Nonnull TextureAtlasSprite tex) {
    RenderUtil.addBakedQuads(quads, bound, uvs, tex);
  }

  public static void addBakedQuads(@Nonnull List<BakedQuad> quads, @Nonnull BoundingBox bound, @Nonnull TextureAtlasSprite tex, Vector4f color) {
    RenderUtil.addBakedQuads(quads, bound, tex, color);
  }

  public static void addBakedQuads(@Nonnull List<BakedQuad> quads, @Nonnull List<Vertex> vertices, @Nonnull TextureAtlasSprite tex, Vector4f color) {
    RenderUtil.addBakedQuads(quads, vertices, tex, color);
  }

  public static void addBakedQuadForFace(@Nonnull List<BakedQuad> quads, @Nonnull BoundingBox bb, @Nonnull TextureAtlasSprite tex, @Nonnull EnumFacing dir) {
    RenderUtil.addBakedQuadForFace(quads, bb, tex, dir);
  }
  
  public static void addBakedQuadForFace(@Nonnull List<BakedQuad> quads, @Nonnull BoundingBox bb, @Nonnull TextureAtlasSprite tex, @Nonnull EnumFacing dir,
      @Nonnull Vector4f uvs) {
    RenderUtil.addBakedQuadForFace(quads, bb, tex, dir, uvs);
  }

  public static void addBakedQuadForFace(@Nonnull List<BakedQuad> quads, @Nonnull BoundingBox bb, @Nonnull TextureAtlasSprite tex, @Nonnull EnumFacing face,
      boolean rotateUV, boolean flipU, Vector4f color) {
    RenderUtil.addBakedQuadForFace(quads, bb, tex, face, null, rotateUV, flipU, true, color);
  }
  
  public static void addBakedQuadForFace(@Nonnull List<BakedQuad> quads, @Nonnull BoundingBox bb, @Nonnull TextureAtlasSprite tex, @Nonnull EnumFacing face,
      @Nonnull Vector4f uvs, boolean rotateUV, boolean flipU, Vector4f color) {
    RenderUtil.addBakedQuadForFace(quads, bb, tex, face, uvs, null, rotateUV, flipU, true, color);
  }

}
