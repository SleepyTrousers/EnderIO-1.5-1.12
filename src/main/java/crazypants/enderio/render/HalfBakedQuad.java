package crazypants.enderio.render;

import java.util.List;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

import com.enderio.core.api.client.render.VertexTransform;
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vector4f;
import com.enderio.core.common.vecmath.Vertex;

public class HalfBakedQuad {
  private final List<Vertex> corners;
  private final TextureAtlasSprite tex;
  private final Vector4f color;

  public HalfBakedQuad(BoundingBox bb, EnumFacing face, float umin, float umax, float vmin, float vmax, TextureAtlasSprite tex, Vector4f color) {
    this.corners = bb.getCornersWithUvForFace(face, umin, umax, vmin, vmax);
    this.tex = tex;
    this.color = color;
  }

  public void bake(List<BakedQuad> quads, VertexTransform... xforms) {
    for (Vertex vertex : corners) {
      for (VertexTransform xform : xforms) {
        xform.apply(vertex);
      }
    }
    RenderUtil.addBakedQuads(quads, corners, tex, color);
  }

  public void render(WorldRenderer tes) {
    for (Vertex v : corners) {
      tes.pos(v.x(), v.y(), v.z()).tex(tex.getInterpolatedU(v.u() * 16), tex.getInterpolatedV(v.v() * 16)).color(color.x, color.y, color.z, color.w)
          .endVertex();
    }
  }
}