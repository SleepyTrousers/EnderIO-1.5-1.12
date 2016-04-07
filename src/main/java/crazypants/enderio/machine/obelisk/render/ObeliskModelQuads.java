package crazypants.enderio.machine.obelisk.render;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

import com.enderio.core.api.client.render.VertexTransform;
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.IconUtil;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vector3d;
import com.enderio.core.common.vecmath.Vector3f;
import com.enderio.core.common.vecmath.Vertex;

import crazypants.enderio.render.TextureRegistry.TextureSupplier;

public class ObeliskModelQuads {
  
  
  private static final float WIDE_PINCH = 0.9f;
  private static final float WIDTH = 18f / 32f * WIDE_PINCH;
  private static final float HEIGHT = 0.475f;
  
  private static final int BOTTOM = EnumFacing.DOWN.ordinal();
  private static final int TOP = EnumFacing.UP.ordinal();

  private static final BoundingBox bb1 = BoundingBox.UNIT_CUBE.scale(WIDTH, HEIGHT, 1).translate(0, -0.5f + HEIGHT / 2, 0);
  private static final BoundingBox bb2 = BoundingBox.UNIT_CUBE.scale(1, HEIGHT, WIDTH).translate(0, -0.5f + HEIGHT / 2, 0);

  public static final ObeliskModelQuads INSTANCE_ACTIVE = new ObeliskModelQuads(true);
  public static final ObeliskModelQuads INSTANCE = new ObeliskModelQuads(false);
    
  private List<BakedQuad> quads = new ArrayList<BakedQuad>();
  private static final VertXForm2x xform2x = new VertXForm2x();
  private static final VertXForm2z xform2z = new VertXForm2z();
  private static final VertXForm3 xform3 = new VertXForm3();

  private boolean isActive;
  
  public ObeliskModelQuads(boolean isActive) {
    this.isActive = isActive;
  }
  
  public List<BakedQuad> getQuads() {
    if(quads.isEmpty()) {
      quads = createQuads(isActive);
    }
    return quads;
  }
  
  public void invalidate() {
    quads = new ArrayList<BakedQuad>();
  }

  private static List<BakedQuad> createQuads(boolean isActive) {
    List<BakedQuad> quads = new ArrayList<BakedQuad>();

    TextureSupplier[] texs;
    if(isActive) {
      texs = ObeliskRenderManager.INSTANCE.getActiveTextures();
    } else {
      texs = ObeliskRenderManager.INSTANCE.getTextures();
    }
    
    //Icons for block
    TextureAtlasSprite[] icons = new TextureAtlasSprite[6];
    for(int i=0;i<icons.length;i++) {
      icons[i] = texs[i].get(TextureAtlasSprite.class);
    }
    // bottom texture goes into its own BB
    TextureAtlasSprite[] bottomIcons = new TextureAtlasSprite[6];
    for (int i = 1; i < bottomIcons.length; i++) {
      bottomIcons[i] = IconUtil.instance.blankTexture;
    }
    bottomIcons[BOTTOM] = icons[BOTTOM];
    icons[BOTTOM] = IconUtil.instance.blankTexture;

    int i=0;
    for(EnumFacing face : EnumFacing.values()) {
      RenderUtil.addBakedQuadForFace(quads, bb1, icons[i], face, xform2z);
      ++i;
    }    

    icons[TOP] = IconUtil.instance.blankTexture;
    i=0;
    for(EnumFacing face : EnumFacing.values()) {
      RenderUtil.addBakedQuadForFace(quads, bb2, icons[i], face, xform2x);
      ++i;
    }    

    i=0;
    for(EnumFacing face : EnumFacing.values()) {
      RenderUtil.addBakedQuadForFace(quads, BoundingBox.UNIT_CUBE, bottomIcons[i], face, xform3);
      ++i;
    }

    return quads;
  }


  private static class VertXForm2x implements VertexTransform {

    public VertXForm2x() {
    }

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

    public VertXForm2z() {
    }

    @Override
    public void apply(Vertex vertex) {
      apply(vertex.xyz);
    }

    @Override
    public void apply(Vector3d vec) {
      double pinch = WIDE_PINCH;
      if(vec.y > 0.2) {
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

    public VertXForm3() {
    }

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
