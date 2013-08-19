package crazypants.enderio.machine.light;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import crazypants.enderio.EnderIO;
import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;
import crazypants.render.RenderUtil;

public class ElectricLightRenderer implements ISimpleBlockRenderingHandler {

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
    // this is highly non thread safe but atm all rendereding is single threaded
    // so it should be fine, i just really dont like it
    // int renderId = EnderIO.blockElectricLight.renderId;
    // EnderIO.blockElectricLight.renderId = 0;
    // renderer.renderBlockAsItem(EnderIO.blockElectricLight, metadata, modelID)
    // ;
    // EnderIO.blockElectricLight.renderId = renderId;

    BoundingBox bb = new BoundingBox(0, 0, 0, 1, 0.2, 1);
    boolean doDraw = false;
    if (!Tessellator.instance.isDrawing) {
      doDraw = true;
      Tessellator.instance.startDrawingQuads();
    }
    CubeRenderer.render(bb, block.getBlockTextureFromSide(0));
    if (doDraw) {
      Tessellator.instance.draw();
    }
  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
    block.setBlockBoundsBasedOnState(world, x, y, z);
    BoundingBox bb = new BoundingBox(block.getBlockBoundsMinX(), block.getBlockBoundsMinY(), block.getBlockBoundsMinZ(), block.getBlockBoundsMaxX(),
        block.getBlockBoundsMaxY(), block.getBlockBoundsMaxZ());

    bb = bb.translate(x, y, z);
    RenderUtil.setTesselatorBrightness(world, x, y, z);

    Icon[] textures = new Icon[6];
     
    textures[0] = block.getBlockTexture(world, x, y, z, ForgeDirection.NORTH.ordinal());
    textures[1] = block.getBlockTexture(world, x, y, z, ForgeDirection.SOUTH.ordinal());
    textures[2] = block.getBlockTexture(world, x, y, z, ForgeDirection.UP.ordinal());
    textures[3] = block.getBlockTexture(world, x, y, z, ForgeDirection.DOWN.ordinal());
    textures[4] = block.getBlockTexture(world, x, y, z, ForgeDirection.WEST.ordinal());
    textures[5] = block.getBlockTexture(world, x, y, z, ForgeDirection.EAST.ordinal());    
    
    CubeRenderer.render(bb, textures, null);

    return true;
  }

  @Override
  public boolean shouldRender3DInInventory() {
    return true;
  }

  @Override
  public int getRenderId() {
    return BlockElectricLight.renderId;
  }

}
