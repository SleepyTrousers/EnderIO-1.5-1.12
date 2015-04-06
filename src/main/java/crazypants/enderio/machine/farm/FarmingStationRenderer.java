package crazypants.enderio.machine.farm;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import crazypants.enderio.init.EIOBlocks;
import crazypants.enderio.machine.TechneMachineRenderer;
import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;

public class FarmingStationRenderer extends TechneMachineRenderer<TileFarmStation> {

  public FarmingStationRenderer() {
    super(EIOBlocks.blockFarmStation, "models/farm");
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

    return super.renderWorldBlock(world, x, y, z, block, modelId, renderer);
  }
}
