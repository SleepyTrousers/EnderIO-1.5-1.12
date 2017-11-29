package crazypants.enderio.conduit.geom;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import com.enderio.core.api.client.render.VertexTransform;
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.VertexRotation;
import com.enderio.core.client.render.VertexTransformComposite;
import com.enderio.core.client.render.VertexTranslation;
import com.enderio.core.common.vecmath.Vector3d;
import com.enderio.core.common.vecmath.Vector4f;
import com.enderio.core.common.vecmath.Vertex;

import static com.enderio.core.common.util.ForgeDirectionOffsets.offsetScaled;

import crazypants.enderio.base.conduit.geom.ConduitGeometryUtil;
import crazypants.enderio.base.conduit.geom.Offset;
import crazypants.enderio.conduit.render.BakedQuadBuilder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

public class ConnectionModeGeometry {

  private static final EnumMap<EnumFacing, List<Vertex>> VERTS = new EnumMap<EnumFacing, List<Vertex>>(EnumFacing.class);

  static {

    float scale = 0.9f;
    BoundingBox refBB = ConduitGeometryUtil.CORE_BOUNDS;
    refBB = refBB.scale(scale, scale, scale);
    refBB = refBB.scale(scale, 1, 1);

    double offset = (ConduitGeometryUtil.HWIDTH * scale * scale) + ConduitGeometryUtil.CONNECTOR_DEPTH;

    EnumFacing dir;
    Vector3d trans;

    VertexRotation vrot = new VertexRotation(Math.PI / 2, new Vector3d(0, 1, 0), new Vector3d(0.5, 0.5, 0.5));
    VertexTranslation vtrans = new VertexTranslation(0, 0, 0);
    VertexTransformComposite xform = new VertexTransformComposite(vrot, vtrans);

    dir = EnumFacing.SOUTH;
    trans = offsetScaled(dir, 0.5);
    trans.sub(offsetScaled(dir, offset));
    vtrans.set(trans);
    VERTS.put(dir, createVerticesForDir(refBB, xform));

    dir = EnumFacing.NORTH;
    vrot.setAngle(Math.PI + Math.PI / 2);
    trans = offsetScaled(dir, 0.5);
    trans.sub(offsetScaled(dir, offset));
    vtrans.set(trans);
    VERTS.put(dir, createVerticesForDir(refBB, xform));

    dir = EnumFacing.EAST;
    vrot.setAngle(Math.PI);
    trans = offsetScaled(dir, 0.5);
    trans.sub(offsetScaled(dir, offset));
    vtrans.set(trans);
    VERTS.put(dir, createVerticesForDir(refBB, xform));

    dir = EnumFacing.WEST;
    vrot.setAngle(0);
    trans = offsetScaled(dir, 0.5);
    trans.sub(offsetScaled(dir, offset));
    vtrans.set(trans);
    VERTS.put(dir, createVerticesForDir(refBB, xform));

    vrot.setAxis(new Vector3d(0, 0, 1));

    dir = EnumFacing.UP;
    vrot.setAngle(-Math.PI / 2);
    trans = offsetScaled(dir, 0.5);
    trans.sub(offsetScaled(dir, offset));
    vtrans.set(trans);
    VERTS.put(dir, createVerticesForDir(refBB, xform));

    dir = EnumFacing.DOWN;
    vrot.setAngle(Math.PI / 2);
    trans = offsetScaled(dir, 0.5);
    trans.sub(offsetScaled(dir, offset));
    vtrans.set(trans);
    VERTS.put(dir, createVerticesForDir(refBB, xform));

  }

  private static List<Vertex> createVerticesForDir(BoundingBox refBB, VertexTransform xform) {
    List<Vertex> result = new ArrayList<Vertex>(24);
    for (EnumFacing face : EnumFacing.VALUES) {
      result.addAll(refBB.getCornersWithUvForFace(face));
    }
    for (Vertex v : result) {
      xform.apply(v.xyz);
      xform.applyToNormal(v.normal);

    }
    return result;
  }


  public static void addModeConnectorQuads(EnumFacing dir, Offset offset, TextureAtlasSprite tex, Vector4f color, List<BakedQuad> quads) {
    List<Vertex> verts = VERTS.get(dir);
    if (verts == null) {
      return;
    }
    Vector3d trans = ConduitGeometryUtil.instance.getTranslation(dir, offset);
    List<Vertex> xFormed = new ArrayList<Vertex>(verts.size());
    for (Vertex v : verts) {
      Vertex xf = new Vertex(v);
      xf.xyz.add(trans);
      xFormed.add(xf);
    }
    BakedQuadBuilder.addBakedQuads(quads, xFormed, tex, color);
  }

}
