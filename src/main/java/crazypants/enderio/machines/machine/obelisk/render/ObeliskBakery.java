package crazypants.enderio.machines.machine.obelisk.render;

import java.util.ArrayList;
import java.util.List;

import com.enderio.core.api.client.render.VertexTransform;
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.vecmath.Vector3d;
import com.enderio.core.common.vecmath.Vector3f;
import com.enderio.core.common.vecmath.Vertex;

import crazypants.enderio.render.registry.TextureRegistry.TextureSupplier;
import crazypants.enderio.render.util.HalfBakedQuad.HalfBakedList;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

public class ObeliskBakery {

  private static final float WIDE_PINCH = 0.9f;
  private static final float WIDTH = 18f / 32f * WIDE_PINCH;
  private static final float HEIGHT = 0.475f;

  private static final BoundingBox bb1 = BoundingBox.UNIT_CUBE.scale(WIDTH, HEIGHT, 1).translate(0, -0.5f + HEIGHT / 2, 0);
  private static final BoundingBox bb2 = BoundingBox.UNIT_CUBE.scale(1, HEIGHT, WIDTH).translate(0, -0.5f + HEIGHT / 2, 0);

  private static final VertXForm2x xform2x = new VertXForm2x();
  private static final VertXForm2z xform2z = new VertXForm2z();
  private static final VertXForm3 xform3 = new VertXForm3();

  private ObeliskBakery() {
  }

  public static List<BakedQuad> bake(TextureSupplier[] texs) {
    // Icons for block
    TextureAtlasSprite[] icons = new TextureAtlasSprite[6];
    for (int i = 0; i < icons.length; i++) {
      icons[i] = texs[i].get(TextureAtlasSprite.class);
    }

    HalfBakedList buffer1 = new HalfBakedList();
    HalfBakedList buffer2 = new HalfBakedList();
    HalfBakedList buffer3 = new HalfBakedList();

    buffer1.add(bb1, EnumFacing.UP, 0f, 1f, 0f, 1f, icons[EnumFacing.UP.ordinal()], null);
    for (EnumFacing face : EnumFacing.Plane.HORIZONTAL) {
      buffer1.add(bb1, face, 0f, 1f, 0f, 1f, icons[face.ordinal()], null);
      buffer2.add(bb2, face, 0f, 1f, 0f, 1f, icons[face.ordinal()], null);
    }
    buffer3.add(BoundingBox.UNIT_CUBE, EnumFacing.DOWN, 0f, 1f, 0f, 1f, icons[EnumFacing.DOWN.ordinal()], null);

    List<BakedQuad> quads = new ArrayList<BakedQuad>();

    buffer1.bake(quads, xform2z);
    buffer2.bake(quads, xform2x);
    buffer3.bake(quads, xform3);

    return quads;
  }

  private static class VertXForm2x implements VertexTransform {

    @Override
    public void apply(Vertex vertex) {
      apply(vertex.xyz);
    }

    @Override
    public void apply(Vector3d vec) {
      double pinch = WIDE_PINCH;
      if (vec.y > 0.2) {
        pinch = 0.5;
      }
      vec.x -= 0.5;
      vec.x *= pinch;
      vec.x += 0.5;
    }

    @Override
    public void applyToNormal(Vector3f vec) {
    }

  }

  private static class VertXForm2z implements VertexTransform {

    @Override
    public void apply(Vertex vertex) {
      apply(vertex.xyz);
    }

    @Override
    public void apply(Vector3d vec) {
      double pinch = WIDE_PINCH;
      if (vec.y > 0.2) {
        pinch = 0.5;
      }
      vec.z -= 0.5;
      vec.z *= pinch;
      vec.z += 0.5;
    }

    @Override
    public void applyToNormal(Vector3f vec) {
    }

  }

  private static class VertXForm3 implements VertexTransform {

    @Override
    public void apply(Vertex vertex) {
      apply(vertex.xyz);
    }

    @Override
    public void apply(Vector3d vec) {
      vec.x -= 0.5;
      vec.x *= WIDE_PINCH;
      vec.x += 0.5;
      vec.z -= 0.5;
      vec.z *= WIDE_PINCH;
      vec.z += 0.5;
    }

    @Override
    public void applyToNormal(Vector3f vec) {
    }

  }

}
