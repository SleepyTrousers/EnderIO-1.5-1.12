package crazypants.render;

import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class CustomCubeRenderer {

  public static final CustomCubeRenderer instance = new CustomCubeRenderer();

  private CustomRenderBlocks rb = null;

  private IIcon overrideTexture;

  public void renderBlock(IBlockAccess ba, Block par1Block, int par2, int par3, int par4) {
    renderBlock(ba, par1Block, par2, par3, par4, (IRenderFace) null);
  }

  public void renderBlock(IBlockAccess ba, Block par1Block, int par2, int par3, int par4, IRenderFace renderer) {
    if(rb == null) {
      rb = new CustomRenderBlocks(ba);
    }

    rb.blockAccess = ba;
    rb.setOverrideTexture(getOverrideTexture());
    rb.setFaceRenderers(null);
    if(renderer != null) {
      rb.addFaceRenderer(renderer);
    }
    rb.setRenderBoundsFromBlock(par1Block);
    rb.renderStandardBlock(par1Block, par2, par3, par4);

  }

  public void renderBlock(IBlockAccess ba, Block par1Block, int par2, int par3, int par4, Collection<IRenderFace> renderers) {
    if(rb == null) {
      rb = new CustomRenderBlocks(ba);
    }
    rb.blockAccess = ba;
    rb.setOverrideTexture(getOverrideTexture());
    rb.setFaceRenderers(renderers);
    rb.setRenderBoundsFromBlock(par1Block);
    rb.renderStandardBlock(par1Block, par2, par3, par4);

  }

  public IIcon getOverrideTexture() {
    return overrideTexture;
  }

  public void setOverrideTexture(IIcon overrideTexture) {
    this.overrideTexture = overrideTexture;
  }

  public CustomRenderBlocks getCustomRenderBlocks() {
    return rb;
  }


}
