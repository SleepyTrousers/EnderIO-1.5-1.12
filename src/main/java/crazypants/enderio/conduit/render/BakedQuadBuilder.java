package crazypants.enderio.conduit.render;

import java.util.List;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vector4f;
import com.enderio.core.common.vecmath.Vertex;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

//I am using this class instead of RenderUtil directly in case I decide to cache the BakedQuads
public class BakedQuadBuilder {

  

  public static void addBakedQuads(List<BakedQuad> quads, BoundingBox bound, TextureAtlasSprite tex) {
    RenderUtil.addBakedQuads(quads, bound, tex);    
  }
  
  public static void addBakedQuads(List<BakedQuad> quads, BoundingBox bound, TextureAtlasSprite tex, Vector4f color) {
    RenderUtil.addBakedQuads(quads, bound, tex, color);    
  }
  
  public static void addBakedQuads(List<BakedQuad> quads, List<Vertex> vertices, TextureAtlasSprite tex, Vector4f color) {
    RenderUtil.addBakedQuads(quads, vertices, tex, color);   
  }
  
  public static void addBakedQuadForFace(List<BakedQuad> quads, BoundingBox bb, TextureAtlasSprite tex, EnumFacing dir) {
    RenderUtil.addBakedQuadForFace(quads, bb, tex, dir);    
  }
  
  public static void addBakedQuadForFace(List<BakedQuad> quads, BoundingBox bb, TextureAtlasSprite tex, EnumFacing face, boolean rotateUV, boolean flipU,
      Vector4f color) {
    RenderUtil.addBakedQuadForFace(quads, bb, tex, face, rotateUV, flipU, color);
  }

  

}
