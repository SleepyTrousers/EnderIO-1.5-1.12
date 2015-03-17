package crazypants.enderio.machine.farm;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.obj.GroupObject;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;
import crazypants.render.TechneUtil;

public class FarmingStationRenderer implements ISimpleBlockRenderingHandler {

  private List<GroupObject> model = TechneUtil.getModel("models/farm");

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
    TechneUtil.renderInventoryBlock(model, block, metadata);
  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
    IIcon override = renderer.overrideBlockTexture;

    if(world != null) {
      TileEntity te = world.getTileEntity(x, y, z);
      if(te instanceof TileFarmStation && ((TileFarmStation) te).isActive()) {
        BoundingBox bb = BoundingBox.UNIT_CUBE.scale(10D / 16D, 0.25, 10D / 16D);
        bb = bb.scale(1.01, 1, 1.01);
        bb = bb.translate(0, 5f / 16f, 0);
        bb = bb.translate(x, y, z);
        Tessellator.instance.setColorOpaque_F(1, 1, 1);
        CubeRenderer.render(bb, override != null ? override : Blocks.portal.getBlockTextureFromSide(1));
      }
    }

    if(override != null) {
      return TechneUtil.renderWorldBlock(model, override, world, x, y, z, block);
    }
    return TechneUtil.renderWorldBlock(model, world, x, y, z, block);
  }

  @Override
  public boolean shouldRender3DInInventory(int modelId) {
    return true;
  }

  @Override
  public int getRenderId() {
    return BlockFarmStation.renderId;
  }
}
