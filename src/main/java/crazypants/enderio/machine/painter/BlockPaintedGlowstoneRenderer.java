package crazypants.enderio.machine.painter;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class BlockPaintedGlowstoneRenderer implements ISimpleBlockRenderingHandler {

  @Override
  public int getRenderId() {
    return BlockPaintedGlowstone.renderId;
  }

  @Override
  public void renderInventoryBlock(Block arg0, int arg1, int arg2, RenderBlocks arg3) {
  }

  @Override
  public boolean renderWorldBlock(IBlockAccess ba, int x, int y, int z, Block block, int arg5, RenderBlocks rb) {

    BlockPaintedGlowstone paintedGlowstone = (BlockPaintedGlowstone) block;
    TileEntity tile = ba.getTileEntity(x, y, z);
    if(!(tile instanceof TileEntityPaintedBlock)) {
      return false;
    }
    TileEntityPaintedBlock te = (TileEntityPaintedBlock) tile;
    Block srcBlk = te.getSourceBlock();
    if(srcBlk == null) {
      srcBlk = Blocks.glowstone;
    }

    IBlockAccess origBa = rb.blockAccess;
    rb.blockAccess = new PaintedBlockAccessWrapper(origBa);
    rb.renderBlockByRenderType(srcBlk, x, y, z);
    rb.blockAccess = origBa;

    return true;
  }

  @Override
  public boolean shouldRender3DInInventory(int arg0) {
    return true;
  }

}
