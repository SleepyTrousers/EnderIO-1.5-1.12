package crazypants.enderio.machine;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;
import crazypants.render.CustomCubeRenderer;
import crazypants.render.CustomRenderBlocks;
import crazypants.render.IRenderFace;
import crazypants.render.RenderUtil;
import crazypants.vecmath.Vertex;

public class AbstractMachineRenderer implements ISimpleBlockRenderingHandler {

  private OverlayRenderer overlayRenderer = new OverlayRenderer();

  private AbstractMachineEntity curEnt;

  private CustomCubeRenderer ccr = new CustomCubeRenderer();

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {

    BoundingBox bb = BoundingBox.UNIT_CUBE;

    Tessellator.instance.startDrawingQuads();

    IIcon[] textures = RenderUtil.getBlockTextures(block, metadata);

    float[] brightnessPerSide = new float[6];
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      brightnessPerSide[dir.ordinal()] = Math.max(RenderUtil.getColorMultiplierForFace(dir) + 0.1f, 1f);
    }

    CubeRenderer.render(bb, textures, null, brightnessPerSide);

    Tessellator.instance.draw();

  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {

    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof AbstractMachineEntity) {
      curEnt = (AbstractMachineEntity)te;      
    } else {
      curEnt = null;
    }
    ccr.renderBlock(world, block, x, y, z, overlayRenderer);

    return true;
  }

  @Override
  public boolean shouldRender3DInInventory(int modelId) {
    return true;
  }

  @Override
  public int getRenderId() {
    return AbstractMachineBlock.renderId;
  }

  private class OverlayRenderer implements IRenderFace {

    @Override
    public void renderFace(CustomRenderBlocks rb, ForgeDirection face, Block par1Block, double x, double y, double z, IIcon texture, List<Vertex> refVertices,
        boolean translateToXyz) {

      ccr.getCustomRenderBlocks().doDefaultRenderFace(face,par1Block,x,y,z,texture);
      if(curEnt != null && par1Block instanceof AbstractMachineBlock) {
        IoMode mode = curEnt.getIoMode(face);
        IIcon tex = ((AbstractMachineBlock)par1Block).getOverlayIconForMode(mode);
        if(tex != null) {
          ccr.getCustomRenderBlocks().doDefaultRenderFace(face,par1Block,x,y,z, tex);
        }
      }

    }

  }

}
