package crazypants.enderio.render.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.enderio.core.api.client.render.VertexTransform;
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.IconUtil;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vector4f;
import com.enderio.core.common.vecmath.Vertex;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.CullFace;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad.Builder;

public class HalfBakedQuad {
  private final List<Vertex> corners;
  private final TextureAtlasSprite tex;
  private final Vector4f color;
  private final EnumFacing face;

  private final static Vector4f NO_COLOR = new Vector4f(1, 1, 1, 1);

  public HalfBakedQuad(BoundingBox bb, EnumFacing face, float umin, float umax, float vmin, float vmax, TextureAtlasSprite tex, Vector4f color) {
    this.corners = bb.getCornersWithUvForFace(face, umin, umax, vmin, vmax);
    this.tex = tex != null ? tex : IconUtil.instance.errorTexture;
    this.color = color != null ? color : NO_COLOR;
    this.face = face;
  }

  private static boolean hasMojangFixedUVXWTextureCoords = false;

  public void bake(List<BakedQuad> quads) {
    float w01 = 1;
    float w23 = 1;

    if (hasMojangFixedUVXWTextureCoords && face != EnumFacing.DOWN && face != EnumFacing.UP) {
      // assuming the first vertex is upper-left or lower-right
      w01 = (float) corners.get(0).xyz.distance(corners.get(1).xyz);
      w23 = (float) corners.get(2).xyz.distance(corners.get(3).xyz);
    }

    UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(DefaultVertexFormats.ITEM); // this one has normals
    builder.setQuadOrientation(face);
    builder.setTexture(tex);
    putVertexData(builder, corners.get(0), w01);
    putVertexData(builder, corners.get(1), w01);
    putVertexData(builder, corners.get(2), w23);
    putVertexData(builder, corners.get(3), w23);
    quads.add(builder.build());
  }

  private void putVertexData(Builder builder, Vertex v, float w) {
    VertexFormat format = builder.getVertexFormat();
    for (int e = 0; e < format.getElementCount(); e++) {
      switch (format.getElement(e).getUsage()) {
      case POSITION:
        builder.put(e, (float) v.x(), (float) v.y(), (float) v.z(), 1);
        break;
      case COLOR:
        float d = LightUtil.diffuseLight(v.nx(), v.ny(), v.nz());
        builder.put(e, d * color.x, d * color.y, d * color.z, color.w);
        break;
      case UV:
        builder.put(e, tex.getInterpolatedU(v.u() * 16) * w, tex.getInterpolatedV(v.v() * 16) * w, 0, w);
        break;
      case NORMAL:
        builder.put(e, v.nx(), v.ny(), v.nz(), 0);
        break;
      default:
        builder.put(e);
      }
    }
  }

  public void transform(VertexTransform... xforms) {
    for (Vertex vertex : corners) {
      for (VertexTransform xform : xforms) {
        xform.apply(vertex);
      }
    }
  }

  public void render(VertexBuffer tes) {
    for (Vertex v : corners) {
      tes.pos(v.x(), v.y(), v.z()).tex(tex.getInterpolatedU(v.u() * 16), tex.getInterpolatedV(v.v() * 16)).color(color.x, color.y, color.z, color.w)
          .normal(v.nx(), v.ny(), v.nz()).endVertex();
    }
  }

  public static class HalfBakedList extends AbstractList<HalfBakedQuad> {

    private final List<HalfBakedQuad> store = new ArrayList<HalfBakedQuad>();

    @Override
    public HalfBakedQuad get(int index) {
      return store.get(index);
    }

    @Override
    public int size() {
      return store.size();
    }

    public void add(BoundingBox bb, EnumFacing face, float umin, float umax, float vmin, float vmax, TextureAtlasSprite tex, Vector4f color) {
      store.add(new HalfBakedQuad(bb, face, umin, umax, vmin, vmax, tex, color));
    }

    public void add(BoundingBox bb, EnumFacing face, double umin, double umax, double vmin, double vmax, TextureAtlasSprite tex, Vector4f color) {
      store.add(new HalfBakedQuad(bb, face, (float) umin, (float) umax, (float) vmin, (float) vmax, tex, color));
    }

    public void transform(VertexTransform... xforms) {
      for (HalfBakedQuad halfBakedQuad : store) {
        halfBakedQuad.transform(xforms);
      }
    }

    public void bake(List<BakedQuad> quads, VertexTransform... xforms) {
      for (HalfBakedQuad halfBakedQuad : store) {
        halfBakedQuad.transform(xforms);
        halfBakedQuad.bake(quads);
      }
    }

    public void bake(List<BakedQuad> quads) {
      for (HalfBakedQuad halfBakedQuad : store) {
        halfBakedQuad.bake(quads);
      }
    }

    public void render(VertexBuffer tes) {
      for (HalfBakedQuad halfBakedQuad : store) {
        halfBakedQuad.render(tes);
      }
    }

    /*
     * Use for liquids in TE render pass 1
     */
    public void render() {
      RenderUtil.bindBlockTexture();
      RenderHelper.disableStandardItemLighting();
      VertexBuffer tes = Tessellator.getInstance().getBuffer();
      for (int i = 0; i <= 1; i++) {
        if (i == 0) {
          GlStateManager.cullFace(CullFace.FRONT);
        } else {
          GlStateManager.cullFace(CullFace.BACK);
        }
        tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
        render(tes);
        Tessellator.getInstance().draw();
      }
      RenderHelper.enableStandardItemLighting();
    }

  }

}