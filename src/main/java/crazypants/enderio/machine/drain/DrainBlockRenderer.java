package crazypants.enderio.machine.drain;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import crazypants.enderio.EnderIO;
import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;

public class DrainBlockRenderer implements ISimpleBlockRenderingHandler {

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
    Tessellator.instance.startDrawingQuads();
    CubeRenderer.render(BoundingBox.UNIT_CUBE.scale(0.6, 0.6, 0.6), EnderIO.blockHyperCube.getIcon(0, 0));
    CubeRenderer.render(BoundingBox.UNIT_CUBE.scale(0.90, 0.90, 0.90), EnderIO.blockVacuumChest.getIcon(0, 0));
    Tessellator.instance.draw();
  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {

    final IIcon icon_sides = EnderIO.blockDrain.getIcon(2, 0);
    final float minU_sides = icon_sides.getMinU();
    final float maxV_sides = icon_sides.getMaxV();
    final float maxU_sides = icon_sides.getMaxU();
    final float minV_sides = icon_sides.getMinV();

    final IIcon icon_bottom = EnderIO.blockDrain.getIcon(0, 0);
    final float minU_bottom = icon_bottom.getMinU();
    final float maxV_bottom = icon_bottom.getMaxV();
    final float minV_bottom = icon_bottom.getMinV();
    final float maxU_bottom = icon_bottom.getMaxU();

    Tessellator.instance.addTranslation(x, y, z);

    Tessellator.instance.setNormal(0, 0, -1); // NORTH SIDE
    Tessellator.instance.addVertexWithUV(0.99D, 0.01D, 0.99D, minU_sides, maxV_sides);
    Tessellator.instance.addVertexWithUV(0.01D, 0.01D, 0.99D, maxU_sides, maxV_sides);
    Tessellator.instance.addVertexWithUV(0.01D, 0.99D, 0.99D, maxU_sides, minV_sides);
    Tessellator.instance.addVertexWithUV(0.99D, 0.99D, 0.99D, minU_sides, minV_sides);

    Tessellator.instance.setNormal(0, 0, 1); // SOUTH SIDE
    Tessellator.instance.addVertexWithUV(0.01D, 0.01D, 0.01D, minU_sides, maxV_sides);
    Tessellator.instance.addVertexWithUV(0.99D, 0.01D, 0.01D, maxU_sides, maxV_sides);
    Tessellator.instance.addVertexWithUV(0.99D, 0.99D, 0.01D, maxU_sides, minV_sides);
    Tessellator.instance.addVertexWithUV(0.01D, 0.99D, 0.01D, minU_sides, minV_sides);

    Tessellator.instance.setNormal(0, 1, 0); // TOP SIDE
    Tessellator.instance.addVertexWithUV(0.99D, 0.5D, 0.99D, minU_bottom, maxV_bottom);
    Tessellator.instance.addVertexWithUV(0.99D, 0.5D, 0.01D, minU_bottom, minV_bottom);
    Tessellator.instance.addVertexWithUV(0.01D, 0.5D, 0.01D, maxU_bottom, minV_bottom);
    Tessellator.instance.addVertexWithUV(0.01D, 0.5D, 0.99D, maxU_bottom, maxV_bottom);

    Tessellator.instance.setNormal(1, 0, 0); // EAST SIDE
    Tessellator.instance.addVertexWithUV(0.01D, 0.99D, 0.01D, minU_sides, minV_sides);
    Tessellator.instance.addVertexWithUV(0.01D, 0.99D, 0.99D, maxU_sides, minV_sides);
    Tessellator.instance.addVertexWithUV(0.01D, 0.01D, 0.99D, maxU_sides, maxV_sides);
    Tessellator.instance.addVertexWithUV(0.01D, 0.01D, 0.01D, minU_sides, maxV_sides);

    Tessellator.instance.setNormal(-1, 0, 0); // WEST SIDE
    Tessellator.instance.addVertexWithUV(0.99D, 0.01D, 0.01D, minU_sides, maxV_sides);
    Tessellator.instance.addVertexWithUV(0.99D, 0.01D, 0.99D, maxU_sides, maxV_sides);
    Tessellator.instance.addVertexWithUV(0.99D, 0.99D, 0.99D, maxU_sides, minV_sides);
    Tessellator.instance.addVertexWithUV(0.99D, 0.99D, 0.01D, minU_sides, minV_sides);

    Tessellator.instance.addTranslation(-x, -y, -z);

    renderer.renderStandardBlock(EnderIO.blockDrain, x, y, z);

    return true;
  }

  @Override
  public boolean shouldRender3DInInventory(int modelId) {    
    return true;
  }

  @Override
  public int getRenderId() {
    return EnderIO.blockDrain.renderId;
  }
  
}
