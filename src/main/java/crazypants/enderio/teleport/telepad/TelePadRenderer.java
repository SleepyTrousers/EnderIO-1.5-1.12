package crazypants.enderio.teleport.telepad;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.obj.GroupObject;
import crazypants.render.CubeRenderer;
import crazypants.render.RenderUtil;
import crazypants.render.TechneModelRenderer;
import crazypants.render.TechneUtil;

public class TelePadRenderer extends TechneModelRenderer {

  private List<GroupObject> glass = TechneUtil.getModel("models/telePadGlass");

  public TelePadRenderer() {
    super("models/telePad", BlockTelePad.renderId);
  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
    TileTelePad te = (TileTelePad) world.getTileEntity(x, y, z);
    IIcon cached = renderer.overrideBlockTexture;
    boolean ret = true;
    if(te.inNetwork()) {
      renderer.overrideBlockTexture = null;
      if(te.isMaster()) {
        if(RenderUtil.theRenderPass == 0) {
          super.renderWorldBlock(world, x, y, z, block, modelId, renderer);
        } else {
          TechneUtil.renderWorldBlock(glass, world, x, y, z, block, renderer);
        }
      } else {
        ret = false;
      }
    } else {
      renderer.renderStandardBlock(block, x, y, z);
    }
    renderer.overrideBlockTexture = cached;
    return ret;
  }

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
    CubeRenderer.render(block, metadata);
  }
}
