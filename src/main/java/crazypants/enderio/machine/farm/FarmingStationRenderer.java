package crazypants.enderio.machine.farm;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import crazypants.enderio.machine.generator.combustion.TranslatedCubeRenderer;
import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;
import crazypants.render.VertexTransform;
import crazypants.vecmath.Vector3d;
import crazypants.vecmath.Vector3f;
import crazypants.vecmath.Vertex;

public class FarmingStationRenderer implements ISimpleBlockRenderingHandler {

  private VertXForm xform = new VertXForm();

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {

    GL11.glDisable(GL11.GL_LIGHTING);
    Tessellator.instance.startDrawingQuads();
    renderWorldBlock(null, 0, 0, 0, block, 0, renderer);
    Tessellator.instance.draw();
    GL11.glEnable(GL11.GL_LIGHTING);
  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {

    BoundingBox bb = BoundingBox.UNIT_CUBE;
    TranslatedCubeRenderer.instance.renderBoundingBox(x, y, z, block, bb, xform, null, world != null);

    float scale = 0.7f;
    float width = 0.4f;
    float trans = (1 - scale) / 2;
    bb = BoundingBox.UNIT_CUBE.scale(1, scale, width);
    bb = bb.translate(0, -trans, 0);
    TranslatedCubeRenderer.instance.renderBoundingBox(x, y, z, block, bb, xform, null, world != null);

    bb = BoundingBox.UNIT_CUBE.scale(width, scale, 1);
    bb = bb.translate(0, -trans, 0);
    TranslatedCubeRenderer.instance.renderBoundingBox(x, y, z, block, bb, xform, null, world != null);

    float topWidth = 0.15f;
    bb = BoundingBox.UNIT_CUBE.scale(1, topWidth, 1);
    bb = bb.translate(0, 0.3f + topWidth / 2f, 0);
    TranslatedCubeRenderer.instance.renderBoundingBox(x, y, z, block, bb, xform, null, world != null);

    if(world != null) {
      TileEntity te = world.getTileEntity(x, y, z);
      if(te instanceof TileFarmStation && ((TileFarmStation) te).isActive()) {
        bb = BoundingBox.UNIT_CUBE.scale(1, 0.08, .4);
        bb = bb.translate(0, 0.1f, 0);
        bb = bb.translate(x, y, z);
        Tessellator.instance.setColorOpaque_F(1, 1, 1);
        CubeRenderer.render(bb, Blocks.portal.getBlockTextureFromSide(1));

        bb = BoundingBox.UNIT_CUBE.scale(.4, 0.08, 1);
        bb = bb.translate(0, 0.1f, 0);
        bb = bb.translate(x, y, z);
        Tessellator.instance.setColorOpaque_F(1, 1, 1);
        CubeRenderer.render(bb, Blocks.portal.getBlockTextureFromSide(1));
      }
    }

    return true;
  }

  @Override
  public boolean shouldRender3DInInventory(int modelId) {
    return true;
  }

  @Override
  public int getRenderId() {
    return BlockFarmStation.renderId;
  }

  private static class VertXForm implements VertexTransform {

    public VertXForm() {
    }

    @Override
    public void apply(Vertex vertex) {
      apply(vertex.xyz);
    }

    @Override
    public void apply(Vector3d vec) {
      if(vec.y > 0.9) {
        double pinch = 0.5;
        vec.x -= 0.5;
        vec.x *= pinch;
        vec.x += 0.5;
        vec.z -= 0.5;
        vec.z *= pinch;
        vec.z += 0.5;
      }

      double scale = 0.8;
      vec.y -= 0.5;
      vec.y *= scale;
      vec.y += (0.5 * scale);
    }

    @Override
    public void applyToNormal(Vector3f vec) {
    }

  }

}
