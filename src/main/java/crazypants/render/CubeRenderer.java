package crazypants.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.common.vecmath.Vector3d;

public final class CubeRenderer {

  public static final Vector3d[] verts = new Vector3d[8];
  static {
    for (int i = 0; i < verts.length; i++) {
      verts[i] = new Vector3d();
    }
  }

  public static void render(Block block, int meta) {
    render(block, meta, null);
  }
  
  public static void render(Block block, int meta, VertexTransform xForm) {
    IIcon[] icons = new IIcon[6];
    for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      icons[dir.ordinal()] = block.getIcon(dir.ordinal(), meta);
    }
    render(BoundingBox.UNIT_CUBE.translate(0, -0.1f, 0), icons, xForm, true);
  }
  
  public static void render(BoundingBox bb, IIcon tex) {
    render(bb, tex, null, false);
  }

  public static void render(BoundingBox bb, IIcon tex, boolean tintSides) {
    render(bb, tex, null, tintSides);
  }

  public static void render(BoundingBox bb, IIcon tex, VertexTransform xForm) {
    render(bb, tex.getMinU(), tex.getMaxU(), tex.getMinV(), tex.getMaxV(), xForm, false);
  }
  
  public static void render(BoundingBox bb, IIcon tex, VertexTransform xForm, float[] brightnessPerSide, boolean tintSides) {
    float minU = 0;
    float minV = 0;
    float maxU = 1;
    float maxV = 1;
    if(tex != null) {
      minU = tex.getMinU();
      minV = tex.getMinV();
      maxU = tex.getMaxU();
      maxV = tex.getMaxV();
    }
    render(bb, minU, maxU, minV, maxV, xForm, brightnessPerSide, tintSides);
  }

  public static void render(BoundingBox bb, IIcon tex, VertexTransform xForm, boolean tintSides) {
    float minU = 0;
    float minV = 0;
    float maxU = 1;
    float maxV = 1;
    if(tex != null) {
      minU = tex.getMinU();
      minV = tex.getMinV();
      maxU = tex.getMaxU();
      maxV = tex.getMaxV();
    }
    render(bb, minU, maxU, minV, maxV, xForm, tintSides);
  }

  public static void render(BoundingBox bb, float minU, float maxU, float minV, float maxV, boolean tintSides) {
    render(bb, minU, maxU, minV, maxV, null, tintSides);
  }

  public static void render(BoundingBox bb, float minU, float maxU, float minV, float maxV) {
    render(bb, minU, maxU, minV, maxV, null, false);
  }

  public static void render(BoundingBox bb, float minU, float maxU, float minV, float maxV, VertexTransform xForm) {
    render(bb, minU, maxU, minV, maxV, xForm, false);
  }

  public static void render(BoundingBox bb, float minU, float maxU, float minV, float maxV, VertexTransform xForm, boolean tintSides) {
    float[] brightnessPerSide = null;
    if(tintSides) {
      brightnessPerSide = new float[6];
      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        brightnessPerSide[dir.ordinal()] = RenderUtil.getColorMultiplierForFace(dir);
      }
    }
    render(bb, minU, maxU, minV, maxV, xForm, brightnessPerSide);
  }

  public static void render(BoundingBox bb, float minU, float maxU, float minV, float maxV, VertexTransform xForm, float[] brightnessPerSide, boolean tintSides) {

    if(tintSides) {
      if(brightnessPerSide == null || brightnessPerSide.length != 6) {
        brightnessPerSide = new float[] { 1, 1, 1, 1, 1, 1 };
      }
      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        brightnessPerSide[dir.ordinal()] = brightnessPerSide[dir.ordinal()] * RenderUtil.getColorMultiplierForFace(dir);
      }

    }
    render(bb, minU, maxU, minV, maxV, xForm, brightnessPerSide);
  }

  public static void render(BoundingBox bb, float minU, float maxU, float minV, float maxV, VertexTransform xForm, float[] brightnessPerSide) {

    if(brightnessPerSide != null && brightnessPerSide.length != 6) {
      brightnessPerSide = null;
    }

    setupVertices(bb, xForm);

    float tmp = minV;
    minV = maxV;
    maxV = tmp;

    Tessellator tessellator = Tessellator.instance;

    tessellator.setNormal(0, 0, -1);
    if(brightnessPerSide != null) {
      float cm = brightnessPerSide[ForgeDirection.NORTH.ordinal()];
      tessellator.setColorOpaque_F(cm, cm, cm);
    }
    addVecWithUV(verts[1], minU, minV);
    addVecWithUV(verts[0], maxU, minV);
    addVecWithUV(verts[3], maxU, maxV);
    addVecWithUV(verts[2], minU, maxV);

    tessellator.setNormal(0, 0, 1);
    if(brightnessPerSide != null) {
      float cm = brightnessPerSide[ForgeDirection.SOUTH.ordinal()];
      tessellator.setColorOpaque_F(cm, cm, cm);
    }
    addVecWithUV(verts[4], minU, minV);
    addVecWithUV(verts[5], maxU, minV);
    addVecWithUV(verts[6], maxU, maxV);
    addVecWithUV(verts[7], minU, maxV);

    tessellator.setNormal(0, 1, 0);
    if(brightnessPerSide != null) {
      float cm = brightnessPerSide[ForgeDirection.UP.ordinal()];
      tessellator.setColorOpaque_F(cm, cm, cm);
    }
    addVecWithUV(verts[6], minU, minV);
    addVecWithUV(verts[2], minU, maxV);
    addVecWithUV(verts[3], maxU, maxV);
    addVecWithUV(verts[7], maxU, minV);

    tessellator.setNormal(0, -1, 0);
    if(brightnessPerSide != null) {
      float cm = brightnessPerSide[ForgeDirection.DOWN.ordinal()];
      tessellator.setColorOpaque_F(cm, cm, cm);
    }
    addVecWithUV(verts[0], maxU, maxV);
    addVecWithUV(verts[1], minU, maxV);
    addVecWithUV(verts[5], minU, minV);
    addVecWithUV(verts[4], maxU, minV);

    tessellator.setNormal(1, 0, 0);
    if(brightnessPerSide != null) {
      float cm = brightnessPerSide[ForgeDirection.EAST.ordinal()];
      tessellator.setColorOpaque_F(cm, cm, cm);
    }
    addVecWithUV(verts[2], minU, maxV);
    addVecWithUV(verts[6], maxU, maxV);
    addVecWithUV(verts[5], maxU, minV);
    addVecWithUV(verts[1], minU, minV);

    tessellator.setNormal(-1, 0, 0);
    if(brightnessPerSide != null) {
      float cm = brightnessPerSide[ForgeDirection.WEST.ordinal()];
      tessellator.setColorOpaque_F(cm, cm, cm);
    }
    addVecWithUV(verts[0], minU, minV);
    addVecWithUV(verts[4], maxU, minV);
    addVecWithUV(verts[7], maxU, maxV);
    addVecWithUV(verts[3], minU, maxV);
  }

  public static void render(BoundingBox bb, IIcon[] icons, boolean tintSides) {
    float[] brightnessPerSide = null;
    if(tintSides) {
      brightnessPerSide = new float[6];
      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        brightnessPerSide[dir.ordinal()] = RenderUtil.getColorMultiplierForFace(dir);
      }
    } 
    render(bb, icons, null, brightnessPerSide);
    
  }
  
  public static void render(BoundingBox bb, IIcon[] icons, VertexTransform xForm, boolean tintSides) {
    float[] brightnessPerSide = null;
    if(tintSides) {
      brightnessPerSide = new float[6];
      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        brightnessPerSide[dir.ordinal()] = RenderUtil.getColorMultiplierForFace(dir);
      }
    }
    render(bb, icons, xForm, brightnessPerSide);
  }
  
  public static void render(BoundingBox bb, IIcon[] faceTextures, VertexTransform xForm, float[] brightnessPerSide) {
    setupVertices(bb, xForm);
    float minU;
    float maxU;
    float minV;
    float maxV;
    IIcon tex;

    Tessellator tessellator = Tessellator.instance;    

    tessellator.setNormal(0, 0, -1);
    if(brightnessPerSide != null) {
      float cm = brightnessPerSide[ForgeDirection.NORTH.ordinal()];
      tessellator.setColorOpaque_F(cm, cm, cm);
    }

    tex = faceTextures[ForgeDirection.NORTH.ordinal()];
    minU = tex.getMinU();
    maxU = tex.getMaxU();
    minV = tex.getMinV();
    maxV = tex.getMaxV();
    addVecWithUV(verts[1], minU, maxV);
    addVecWithUV(verts[0], maxU, maxV);
    addVecWithUV(verts[3], maxU, minV);
    addVecWithUV(verts[2], minU, minV);

    tessellator.setNormal(0, 0, 1);
    if(brightnessPerSide != null) {
      float cm = brightnessPerSide[ForgeDirection.SOUTH.ordinal()];
      tessellator.setColorOpaque_F(cm, cm, cm);
    }
    tex = faceTextures[ForgeDirection.SOUTH.ordinal()];
    minU = tex.getMinU();
    maxU = tex.getMaxU();
    minV = tex.getMinV();
    maxV = tex.getMaxV();
    addVecWithUV(verts[4], minU, maxV);
    addVecWithUV(verts[5], maxU, maxV);
    addVecWithUV(verts[6], maxU, minV);
    addVecWithUV(verts[7], minU, minV);

    tessellator.setNormal(0, 1, 0);
    if(brightnessPerSide != null) {
      float cm = brightnessPerSide[ForgeDirection.UP.ordinal()];
      tessellator.setColorOpaque_F(cm, cm, cm);
    }
    tex = faceTextures[ForgeDirection.UP.ordinal()];
    minU = tex.getMinU();
    maxU = tex.getMaxU();
    minV = tex.getMinV();
    maxV = tex.getMaxV();
    addVecWithUV(verts[6], minU, minV);
    addVecWithUV(verts[2], minU, maxV);
    addVecWithUV(verts[3], maxU, maxV);
    addVecWithUV(verts[7], maxU, minV);

    tessellator.setNormal(0, -1, 0);
    if(brightnessPerSide != null) {
      float cm = brightnessPerSide[ForgeDirection.DOWN.ordinal()];
      tessellator.setColorOpaque_F(cm, cm, cm);
    }
    tex = faceTextures[ForgeDirection.DOWN.ordinal()];
    minU = tex.getMinU();
    maxU = tex.getMaxU();
    minV = tex.getMinV();
    maxV = tex.getMaxV();
    addVecWithUV(verts[0], maxU, maxV);
    addVecWithUV(verts[1], minU, maxV);
    addVecWithUV(verts[5], minU, minV);
    addVecWithUV(verts[4], maxU, minV);

    tessellator.setNormal(1, 0, 0);
    if(brightnessPerSide != null) {
      float cm = brightnessPerSide[ForgeDirection.EAST.ordinal()];
      tessellator.setColorOpaque_F(cm, cm, cm);
    }
    tex = faceTextures[ForgeDirection.EAST.ordinal()];
    minU = tex.getMinU();
    maxU = tex.getMaxU();
    minV = tex.getMinV();
    maxV = tex.getMaxV();
    addVecWithUV(verts[2], minU, minV);
    addVecWithUV(verts[6], maxU, minV);
    addVecWithUV(verts[5], maxU, maxV);
    addVecWithUV(verts[1], minU, maxV);

    tessellator.setNormal(-1, 0, 0);
    if(brightnessPerSide != null) {
      float cm = brightnessPerSide[ForgeDirection.WEST.ordinal()];
      tessellator.setColorOpaque_F(cm, cm, cm);
    }
    tex = faceTextures[ForgeDirection.WEST.ordinal()];
    minU = tex.getMinU();
    maxU = tex.getMaxU();
    minV = tex.getMinV();
    maxV = tex.getMaxV();
    addVecWithUV(verts[0], minU, maxV);
    addVecWithUV(verts[4], maxU, maxV);
    addVecWithUV(verts[7], maxU, minV);
    addVecWithUV(verts[3], minU, minV);
  }

  public static void setupVertices(BoundingBox bound) {
    setupVertices(bound, null);
  }

  public static void setupVertices(BoundingBox bound, VertexTransform xForm) {
    verts[0].set(bound.minX, bound.minY, bound.minZ);
    verts[1].set(bound.maxX, bound.minY, bound.minZ);
    verts[2].set(bound.maxX, bound.maxY, bound.minZ);
    verts[3].set(bound.minX, bound.maxY, bound.minZ);
    verts[4].set(bound.minX, bound.minY, bound.maxZ);
    verts[5].set(bound.maxX, bound.minY, bound.maxZ);
    verts[6].set(bound.maxX, bound.maxY, bound.maxZ);
    verts[7].set(bound.minX, bound.maxY, bound.maxZ);

    if(xForm != null) {
      for (Vector3d vec : verts) {
        xForm.apply(vec);
      }
    }
  }

  public static void addVecWithUV(Vector3d vec, double u, double v) {
    Tessellator.instance.addVertexWithUV(vec.x, vec.y, vec.z, u, v);
  }

  private CubeRenderer() {
  }

  

  

}
