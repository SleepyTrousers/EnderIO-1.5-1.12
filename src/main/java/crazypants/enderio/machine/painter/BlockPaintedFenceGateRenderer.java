package crazypants.enderio.machine.painter;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import crazypants.enderio.EnderIO;

public class BlockPaintedFenceGateRenderer implements ISimpleBlockRenderingHandler {

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
        renderer.renderBlockAsItem(Blocks.fence_gate, metadata, 1);
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return BlockPaintedFenceGate.renderId;
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess blockAccess, int par2, int par3, int par4, Block block, int modelId,
            RenderBlocks rb) {

        BlockFenceGate par1BlockFenceGate = (BlockFenceGate) block;

        boolean flag = true;
        int l = rb.blockAccess.getBlockMetadata(par2, par3, par4);
        boolean flag1 = BlockFenceGate.isFenceGateOpen(l);
        int i1 = BlockDirectional.getDirection(l);
        float f = 0.375F;
        float f1 = 0.5625F;
        float f2 = 0.75F;
        float f3 = 0.9375F;
        float f4 = 0.3125F;
        float f5 = 1.0F;

        if ((i1 == 2 || i1 == 0) && rb.blockAccess.getBlock(par2 - 1, par3, par4) == Blocks.cobblestone_wall
                && rb.blockAccess.getBlock(par2 + 1, par3, par4) == Blocks.cobblestone_wall
                || (i1 == 3 || i1 == 1) && rb.blockAccess.getBlock(par2, par3, par4 - 1) == Blocks.cobblestone_wall
                        && rb.blockAccess.getBlock(par2, par3, par4 + 1) == Blocks.cobblestone_wall
                || (i1 == 2 || i1 == 0) && rb.blockAccess.getBlock(par2 - 1, par3, par4) == EnderIO.blockPaintedWall
                        && rb.blockAccess.getBlock(par2 + 1, par3, par4) == EnderIO.blockPaintedWall
                || (i1 == 3 || i1 == 1) && rb.blockAccess.getBlock(par2, par3, par4 - 1) == EnderIO.blockPaintedWall
                        && rb.blockAccess.getBlock(par2, par3, par4 + 1) == EnderIO.blockPaintedWall) {
            f -= 0.1875F;
            f1 -= 0.1875F;
            f2 -= 0.1875F;
            f3 -= 0.1875F;
            f4 -= 0.1875F;
            f5 -= 0.1875F;
        }

        rb.renderAllFaces = true;
        float f6;
        float f7;
        float f8;
        float f9;

        if (i1 != 3 && i1 != 1) {
            f6 = 0.0F;
            f8 = 0.125F;
            f7 = 0.4375F;
            f9 = 0.5625F;
            rb.setRenderBounds(f6, f4, f7, f8, f5, f9);
            rb.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            f6 = 0.875F;
            f8 = 1.0F;
            rb.setRenderBounds(f6, f4, f7, f8, f5, f9);
            rb.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
        } else {
            rb.uvRotateTop = 1;
            f6 = 0.4375F;
            f8 = 0.5625F;
            f7 = 0.0F;
            f9 = 0.125F;
            rb.setRenderBounds(f6, f4, f7, f8, f5, f9);
            rb.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            f7 = 0.875F;
            f9 = 1.0F;
            rb.setRenderBounds(f6, f4, f7, f8, f5, f9);
            rb.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            rb.uvRotateTop = 0;
        }

        if (flag1) {
            if (i1 == 2 || i1 == 0) {
                rb.uvRotateTop = 1;
            }

            if (i1 == 3) {
                rb.setRenderBounds(0.8125D, f, 0.0D, 0.9375D, f3, 0.125D);
                rb.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
                rb.setRenderBounds(0.8125D, f, 0.875D, 0.9375D, f3, 1.0D);
                rb.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
                rb.setRenderBounds(0.5625D, f, 0.0D, 0.8125D, f1, 0.125D);
                rb.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
                rb.setRenderBounds(0.5625D, f, 0.875D, 0.8125D, f1, 1.0D);
                rb.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
                rb.setRenderBounds(0.5625D, f2, 0.0D, 0.8125D, f3, 0.125D);
                rb.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
                rb.setRenderBounds(0.5625D, f2, 0.875D, 0.8125D, f3, 1.0D);
                rb.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            } else if (i1 == 1) {
                rb.setRenderBounds(0.0625D, f, 0.0D, 0.1875D, f3, 0.125D);
                rb.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
                rb.setRenderBounds(0.0625D, f, 0.875D, 0.1875D, f3, 1.0D);
                rb.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
                rb.setRenderBounds(0.1875D, f, 0.0D, 0.4375D, f1, 0.125D);
                rb.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
                rb.setRenderBounds(0.1875D, f, 0.875D, 0.4375D, f1, 1.0D);
                rb.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
                rb.setRenderBounds(0.1875D, f2, 0.0D, 0.4375D, f3, 0.125D);
                rb.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
                rb.setRenderBounds(0.1875D, f2, 0.875D, 0.4375D, f3, 1.0D);
                rb.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            } else if (i1 == 0) {
                rb.setRenderBounds(0.0D, f, 0.8125D, 0.125D, f3, 0.9375D);
                rb.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
                rb.setRenderBounds(0.875D, f, 0.8125D, 1.0D, f3, 0.9375D);
                rb.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
                rb.setRenderBounds(0.0D, f, 0.5625D, 0.125D, f1, 0.8125D);
                rb.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
                rb.setRenderBounds(0.875D, f, 0.5625D, 1.0D, f1, 0.8125D);
                rb.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
                rb.setRenderBounds(0.0D, f2, 0.5625D, 0.125D, f3, 0.8125D);
                rb.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
                rb.setRenderBounds(0.875D, f2, 0.5625D, 1.0D, f3, 0.8125D);
                rb.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            } else if (i1 == 2) {
                rb.setRenderBounds(0.0D, f, 0.0625D, 0.125D, f3, 0.1875D);
                rb.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
                rb.setRenderBounds(0.875D, f, 0.0625D, 1.0D, f3, 0.1875D);
                rb.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
                rb.setRenderBounds(0.0D, f, 0.1875D, 0.125D, f1, 0.4375D);
                rb.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
                rb.setRenderBounds(0.875D, f, 0.1875D, 1.0D, f1, 0.4375D);
                rb.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
                rb.setRenderBounds(0.0D, f2, 0.1875D, 0.125D, f3, 0.4375D);
                rb.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
                rb.setRenderBounds(0.875D, f2, 0.1875D, 1.0D, f3, 0.4375D);
                rb.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            }
        } else if (i1 != 3 && i1 != 1) {
            f6 = 0.375F;
            f8 = 0.5F;
            f7 = 0.4375F;
            f9 = 0.5625F;
            rb.setRenderBounds(f6, f, f7, f8, f3, f9);
            rb.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            f6 = 0.5F;
            f8 = 0.625F;
            rb.setRenderBounds(f6, f, f7, f8, f3, f9);
            rb.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            f6 = 0.625F;
            f8 = 0.875F;
            rb.setRenderBounds(f6, f, f7, f8, f1, f9);
            rb.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            rb.setRenderBounds(f6, f2, f7, f8, f3, f9);
            rb.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            f6 = 0.125F;
            f8 = 0.375F;
            rb.setRenderBounds(f6, f, f7, f8, f1, f9);
            rb.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            rb.setRenderBounds(f6, f2, f7, f8, f3, f9);
            rb.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
        } else {
            rb.uvRotateTop = 1;
            f6 = 0.4375F;
            f8 = 0.5625F;
            f7 = 0.375F;
            f9 = 0.5F;
            rb.setRenderBounds(f6, f, f7, f8, f3, f9);
            rb.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            f7 = 0.5F;
            f9 = 0.625F;
            rb.setRenderBounds(f6, f, f7, f8, f3, f9);
            rb.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            f7 = 0.625F;
            f9 = 0.875F;
            rb.setRenderBounds(f6, f, f7, f8, f1, f9);
            rb.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            rb.setRenderBounds(f6, f2, f7, f8, f3, f9);
            rb.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            f7 = 0.125F;
            f9 = 0.375F;
            rb.setRenderBounds(f6, f, f7, f8, f1, f9);
            rb.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
            rb.setRenderBounds(f6, f2, f7, f8, f3, f9);
            rb.renderStandardBlock(par1BlockFenceGate, par2, par3, par4);
        }

        rb.renderAllFaces = false;
        rb.uvRotateTop = 0;
        rb.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
        return flag;
    }
}
