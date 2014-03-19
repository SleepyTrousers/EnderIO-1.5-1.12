package crazypants.render;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;
import crazypants.vecmath.Vertex;

public class CustomRenderBlocks extends RenderBlocks {

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

  private void renderFace(ForgeDirection face, Block par1Block, double x, double y, double z, Icon texture) {
    if(renderFaceCallbacks.isEmpty()) {
      doDefaultRenderFace(face, par1Block, x, y, z, overrideTexture == null ? texture : overrideTexture);
    } else {
      List<Vertex> faceVertices = createVerticesForFace(face, par1Block, x, y, z, overrideTexture == null ? texture : overrideTexture);
      for (IRenderFace rf : renderFaceCallbacks) {
        rf.renderFace(this, face, par1Block, x, y, z, overrideTexture == null ? texture : overrideTexture, faceVertices, translateToXYZ);
      }
    }
  }

  public List<Vertex> createVerticesForFace(ForgeDirection face, Block par1Block, double x, double y, double z, Icon icon) {
    switch (face) {
    case DOWN:
      return calcFaceYNeg(par1Block, x, y, z, icon);
    case EAST:
      return calcFaceXPos(par1Block, x, y, z, icon);
    case NORTH:
      return calcFaceZNeg(par1Block, x, y, z, icon);
    case SOUTH:
      return calcFaceZPos(par1Block, x, y, z, icon);
    case UP:
      return calcFaceYPos(par1Block, x, y, z, icon);
    case WEST:
      return calcFaceXNeg(par1Block, x, y, z, icon);
    case UNKNOWN:
    default:
      break;
    }
    return Collections.emptyList();
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

  private List<Vertex> calcFaceXNeg(Block par1Block, double par2, double par4, double par6, Icon par8Icon) {

    if(hasOverrideBlockTexture()) {
      par8Icon = this.overrideBlockTexture;
    }

    double d3 = (double) par8Icon.getInterpolatedU(this.renderMinZ * 16.0D);
    double d4 = (double) par8Icon.getInterpolatedU(this.renderMaxZ * 16.0D);
    double d5 = (double) par8Icon.getInterpolatedV(16.0D - this.renderMaxY * 16.0D);
    double d6 = (double) par8Icon.getInterpolatedV(16.0D - this.renderMinY * 16.0D);
    double d7;

    if(this.flipTexture) {
      d7 = d3;
      d3 = d4;
      d4 = d7;
    }

    if(this.renderMinZ < 0.0D || this.renderMaxZ > 1.0D) {
      d3 = (double) par8Icon.getMinU();
      d4 = (double) par8Icon.getMaxU();
    }

    if(this.renderMinY < 0.0D || this.renderMaxY > 1.0D) {
      d5 = (double) par8Icon.getMinV();
      d6 = (double) par8Icon.getMaxV();
    }

    d7 = d4;
    double d8 = d3;
    double d9 = d5;
    double d10 = d6;

    if(this.uvRotateNorth == 1) {
      d3 = (double) par8Icon.getInterpolatedU(this.renderMinY * 16.0D);
      d5 = (double) par8Icon.getInterpolatedV(16.0D - this.renderMaxZ * 16.0D);
      d4 = (double) par8Icon.getInterpolatedU(this.renderMaxY * 16.0D);
      d6 = (double) par8Icon.getInterpolatedV(16.0D - this.renderMinZ * 16.0D);
      d9 = d5;
      d10 = d6;
      d7 = d3;
      d8 = d4;
      d5 = d6;
      d6 = d9;
    } else if(this.uvRotateNorth == 2) {
      d3 = (double) par8Icon.getInterpolatedU(16.0D - this.renderMaxY * 16.0D);
      d5 = (double) par8Icon.getInterpolatedV(this.renderMinZ * 16.0D);
      d4 = (double) par8Icon.getInterpolatedU(16.0D - this.renderMinY * 16.0D);
      d6 = (double) par8Icon.getInterpolatedV(this.renderMaxZ * 16.0D);
      d7 = d4;
      d8 = d3;
      d3 = d4;
      d4 = d8;
      d9 = d6;
      d10 = d5;
    } else if(this.uvRotateNorth == 3) {
      d3 = (double) par8Icon.getInterpolatedU(16.0D - this.renderMinZ * 16.0D);
      d4 = (double) par8Icon.getInterpolatedU(16.0D - this.renderMaxZ * 16.0D);
      d5 = (double) par8Icon.getInterpolatedV(this.renderMaxY * 16.0D);
      d6 = (double) par8Icon.getInterpolatedV(this.renderMinY * 16.0D);
      d7 = d4;
      d8 = d3;
      d9 = d5;
      d10 = d6;
    }

    double d11 = par2 + this.renderMinX;
    double d12 = par4 + this.renderMinY;
    double d13 = par4 + this.renderMaxY;
    double d14 = par6 + this.renderMinZ;
    double d15 = par6 + this.renderMaxZ;

    List<Vertex> result = new ArrayList<Vertex>(4);
    Vertex v;
    if(enableAO) {
      result.add(new Vertex(d11, d13, d15, d7, d9, brightnessTopLeft, colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft, 1));
      result.add(new Vertex(d11, d13, d14, d3, d5, brightnessBottomLeft, colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft, 1));
      result.add(new Vertex(d11, d12, d14, d8, d10, brightnessBottomRight, colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight, 1));
      result.add(new Vertex(d11, d12, d15, d4, d6, brightnessTopRight, colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight, 1));
    } else {
      result.add(new Vertex(d11, d13, d15, d7, d9));
      result.add(new Vertex(d11, d13, d14, d3, d5));
      result.add(new Vertex(d11, d12, d14, d8, d10));
      result.add(new Vertex(d11, d12, d15, d4, d6));
    }
    return result;
  }

  private List<Vertex> calcFaceYPos(Block par1Block, double par2, double par4, double par6, Icon par8Icon) {

    if(this.hasOverrideBlockTexture()) {
      par8Icon = this.overrideBlockTexture;
    }

    double d3 = (double) par8Icon.getInterpolatedU(this.renderMinX * 16.0D);
    double d4 = (double) par8Icon.getInterpolatedU(this.renderMaxX * 16.0D);
    double d5 = (double) par8Icon.getInterpolatedV(this.renderMinZ * 16.0D);
    double d6 = (double) par8Icon.getInterpolatedV(this.renderMaxZ * 16.0D);

    if(this.renderMinX < 0.0D || this.renderMaxX > 1.0D) {
      d3 = (double) par8Icon.getMinU();
      d4 = (double) par8Icon.getMaxU();
    }

    if(this.renderMinZ < 0.0D || this.renderMaxZ > 1.0D) {
      d5 = (double) par8Icon.getMinV();
      d6 = (double) par8Icon.getMaxV();
    }

    double d7 = d4;
    double d8 = d3;
    double d9 = d5;
    double d10 = d6;

    if(this.uvRotateTop == 1) {
      d3 = (double) par8Icon.getInterpolatedU(this.renderMinZ * 16.0D);
      d5 = (double) par8Icon.getInterpolatedV(16.0D - this.renderMaxX * 16.0D);
      d4 = (double) par8Icon.getInterpolatedU(this.renderMaxZ * 16.0D);
      d6 = (double) par8Icon.getInterpolatedV(16.0D - this.renderMinX * 16.0D);
      d9 = d5;
      d10 = d6;
      d7 = d3;
      d8 = d4;
      d5 = d6;
      d6 = d9;
    } else if(this.uvRotateTop == 2) {
      d3 = (double) par8Icon.getInterpolatedU(16.0D - this.renderMaxZ * 16.0D);
      d5 = (double) par8Icon.getInterpolatedV(this.renderMinX * 16.0D);
      d4 = (double) par8Icon.getInterpolatedU(16.0D - this.renderMinZ * 16.0D);
      d6 = (double) par8Icon.getInterpolatedV(this.renderMaxX * 16.0D);
      d7 = d4;
      d8 = d3;
      d3 = d4;
      d4 = d8;
      d9 = d6;
      d10 = d5;
    } else if(this.uvRotateTop == 3) {
      d3 = (double) par8Icon.getInterpolatedU(16.0D - this.renderMinX * 16.0D);
      d4 = (double) par8Icon.getInterpolatedU(16.0D - this.renderMaxX * 16.0D);
      d5 = (double) par8Icon.getInterpolatedV(16.0D - this.renderMinZ * 16.0D);
      d6 = (double) par8Icon.getInterpolatedV(16.0D - this.renderMaxZ * 16.0D);
      d7 = d4;
      d8 = d3;
      d9 = d5;
      d10 = d6;
    }

    double d11 = par2 + this.renderMinX;
    double d12 = par2 + this.renderMaxX;
    double d13 = par4 + this.renderMaxY;
    double d14 = par6 + this.renderMinZ;
    double d15 = par6 + this.renderMaxZ;

    List<Vertex> result = new ArrayList<Vertex>(4);
    Vertex v;
    if(enableAO) {
      v = new Vertex(d12, d13, d15, d4, d6, brightnessTopLeft, colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft, 1);
      result.add(v);
      v = new Vertex(d12, d13, d14, d7, d9, brightnessBottomLeft, colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft, 1);
      result.add(v);
      v = new Vertex(d11, d13, d14, d3, d5, brightnessBottomRight, colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight, 1);
      result.add(v);
      v = new Vertex(d11, d13, d15, d8, d10, brightnessTopRight, colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight, 1);
      result.add(v);
    } else {
      v = new Vertex(d12, d13, d15, d4, d6);
      result.add(v);
      v = new Vertex(d12, d13, d14, d7, d9);
      result.add(v);
      v = new Vertex(d11, d13, d14, d3, d5);
      result.add(v);
      v = new Vertex(d11, d13, d15, d8, d10);
      result.add(v);
    }
    return result;
  }

  private List<Vertex> calcFaceZPos(Block par1Block, double par2, double par4, double par6, Icon par8Icon)
  {
    Tessellator tessellator = Tessellator.instance;

    if(this.hasOverrideBlockTexture())
    {
      par8Icon = this.overrideBlockTexture;
    }

    double d3 = (double) par8Icon.getInterpolatedU(this.renderMinX * 16.0D);
    double d4 = (double) par8Icon.getInterpolatedU(this.renderMaxX * 16.0D);
    double d5 = (double) par8Icon.getInterpolatedV(16.0D - this.renderMaxY * 16.0D);
    double d6 = (double) par8Icon.getInterpolatedV(16.0D - this.renderMinY * 16.0D);
    double d7;

    if(this.flipTexture)
    {
      d7 = d3;
      d3 = d4;
      d4 = d7;
    }

    if(this.renderMinX < 0.0D || this.renderMaxX > 1.0D)
    {
      d3 = (double) par8Icon.getMinU();
      d4 = (double) par8Icon.getMaxU();
    }

    if(this.renderMinY < 0.0D || this.renderMaxY > 1.0D)
    {
      d5 = (double) par8Icon.getMinV();
      d6 = (double) par8Icon.getMaxV();
    }

    d7 = d4;
    double d8 = d3;
    double d9 = d5;
    double d10 = d6;

    if(this.uvRotateWest == 1)
    {
      d3 = (double) par8Icon.getInterpolatedU(this.renderMinY * 16.0D);
      d6 = (double) par8Icon.getInterpolatedV(16.0D - this.renderMinX * 16.0D);
      d4 = (double) par8Icon.getInterpolatedU(this.renderMaxY * 16.0D);
      d5 = (double) par8Icon.getInterpolatedV(16.0D - this.renderMaxX * 16.0D);
      d9 = d5;
      d10 = d6;
      d7 = d3;
      d8 = d4;
      d5 = d6;
      d6 = d9;
    }
    else if(this.uvRotateWest == 2)
    {
      d3 = (double) par8Icon.getInterpolatedU(16.0D - this.renderMaxY * 16.0D);
      d5 = (double) par8Icon.getInterpolatedV(this.renderMinX * 16.0D);
      d4 = (double) par8Icon.getInterpolatedU(16.0D - this.renderMinY * 16.0D);
      d6 = (double) par8Icon.getInterpolatedV(this.renderMaxX * 16.0D);
      d7 = d4;
      d8 = d3;
      d3 = d4;
      d4 = d8;
      d9 = d6;
      d10 = d5;
    }
    else if(this.uvRotateWest == 3)
    {
      d3 = (double) par8Icon.getInterpolatedU(16.0D - this.renderMinX * 16.0D);
      d4 = (double) par8Icon.getInterpolatedU(16.0D - this.renderMaxX * 16.0D);
      d5 = (double) par8Icon.getInterpolatedV(this.renderMaxY * 16.0D);
      d6 = (double) par8Icon.getInterpolatedV(this.renderMinY * 16.0D);
      d7 = d4;
      d8 = d3;
      d9 = d5;
      d10 = d6;
    }

    double d11 = par2 + this.renderMinX;
    double d12 = par2 + this.renderMaxX;
    double d13 = par4 + this.renderMinY;
    double d14 = par4 + this.renderMaxY;
    double d15 = par6 + this.renderMaxZ;

    List<Vertex> result = new ArrayList<Vertex>();
    Vertex v;
    if(enableAO) {
      result.add(new Vertex(d11, d14, d15, d3, d5, brightnessTopLeft, colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft, 1));
      result.add(new Vertex(d11, d13, d15, d8, d10, brightnessBottomLeft, this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft, 1));
      result.add(new Vertex(d12, d13, d15, d4, d6, brightnessBottomRight, colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight, 1));
      result.add(new Vertex(d12, d14, d15, d7, d9, brightnessTopRight, colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight, 1));
    } else {
      result.add(new Vertex(d11, d14, d15, d3, d5));
      result.add(new Vertex(d11, d13, d15, d8, d10));
      result.add(new Vertex(d12, d13, d15, d4, d6));
      result.add(new Vertex(d12, d14, d15, d7, d9));
    }
    return result;
  }

  private List<Vertex> calcFaceZNeg(Block par1Block, double par2, double par4, double par6, Icon par8Icon) {

    if(this.hasOverrideBlockTexture())
    {
      par8Icon = this.overrideBlockTexture;
    }

    double d3 = (double) par8Icon.getInterpolatedU(this.renderMinX * 16.0D);
    double d4 = (double) par8Icon.getInterpolatedU(this.renderMaxX * 16.0D);
    double d5 = (double) par8Icon.getInterpolatedV(16.0D - this.renderMaxY * 16.0D);
    double d6 = (double) par8Icon.getInterpolatedV(16.0D - this.renderMinY * 16.0D);
    double d7;

    if(this.flipTexture) {
      d7 = d3;
      d3 = d4;
      d4 = d7;
    }

    if(this.renderMinX < 0.0D || this.renderMaxX > 1.0D) {
      d3 = (double) par8Icon.getMinU();
      d4 = (double) par8Icon.getMaxU();
    }

    if(this.renderMinY < 0.0D || this.renderMaxY > 1.0D) {
      d5 = (double) par8Icon.getMinV();
      d6 = (double) par8Icon.getMaxV();
    }

    d7 = d4;
    double d8 = d3;
    double d9 = d5;
    double d10 = d6;

    if(this.uvRotateEast == 2) {
      d3 = (double) par8Icon.getInterpolatedU(this.renderMinY * 16.0D);
      d5 = (double) par8Icon.getInterpolatedV(16.0D - this.renderMinX * 16.0D);
      d4 = (double) par8Icon.getInterpolatedU(this.renderMaxY * 16.0D);
      d6 = (double) par8Icon.getInterpolatedV(16.0D - this.renderMaxX * 16.0D);
      d9 = d5;
      d10 = d6;
      d7 = d3;
      d8 = d4;
      d5 = d6;
      d6 = d9;
    } else if(this.uvRotateEast == 1) {
      d3 = (double) par8Icon.getInterpolatedU(16.0D - this.renderMaxY * 16.0D);
      d5 = (double) par8Icon.getInterpolatedV(this.renderMaxX * 16.0D);
      d4 = (double) par8Icon.getInterpolatedU(16.0D - this.renderMinY * 16.0D);
      d6 = (double) par8Icon.getInterpolatedV(this.renderMinX * 16.0D);
      d7 = d4;
      d8 = d3;
      d3 = d4;
      d4 = d8;
      d9 = d6;
      d10 = d5;
    } else if(this.uvRotateEast == 3) {
      d3 = (double) par8Icon.getInterpolatedU(16.0D - this.renderMinX * 16.0D);
      d4 = (double) par8Icon.getInterpolatedU(16.0D - this.renderMaxX * 16.0D);
      d5 = (double) par8Icon.getInterpolatedV(this.renderMaxY * 16.0D);
      d6 = (double) par8Icon.getInterpolatedV(this.renderMinY * 16.0D);
      d7 = d4;
      d8 = d3;
      d9 = d5;
      d10 = d6;
    }

    double d11 = par2 + this.renderMinX;
    double d12 = par2 + this.renderMaxX;
    double d13 = par4 + this.renderMinY;
    double d14 = par4 + this.renderMaxY;
    double d15 = par6 + this.renderMinZ;

    List<Vertex> result = new ArrayList<Vertex>();
    Vertex v;
    if(enableAO) {
      result.add(new Vertex(d11, d14, d15, d7, d9, brightnessTopLeft, colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft, 1));
      result.add(new Vertex(d12, d14, d15, d3, d5, brightnessBottomLeft, colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft, 1));
      result.add(new Vertex(d12, d13, d15, d8, d10, brightnessBottomRight, colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight, 1));
      result.add(new Vertex(d11, d13, d15, d4, d6, brightnessTopRight, colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight, 1));
    } else {
      result.add(new Vertex(d11, d14, d15, d7, d9));
      result.add(new Vertex(d12, d14, d15, d3, d5));
      result.add(new Vertex(d12, d13, d15, d8, d10));
      result.add(new Vertex(d11, d13, d15, d4, d6));
    }
    return result;
  }

  private List<Vertex> calcFaceXPos(Block par1Block, double par2, double par4, double par6, Icon par8Icon)
  {
    Tessellator tessellator = Tessellator.instance;

    if(this.hasOverrideBlockTexture())
    {
      par8Icon = this.overrideBlockTexture;
    }

    double d3 = (double) par8Icon.getInterpolatedU(this.renderMinZ * 16.0D);
    double d4 = (double) par8Icon.getInterpolatedU(this.renderMaxZ * 16.0D);
    double d5 = (double) par8Icon.getInterpolatedV(16.0D - this.renderMaxY * 16.0D);
    double d6 = (double) par8Icon.getInterpolatedV(16.0D - this.renderMinY * 16.0D);
    double d7;

    if(this.flipTexture)
    {
      d7 = d3;
      d3 = d4;
      d4 = d7;
    }

    if(this.renderMinZ < 0.0D || this.renderMaxZ > 1.0D)
    {
      d3 = (double) par8Icon.getMinU();
      d4 = (double) par8Icon.getMaxU();
    }

    if(this.renderMinY < 0.0D || this.renderMaxY > 1.0D)
    {
      d5 = (double) par8Icon.getMinV();
      d6 = (double) par8Icon.getMaxV();
    }

    d7 = d4;
    double d8 = d3;
    double d9 = d5;
    double d10 = d6;

    if(this.uvRotateSouth == 2)
    {
      d3 = (double) par8Icon.getInterpolatedU(this.renderMinY * 16.0D);
      d5 = (double) par8Icon.getInterpolatedV(16.0D - this.renderMinZ * 16.0D);
      d4 = (double) par8Icon.getInterpolatedU(this.renderMaxY * 16.0D);
      d6 = (double) par8Icon.getInterpolatedV(16.0D - this.renderMaxZ * 16.0D);
      d9 = d5;
      d10 = d6;
      d7 = d3;
      d8 = d4;
      d5 = d6;
      d6 = d9;
    }
    else if(this.uvRotateSouth == 1)
    {
      d3 = (double) par8Icon.getInterpolatedU(16.0D - this.renderMaxY * 16.0D);
      d5 = (double) par8Icon.getInterpolatedV(this.renderMaxZ * 16.0D);
      d4 = (double) par8Icon.getInterpolatedU(16.0D - this.renderMinY * 16.0D);
      d6 = (double) par8Icon.getInterpolatedV(this.renderMinZ * 16.0D);
      d7 = d4;
      d8 = d3;
      d3 = d4;
      d4 = d8;
      d9 = d6;
      d10 = d5;
    }
    else if(this.uvRotateSouth == 3)
    {
      d3 = (double) par8Icon.getInterpolatedU(16.0D - this.renderMinZ * 16.0D);
      d4 = (double) par8Icon.getInterpolatedU(16.0D - this.renderMaxZ * 16.0D);
      d5 = (double) par8Icon.getInterpolatedV(this.renderMaxY * 16.0D);
      d6 = (double) par8Icon.getInterpolatedV(this.renderMinY * 16.0D);
      d7 = d4;
      d8 = d3;
      d9 = d5;
      d10 = d6;
    }

    double d11 = par2 + this.renderMaxX;
    double d12 = par4 + this.renderMinY;
    double d13 = par4 + this.renderMaxY;
    double d14 = par6 + this.renderMinZ;
    double d15 = par6 + this.renderMaxZ;

    List<Vertex> result = new ArrayList<Vertex>(4);
    Vertex v;
    if(enableAO) {
      result.add(new Vertex(d11, d12, d15, d8, d10, brightnessTopLeft, colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft, 1));
      result.add(new Vertex(d11, d12, d14, d4, d6, brightnessBottomLeft, colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft, 1));
      result.add(new Vertex(d11, d13, d14, d7, d9, brightnessBottomRight, colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight, 1));
      result.add(new Vertex(d11, d13, d15, d3, d5, brightnessTopRight, colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight, 1));
    } else {
      result.add(new Vertex(d11, d12, d15, d8, d10));
      result.add(new Vertex(d11, d12, d14, d4, d6));
      result.add(new Vertex(d11, d13, d14, d7, d9));
      result.add(new Vertex(d11, d13, d15, d3, d5));
    }
    return result;
  }

  private List<Vertex> calcFaceYNeg(Block par1Block, double x, double y, double z, Icon icon) {

    if(hasOverrideBlockTexture()) {
      icon = this.overrideBlockTexture;
    }

    double minU = (double) icon.getInterpolatedU(this.renderMinX * 16.0D);
    double maxU = (double) icon.getInterpolatedU(this.renderMaxX * 16.0D);
    double minV = (double) icon.getInterpolatedV(this.renderMinZ * 16.0D);
    double maxV = (double) icon.getInterpolatedV(this.renderMaxZ * 16.0D);

    if(renderMinX < 0.0D || renderMaxX > 1.0D) {
      minU = (double) icon.getMinU();
      maxU = (double) icon.getMaxU();
    }

    if(this.renderMinZ < 0.0D || this.renderMaxZ > 1.0D) {
      minV = (double) icon.getMinV();
      maxV = (double) icon.getMaxV();
    }

    double d7 = maxU;
    double d8 = minU;
    double d9 = minV;
    double d10 = maxV;

    if(uvRotateBottom == 2) {
      minU = (double) icon.getInterpolatedU(this.renderMinZ * 16.0D);
      minV = (double) icon.getInterpolatedV(16.0D - this.renderMaxX * 16.0D);
      maxU = (double) icon.getInterpolatedU(this.renderMaxZ * 16.0D);
      maxV = (double) icon.getInterpolatedV(16.0D - this.renderMinX * 16.0D);
      d9 = minV;
      d10 = maxV;
      d7 = minU;
      d8 = maxU;
      minV = maxV;
      maxV = d9;
    } else if(uvRotateBottom == 1) {

      minU = (double) icon.getInterpolatedU(16.0D - this.renderMaxZ * 16.0D);
      minV = (double) icon.getInterpolatedV(this.renderMinX * 16.0D);
      maxU = (double) icon.getInterpolatedU(16.0D - this.renderMinZ * 16.0D);
      maxV = (double) icon.getInterpolatedV(this.renderMaxX * 16.0D);
      d7 = maxU;
      d8 = minU;
      minU = maxU;
      maxU = d8;
      d9 = maxV;
      d10 = minV;
    } else if(uvRotateBottom == 3) {
      minU = (double) icon.getInterpolatedU(16.0D - this.renderMinX * 16.0D);
      maxU = (double) icon.getInterpolatedU(16.0D - this.renderMaxX * 16.0D);
      minV = (double) icon.getInterpolatedV(16.0D - this.renderMinZ * 16.0D);
      maxV = (double) icon.getInterpolatedV(16.0D - this.renderMaxZ * 16.0D);
      d7 = maxU;
      d8 = minU;
      d9 = minV;
      d10 = maxV;
    }

    double minX = x + this.renderMinX;
    double maxX = x + this.renderMaxX;
    double minY = y + this.renderMinY;
    double minZ = z + this.renderMinZ;
    double maxZ = z + this.renderMaxZ;

    List<Vertex> result = new ArrayList<Vertex>(4);
    Vertex v;
    if(this.enableAO) {
      v = new Vertex(minX, minY, maxZ, d8, d10, brightnessTopLeft, colorRedTopLeft, colorGreenTopLeft, colorBlueTopLeft, 1f);
      result.add(v);
      v = new Vertex(minX, minY, minZ, minU, minV, brightnessBottomLeft, colorRedBottomLeft, colorGreenBottomLeft, colorBlueBottomLeft, 1f);
      result.add(v);
      v = new Vertex(maxX, minY, minZ, d7, d9, brightnessBottomRight, colorRedBottomRight, colorGreenBottomRight, colorBlueBottomRight, 1f);
      result.add(v);
      v = new Vertex(maxX, minY, maxZ, maxU, maxV, brightnessTopRight, colorRedTopRight, colorGreenTopRight, colorBlueTopRight, 1f);
      result.add(v);
    } else {
      v = new Vertex(minX, minY, maxZ, d8, d10);
      result.add(v);
      v = new Vertex(minX, minY, minZ, minU, minV);
      result.add(v);
      v = new Vertex(maxX, minY, minZ, d7, d9);
      result.add(v);
      v = new Vertex(maxX, minY, maxZ, maxU, maxV);
      result.add(v);
    }
    return result;
  }

}