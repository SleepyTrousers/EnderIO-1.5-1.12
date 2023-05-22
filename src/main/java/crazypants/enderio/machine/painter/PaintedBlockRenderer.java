package crazypants.enderio.machine.painter;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.client.render.CubeRenderer;
import com.enderio.core.client.render.IconUtil;
import com.enderio.core.common.util.BlockCoord;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

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
        if (!(tile instanceof IPaintableTileEntity)) {
            return false;
        }
        IPaintableTileEntity te = (IPaintableTileEntity) tile;
        Block srcBlk = te.getSourceBlock();
        if (srcBlk == null) {
            srcBlk = defaultBlock;
        }

        IBlockAccess origBa = rb.blockAccess;
        try {
            rb.blockAccess = new PaintedBlockAccessWrapper(origBa);
            if (block.isOpaqueCube() && nonSolidPaintedBlockAround(new BlockCoord(x, y, z), origBa, rb.blockAccess)) {
                if (srcBlk == block) {
                    rb.setRenderAllFaces(true);
                    rb.renderStandardBlock(srcBlk, x, y, z);
                    rb.setRenderAllFaces(false);
                } else {
                    rb.renderBlockAllFaces(srcBlk, x, y, z);
                }
            } else {
                if (srcBlk == block) {
                    rb.renderStandardBlock(srcBlk, x, y, z);
                } else {
                    rb.renderBlockByRenderType(srcBlk, x, y, z);
                }
            }
        } catch (Exception e) {
            // just in case the paint source wont render safely in this way
            rb.setOverrideBlockTexture(IconUtil.errorTexture);
            rb.renderStandardBlock(Blocks.stone, x, y, z);
            rb.setOverrideBlockTexture(null);
        } finally {
            rb.blockAccess = origBa;
        }

        return true;
    }

    /*
     * If a non-solid block is painted with a solid block, the renderer would think it didn't need to render faces of
     * adjacent blocks that touch it because it only sees the paint. This detects this case, so the renderer can be told
     * to just render all faces, even if it thinks it could save some quads.
     */
    private boolean nonSolidPaintedBlockAround(BlockCoord bc, IBlockAccess reality, IBlockAccess fake) {
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            BlockCoord toTest = bc.getLocation(dir);
            if (toTest.y >= 0 && toTest.y <= 255
                    && toTest.getBlock(reality).isOpaqueCube() != toTest.getBlock(fake).isOpaqueCube()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean shouldRender3DInInventory(int arg0) {
        return false;
    }
}
