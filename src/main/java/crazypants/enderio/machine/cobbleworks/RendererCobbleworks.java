package crazypants.enderio.machine.cobbleworks;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.framework.RendererFrameworkMachine;

public class RendererCobbleworks implements ISimpleBlockRenderingHandler {

  private final RendererFrameworkMachine frameRenderer;

  public RendererCobbleworks(RendererFrameworkMachine frameRenderer) {
    this.frameRenderer = frameRenderer;
    frameRenderer.registerController(EnderIO.blockCobbleworks.getControllerModelName(),
        frameRenderer.extractModelPart("ControllerActive"), frameRenderer.extractModelPart("Controller"));
  }

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
    frameRenderer.renderInventoryBlock(block, metadata, modelId, renderer);
  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
    return frameRenderer.renderWorldBlock(world, x, y, z, block, modelId, renderer);
  }

  @Override
  public boolean shouldRender3DInInventory(int modelId) {
    return true;
  }

  @Override
  public int getRenderId() {
    return EnderIO.blockCobbleworks.getRenderType();
  }

}
