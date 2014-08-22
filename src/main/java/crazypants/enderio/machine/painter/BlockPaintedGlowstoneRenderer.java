package crazypants.enderio.machine.painter;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.util.IBlockAccessWrapper;

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

    IBlockAccess origBa = rb.blockAccess;
    rb.blockAccess = new PaintedAccessWrapper(origBa);
    rb.renderBlockByRenderType(srcBlk, x, y, z);
    rb.blockAccess = origBa;

    return true;
  }

  @Override
  public boolean shouldRender3DInInventory(int arg0) {
    return true;
  }

  private class PaintedAccessWrapper extends IBlockAccessWrapper {

    public PaintedAccessWrapper(IBlockAccess ba) {
      super(ba);
    }

    @Override
    public Block getBlock(int x, int y, int z) {
      Block res = super.getBlock(x, y, z);
      if(res == EnderIO.blockPaintedGlowstone) {
        TileEntity te = getTileEntity(x, y, z);
        if(te instanceof TileEntityPaintedBlock) {
          TileEntityPaintedBlock tcb = (TileEntityPaintedBlock) te;
          Block fac = tcb.getSourceBlock();
          if(fac != null) {
            res = fac;
          }
        }
      }
      return res;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getLightBrightnessForSkyBlocks(int var1, int var2, int var3, int var4) {
      return wrapped.getLightBrightnessForSkyBlocks(var1, var2, var3, var4);
    }

    @Override
    public int getBlockMetadata(int x, int y, int z) {
      Block block = super.getBlock(x, y, z);
      if(block == EnderIO.blockPaintedGlowstone) {
        TileEntity te = getTileEntity(x, y, z);
        if(te instanceof TileEntityPaintedBlock) {
          TileEntityPaintedBlock tcb = (TileEntityPaintedBlock) te;
          Block fac = tcb.getSourceBlock();
          if(fac != null) {
            return tcb.getSourceBlockMetadata();
          }
        }
      }
      return super.getBlockMetadata(x, y, z);
    }

  }

}
