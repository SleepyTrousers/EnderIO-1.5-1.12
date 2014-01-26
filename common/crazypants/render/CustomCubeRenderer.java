package crazypants.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;
import crazypants.util.BlockCoord;
import crazypants.util.ForgeDirectionOffsets;
import crazypants.vecmath.Vector2f;
import crazypants.vecmath.Vector3d;
import crazypants.vecmath.Vector3f;
import crazypants.vecmath.Vector4f;
import crazypants.vecmath.Vertex;

public class CustomCubeRenderer {

  private static final Tessellator DEFAULT_TES = Tessellator.instance;

  private static final MockTesselator BUF_TES = new MockTesselator();

  public static final CustomCubeRenderer instance = new CustomCubeRenderer();

  private InnerRenderBlocks rb = null;

  private Icon overrideTexture;

  public void renderBlock(IBlockAccess ba, Block par1Block, int par2, int par3, int par4, Icon border, boolean forceAllEdges) {
    renderBlock(ba, par1Block, par2, par3, par4, new DefaultTextureCallback(border), forceAllEdges);
  }

  public void renderBlock(IBlockAccess ba, Block par1Block, int par2, int par3, int par4, TextureCallback borderCallback, boolean forceAllEdges) {
    if(rb == null) {
      rb = new InnerRenderBlocks(ba);
    }
    rb.forceAllEdges = forceAllEdges;
    rb.blockAccess = ba;
    rb.setRenderBoundsFromBlock(par1Block);
    rb.setBorderTextureCallback(borderCallback);
    try {
      rb.setTesselatorEnabled(false);
      rb.renderStandardBlock(par1Block, par2, par3, par4);
    } finally {
      rb.setBorderTextureCallback(null);
      rb.setTesselatorEnabled(true);
    }
  }

  public Icon getOverrideTexture() {
    return overrideTexture;
  }

  public void setOverrideTexture(Icon overrideTexture) {
    this.overrideTexture = overrideTexture;
  }

  public static interface TextureCallback {

    public Icon getTextureForFace(ForgeDirection dir);
  }

  public static class DefaultTextureCallback implements TextureCallback {

    private final Icon tex;

    private final Block block;
    private final int meta;

    public DefaultTextureCallback(Icon borderTex) {
      this.tex = borderTex;
      block = null;
      meta = 0;
    }

    public DefaultTextureCallback(Block block, int meta) {
      tex = null;
      this.block = block;
      this.meta = meta;
    }

    @Override
    public Icon getTextureForFace(ForgeDirection dir) {
      if(tex != null) {
        return tex;
      }
      if(block != null) {
        return block.getIcon(dir.ordinal(), meta);
      }
      return null;
    }

  }

  private class InnerRenderBlocks extends RenderBlocks {

    boolean forceAllEdges = false;
    boolean translateToXYZ = true;
    private TextureCallback texCallback;

    private InnerRenderBlocks(IBlockAccess par1iBlockAccess) {
      super(par1iBlockAccess);
    }

    public void setBorderTextureCallback(TextureCallback borderCallback) {
      this.texCallback = borderCallback;
    }

    void setTesselatorEnabled(boolean enabled) {
      if(enabled) {
        Tessellator.instance = DEFAULT_TES;
      } else {
        Tessellator.instance = BUF_TES;
      }
    }

    private void resetTesForFace() {
      BUF_TES.reset();
    }

    private void renderFace(ForgeDirection face, Block par1Block, double x, double y, double z, Icon texture) {

      renderFaceToBuffer(face, par1Block, x, y, z, overrideTexture == null ? texture : overrideTexture);
      List<Vertex> refVertices = BUF_TES.getVertices();

      List<Vertex> finalVerts = new ArrayList<Vertex>();
      finalVerts.addAll(refVertices);

      Icon borderTex = texCallback == null ? null : texCallback.getTextureForFace(face);
      if(borderTex != null) {

        texture = borderTex;
        List<ForgeDirection> edges;
        if(forceAllEdges) {
          edges = RenderUtil.getEdgesForFace(face);
        } else {
          edges = RenderUtil.getNonConectedEdgesForFace(blockAccess, (int) x, (int) y, (int) z, face);
        }

        List<ForgeDirection> allEdges = RenderUtil.getEdgesForFace(face);

        float scaleFactor = 15f / 16f;
        Vector2f uv = new Vector2f();

        //for each that needs a border, add a geom for the border and move in the 'centre' of the face
        //so there is no overlap
        for (ForgeDirection edge : edges) {

          //We need to move the 'centre' of the face so it doesn't overlap with the border
          moveCorners(refVertices, edge, 1 - scaleFactor, face);

          //Now create the geometry for this edge of the border
          float xLen = 1 - Math.abs(edge.offsetX) * scaleFactor;
          float yLen = 1 - Math.abs(edge.offsetY) * scaleFactor;
          float zLen = 1 - Math.abs(edge.offsetZ) * scaleFactor;

          BoundingBox bb = BoundingBox.UNIT_CUBE.scale(xLen, yLen, zLen);

          List<Vector3d> corners = bb.getCornersForFaceD(face);
          for (Vector3d corner : corners) {
            if(translateToXYZ) {
              corner.x += x;
              corner.y += y;
              corner.z += z;
            }
            corner.x += (float) (edge.offsetX * 0.5) - Math.signum(edge.offsetX) * xLen / 2f;
            corner.y += (float) (edge.offsetY * 0.5) - Math.signum(edge.offsetY) * yLen / 2f;
            corner.z += (float) (edge.offsetZ * 0.5) - Math.signum(edge.offsetZ) * zLen / 2f;
          }

          int[] indices = new int[] { 0, 1, 2, 3 };
          for (int index : indices) {
            Vector3d corner = corners.get(index);
            if(translateToXYZ) {
              RenderUtil.getUvForCorner(uv, corner, (int) x, (int) y, (int) z, face, texture);
            } else {
              RenderUtil.getUvForCorner(uv, corner, 0, 0, 0, face, texture);
            }
            Vertex vertex = new Vertex();
            vertex.xyz.set(corner);
            vertex.uv = new Vector2f(uv);
            applyLighting(vertex, corner);
            finalVerts.add(vertex);
          }
        }

        //move in corners
        //        Vector3d sideVec = new Vector3d();
        //        sideVec.cross(ForgeDirectionOffsets.forDir(face), ForgeDirectionOffsets.forDir(edge));
        //        moveCornerVec(corners, sideVec, scaleFactor, face);
        //        sideVec.negate();
        //        moveCornerVec(corners, sideVec, scaleFactor, face);

        for (int i = 0; i < allEdges.size(); i++) {
          ForgeDirection dir = allEdges.get(i);
          ForgeDirection dir2 = i + 1 < allEdges.size() ? allEdges.get(i + 1) : allEdges.get(0);
          if(needsCorner(dir, dir2, edges, face, par1Block, x, y, z)) {

            Vertex v = new Vertex();
            v.uv = new Vector2f();
            v.xyz.set(ForgeDirectionOffsets.forDir(dir));
            v.xyz.add(ForgeDirectionOffsets.forDir(dir2));
            //TODO: dodgy hack to just move the corner forward a bit
            v.xyz.add(ForgeDirectionOffsets.offsetScaled(face, 0.001f));
            v.xyz.x = Math.max(-0.001, v.xyz.x);
            v.xyz.y = Math.max(-0.001, v.xyz.y);
            v.xyz.z = Math.max(-0.001, v.xyz.z);

            if(ForgeDirectionOffsets.isPositiveOffset(face)) {
              v.xyz.add(ForgeDirectionOffsets.forDir(face));
            }

            if(translateToXYZ) {
              v.xyz.x += x;
              v.xyz.y += y;
              v.xyz.z += z;
              RenderUtil.getUvForCorner(v.uv, v.xyz, (int) x, (int) y, (int) z, face, texture);
            } else {
              RenderUtil.getUvForCorner(v.uv, v.xyz, 0, 0, 0, face, texture);
            }
            applyLighting(v, v.xyz);

            finalVerts.add(v);

            Vector3d corner = new Vector3d(v.xyz);
            if(ForgeDirectionOffsets.isPositiveOffset(face)) {
              addVertexForCorner(face, x, y, z, texture, translateToXYZ, finalVerts, dir2, null, corner);
              addVertexForCorner(face, x, y, z, texture, translateToXYZ, finalVerts, dir, dir2, corner);
              addVertexForCorner(face, x, y, z, texture, translateToXYZ, finalVerts, dir, null, corner);
            } else {
              addVertexForCorner(face, x, y, z, texture, translateToXYZ, finalVerts, dir, null, corner);
              addVertexForCorner(face, x, y, z, texture, translateToXYZ, finalVerts, dir, dir2, corner);
              addVertexForCorner(face, x, y, z, texture, translateToXYZ, finalVerts, dir2, null, corner);
            }
          }
        }
      }

      RenderUtil.addVerticesToTessellator(finalVerts, DEFAULT_TES);
    }

    private Vertex getClosestVertex(List<Vertex> vertices, Vector3d corner) {
      Vertex result = null;
      double d2 = Double.MAX_VALUE;
      for (Vertex v : vertices) {
        double tmp = corner.distanceSquared(v.xyz);
        if(tmp <= d2) {
          result = v;
          d2 = tmp;
        }
      }
      return result;
    }

    private void addVertexForCorner(ForgeDirection face, double x, double y, double z, Icon texture, boolean translateToXYZ, List<Vertex> vertices,
        ForgeDirection dir, ForgeDirection dir2, Vector3d corner) {
      float scale = 1 / 16f;
      Vertex vert = new Vertex();
      vert.uv = new Vector2f();
      vert.xyz.set(corner);
      vert.xyz.sub(ForgeDirectionOffsets.offsetScaled(dir, scale));
      if(dir2 != null) {
        vert.xyz.sub(ForgeDirectionOffsets.offsetScaled(dir2, scale));
      }
      if(translateToXYZ) {
        RenderUtil.getUvForCorner(vert.uv, vert.xyz, (int) x, (int) y, (int) z, face, texture);
      } else {
        RenderUtil.getUvForCorner(vert.uv, vert.xyz, 0, 0, 0, face, texture);
      }
      applyLighting(vert, corner);
      vertices.add(vert);
    }

    private void applyLighting(Vertex vert, Vector3d samplePoint) {
      Vertex closest = getClosestVertex(BUF_TES.getVertices(), samplePoint);
      vert.setBrightness(closest.brightness);
      Vector4f col = closest.getColor();
      if(col != null) {
        vert.setColor(col);
      }
    }

    //    private int interpolateBrightness(float u, float v, int bl, int br, int tl, int tr) {
    //      //(1 - yfrac) * [(1 - xfrac)*s00 + xfrac*s01] +  yfrac * [(1 - xfrac)*s10 + xfrac*s11]                  
    //      float res = (1 - v) * ((1 - u) * bl + u * tl) + v * ((1 - u) * br + u * tr);
    //      return (int) res;
    //    }

    private boolean needsCorner(ForgeDirection dir, ForgeDirection dir2, List<ForgeDirection> edges, ForgeDirection face,
        Block par1Block, double x, double y,
        double z) {

      if(edges.contains(dir) || edges.contains(dir2)) {
        return false;
      }

      BlockCoord bc = new BlockCoord((int) x, (int) y, (int) z);
      BlockCoord testLoc = bc.getLocation(dir);
      if(RenderUtil.getNonConectedEdgesForFace(blockAccess, testLoc.x, testLoc.y, testLoc.z, face).contains(dir2)) {
        return true;
      }
      testLoc = bc.getLocation(dir2);
      if(RenderUtil.getNonConectedEdgesForFace(blockAccess, testLoc.x, testLoc.y, testLoc.z, face).contains(dir)) {
        return true;
      }
      return false;
    }

    private void moveCornerVec(List<Vector3d> corners, Vector3d edge, float scaleFactor, ForgeDirection face) {
      int[] indices = getClosestVec(edge, corners, face);
      corners.get(indices[0]).x -= scaleFactor * edge.x;
      corners.get(indices[1]).x -= scaleFactor * edge.x;
      corners.get(indices[0]).y -= scaleFactor * edge.y;
      corners.get(indices[1]).y -= scaleFactor * edge.y;
      corners.get(indices[0]).z -= scaleFactor * edge.z;
      corners.get(indices[1]).z -= scaleFactor * edge.z;
    }

    private void moveCorners(List<Vertex> vertices, ForgeDirection edge, float scaleFactor, ForgeDirection face) {
      int[] indices = getClosest(edge, vertices, face);
      vertices.get(indices[0]).xyz.x -= scaleFactor * edge.offsetX;
      vertices.get(indices[1]).xyz.x -= scaleFactor * edge.offsetX;
      vertices.get(indices[0]).xyz.y -= scaleFactor * edge.offsetY;
      vertices.get(indices[1]).xyz.y -= scaleFactor * edge.offsetY;
      vertices.get(indices[0]).xyz.z -= scaleFactor * edge.offsetZ;
      vertices.get(indices[1]).xyz.z -= scaleFactor * edge.offsetZ;
    }

    private int[] getClosest(ForgeDirection edge, List<Vertex> vertices, ForgeDirection face) {
      int[] res = new int[] { -1, -1 };
      boolean highest = edge.offsetX > 0 || edge.offsetY > 0 || edge.offsetZ > 0;
      double minMax = highest ? -Double.MAX_VALUE : Double.MAX_VALUE;
      int index = 0;
      for (Vertex v : vertices) {
        double val = get(v.xyz, edge);
        if(highest ? val >= minMax : val <= minMax) {
          if(val != minMax) {
            res[0] = index;
          } else {
            res[1] = index;
          }
          minMax = val;
        }
        index++;
      }
      return res;
    }

    private int[] getClosestVec(Vector3d edge, List<Vector3d> corners, ForgeDirection face) {
      int[] res = new int[] { -1, -1 };
      boolean highest = edge.x > 0 || edge.y > 0 || edge.z > 0;
      double minMax = highest ? -Double.MAX_VALUE : Double.MAX_VALUE;
      int index = 0;
      for (Vector3d v : corners) {
        double val = get(v, edge);
        if(highest ? val >= minMax : val <= minMax) {
          if(val != minMax) {
            res[0] = index;
          } else {
            res[1] = index;
          }
          minMax = val;
        }
        index++;
      }
      return res;
    }

    private double get(Vector3d xyz, ForgeDirection edge) {
      if(edge == ForgeDirection.EAST || edge == ForgeDirection.WEST) {
        return xyz.x;
      }
      if(edge == ForgeDirection.UP || edge == ForgeDirection.DOWN) {
        return xyz.y;
      }
      return xyz.z;
    }

    private double get(Vector3d xyz, Vector3d edge) {
      if(Math.abs(edge.x) > 0.5) {
        return xyz.x;
      }
      if(Math.abs(edge.y) > 0.5) {
        return xyz.y;
      }
      return xyz.z;
    }

    private void renderFaceToBuffer(ForgeDirection face, Block par1Block, double par2, double par4, double par6, Icon par8Icon) {
      setTesselatorEnabled(false);
      BUF_TES.reset();
      switch (face) {
      case DOWN:
        super.renderFaceYNeg(par1Block, par2, par4, par6, par8Icon);
        break;
      case EAST:
        super.renderFaceXPos(par1Block, par2, par4, par6, par8Icon);
        break;
      case NORTH:
        super.renderFaceZNeg(par1Block, par2, par4, par6, par8Icon);
        break;
      case SOUTH:
        super.renderFaceZPos(par1Block, par2, par4, par6, par8Icon);
        break;
      case UP:
        super.renderFaceYPos(par1Block, par2, par4, par6, par8Icon);
        break;
      case WEST:
        super.renderFaceXNeg(par1Block, par2, par4, par6, par8Icon);
        break;
      case UNKNOWN:
      default:
        break;
      }
    }

    @Override
    public void renderFaceYNeg(Block par1Block, double par2, double par4, double par6, Icon par8Icon) {
      renderFace(ForgeDirection.DOWN, par1Block, par2, par4, par6, par8Icon);
    }

    @Override
    public void renderFaceYPos(Block par1Block, double par2, double par4, double par6, Icon par8Icon) {
      renderFace(ForgeDirection.UP, par1Block, par2, par4, par6, par8Icon);
    }

    @Override
    public void renderFaceZNeg(Block par1Block, double par2, double par4, double par6, Icon par8Icon) {
      renderFace(ForgeDirection.NORTH, par1Block, par2, par4, par6, par8Icon);
    }

    @Override
    public void renderFaceZPos(Block par1Block, double par2, double par4, double par6, Icon par8Icon) {
      renderFace(ForgeDirection.SOUTH, par1Block, par2, par4, par6, par8Icon);
    }

    @Override
    public void renderFaceXNeg(Block par1Block, double par2, double par4, double par6, Icon par8Icon) {
      renderFace(ForgeDirection.WEST, par1Block, par2, par4, par6, par8Icon);
    }

    @Override
    public void renderFaceXPos(Block par1Block, double par2, double par4, double par6, Icon par8Icon) {
      renderFace(ForgeDirection.EAST, par1Block, par2, par4, par6, par8Icon);
    }

  }

  private static class MockTesselator extends Tessellator {

    private final List<Vertex> vertices = new ArrayList<Vertex>();

    private boolean hasTexture;
    private double textureU;
    private double textureV;
    private boolean hasBrightness;
    private int brightness;
    private boolean hasColor;
    private Vector4f color;
    private boolean hasNormals;
    private Vector3f normal;

    public List<Vertex> getVertices() {
      return vertices;
    }

    @Override
    public void setTextureUV(double par1, double par3) {
      super.setTextureUV(par1, par3);
      this.hasTexture = true;
      this.textureU = par1;
      this.textureV = par3;
    }

    @Override
    public void setBrightness(int par1) {
      super.setBrightness(par1);
      this.hasBrightness = true;
      this.brightness = par1;
    }

    /**
     * Sets the RGBA values for the color. Also clamps them to 0-255.
     */
    @Override
    public void setColorRGBA(int r, int g, int b, int a) {
      super.setColorRGBA(r, g, b, a);
      hasColor = true;
      color = ColorUtil.toFloat(new Color(r, g, b, a));
    }

    /**
     * Sets the normal for the current draw call.
     */
    @Override
    public void setNormal(float par1, float par2, float par3) {
      super.setNormal(par1, par2, par3);
      hasNormals = true;
      normal = new Vector3f(par1, par2, par3);
    }

    public void reset() {
      hasNormals = false;
      this.hasColor = false;
      this.hasTexture = false;
      this.hasBrightness = false;
      vertices.clear();
    }

    @Override
    public void addVertex(double x, double y, double z) {
      Vertex v = new Vertex();
      v.setXYZ(x, y, z);
      if(hasTexture) {
        v.setUV(textureU, textureV);
      }
      if(hasBrightness) {
        v.setBrightness(brightness);
      }
      if(this.hasColor) {
        v.setColor(this.color);
      }
      if(hasNormals) {
        v.setNormal(normal);
      }
      vertices.add(v);
    }

  }

}
