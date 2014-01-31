package crazypants.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;
import crazypants.vecmath.Vector3f;
import crazypants.vecmath.Vector4f;
import crazypants.vecmath.Vertex;

public class CustomRenderBlocks extends RenderBlocks {

  private static final Tessellator DEFAULT_TES = Tessellator.instance;

  private static final MockTesselator BUF_TES = new MockTesselator();

  boolean translateToXYZ = true;

  private final List<IRenderFace> renderFaceCallbacks = new ArrayList<IRenderFace>();
  private Icon overrideTexture;

  public CustomRenderBlocks(IBlockAccess par1iBlockAccess) {
    super(par1iBlockAccess);
  }

  public void addFaceRenderer(IRenderFace renderer) {
    renderFaceCallbacks.add(renderer);
  }

  public void setFaceRenderers(Collection<IRenderFace> renderers) {
    renderFaceCallbacks.clear();
    if(renderers != null) {
      renderFaceCallbacks.addAll(renderers);
    }
  }

  public void removeFaceRenderer(IRenderFace renderer) {
    renderFaceCallbacks.remove(renderer);
  }

  public void setDefaultTesselatorEnabled(boolean enabled) {
    if(enabled) {
      Tessellator.instance = DEFAULT_TES;
    } else {
      Tessellator.instance = BUF_TES;
    }
  }

  public boolean isTranslateToXYZ() {
    return translateToXYZ;
  }

  public void setTranslateToXYZ(boolean translateToXYZ) {
    this.translateToXYZ = translateToXYZ;
  }

  public Icon getOverrideTexture() {
    return overrideTexture;
  }

  public void setOverrideTexture(Icon overrideTexture) {
    this.overrideTexture = overrideTexture;
  }

  private void resetTesForFace() {
    BUF_TES.reset();
  }

  private void renderFace(ForgeDirection face, Block par1Block, double x, double y, double z, Icon texture) {
    if(renderFaceCallbacks.isEmpty()) {
      setDefaultTesselatorEnabled(true);
      doDefaultRenderFace(face, par1Block, x, y, z, overrideTexture == null ? texture : overrideTexture);
    } else {
      doDefaultRenderFaceToBuffer(face, par1Block, x, y, z, overrideTexture == null ? texture : overrideTexture);
      for (IRenderFace rf : renderFaceCallbacks) {
        rf.renderFace(this, face, par1Block, x, y, z, overrideTexture == null ? texture : overrideTexture, getBufferVertices(), translateToXYZ);
      }
    }
  }

  public void doDefaultRenderFaceToBuffer(ForgeDirection face, Block par1Block, double par2, double par4, double par6, Icon par8Icon) {
    setDefaultTesselatorEnabled(false);
    BUF_TES.reset();
    doDefaultRenderFace(face, par1Block, par2, par4, par6, par8Icon);
    setDefaultTesselatorEnabled(true);
  }

  public void doDefaultRenderFace(ForgeDirection face, Block par1Block, double par2, double par4, double par6, Icon par8Icon) {
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

  public List<Vertex> getBufferVertices() {
    return BUF_TES.getVertices();
  }

  public Tessellator getBufferTesselator() {
    return BUF_TES;
  }

  public Tessellator getDefaultTesselator() {
    return DEFAULT_TES;
  }
}