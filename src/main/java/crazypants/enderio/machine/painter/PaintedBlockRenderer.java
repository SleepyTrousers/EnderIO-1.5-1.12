package crazypants.enderio.machine.painter;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import crazypants.render.CubeRenderer;
import crazypants.render.IconUtil;

public class PaintedBlockRenderer implements ISimpleBlockRenderingHandler {

  private int renderId;
  private Block defaultBlock;

  public PaintedBlockRenderer(int renderId, Block defaultBlock) {
    this.renderId = renderId;
    this.defaultBlock = defaultBlock;
  }

  @Override
  public int getRenderId() {
    return renderId;
  }

  @Override
  public void renderInventoryBlock(Block blk, int meta, int modelId, RenderBlocks arg3) {
    Tessellator.instance.startDrawingQuads();
    CubeRenderer.render(blk, meta);
    Tessellator.instance.draw();
  }

  @Override
  public boolean renderWorldBlock(IBlockAccess ba, int x, int y, int z, Block block, int arg5, RenderBlocks rb) {

    TileEntity tile = ba.getTileEntity(x, y, z);
    if(!(tile instanceof IPaintableTileEntity)) {
      return false;
    }
    IPaintableTileEntity te = (IPaintableTileEntity) tile;
    Block srcBlk = te.getSourceBlock();
    if(srcBlk == null) {
      srcBlk = defaultBlock;
    }

    IBlockAccess origBa = rb.blockAccess;
    try {      
      rb.blockAccess = new PaintedBlockAccessWrapper(origBa);
      if(srcBlk == block) {
        rb.renderStandardBlock(srcBlk, x, y, z);
      } else {
        rb.renderBlockByRenderType(srcBlk, x, y, z);
      }      
    } catch (Exception e) {
      //just in case the paint source wont render safely in this way
      rb.setOverrideBlockTexture(IconUtil.errorTexture);
      rb.renderStandardBlock(Blocks.stone, x, y, z);
      rb.setOverrideBlockTexture(null);
    } finally {
      rb.blockAccess = origBa;
    }

    return true;
  }

  @Override
  public boolean shouldRender3DInInventory(int arg0) {
    return false;
  }

}
