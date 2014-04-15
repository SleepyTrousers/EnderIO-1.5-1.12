package crazypants.enderio.farm;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import crazypants.enderio.EnderIO;
import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;
import crazypants.render.RenderUtil;
import crazypants.render.VertexTransform;
import crazypants.vecmath.Vector3d;
import crazypants.vecmath.Vector3f;

public class FarmingStationRenderer implements ISimpleBlockRenderingHandler {

  private VertXForm xform = new VertXForm();

  @Override
  public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
    Tessellator.instance.startDrawingQuads();
    renderWorldBlock(null, 0, 0, 0, block, modelId, renderer);
    Tessellator.instance.draw();
  }

  @Override
  public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {

    IIcon[] textures = new IIcon[6];
    textures[0] = EnderIO.blockFarmStation.getIcon(3, 0);
    textures[1] = EnderIO.blockFarmStation.getIcon(3, 0);
    textures[2] = EnderIO.blockFarmStation.getIcon(1, 0);
    textures[3] = EnderIO.blockFarmStation.getIcon(0, 0);
    textures[4] = EnderIO.blockFarmStation.getIcon(3, 0);
    textures[5] = EnderIO.blockFarmStation.getIcon(3, 0);

    Tessellator.instance.setBrightness(15 << 20 | 15 << 4);
    float b = 1;
    if(world != null) {
      b = RenderUtil.claculateTotalBrightnessForLocation(Minecraft.getMinecraft().theWorld, x, y, z);
    }

    float[] cols = new float[6];
    for (int i = 0; i < 6; i++) {
      float m = RenderUtil.getColorMultiplierForFace(ForgeDirection.values()[i]);
      cols[i] = b * m;
    }

    BoundingBox bb = BoundingBox.UNIT_CUBE;
    xform.set(x, y, z);
    CubeRenderer.render(bb, textures, xform, cols);

    float scale = 0.7f;
    float width = 0.4f;
    float trans = (1 - scale)/2;
    bb = BoundingBox.UNIT_CUBE.scale(1, scale, width);
    bb = bb.translate(0, -trans, 0);
    Tessellator.instance.addTranslation(x, y, z);
    CubeRenderer.render(bb, textures, null, cols);
    Tessellator.instance.addTranslation(-x, -y, -z);

    bb = BoundingBox.UNIT_CUBE.scale(width, scale, 1);
    bb = bb.translate(0, -trans, 0);
    Tessellator.instance.addTranslation(x, y, z);
    CubeRenderer.render(bb, textures, null, cols);
    Tessellator.instance.addTranslation(-x, -y, -z);

    float topWidth = 0.15f;
    bb = BoundingBox.UNIT_CUBE.scale(1, topWidth, 1);
    bb = bb.translate(0, 0.2f + topWidth/2f, 0);
    Tessellator.instance.addTranslation(x, y, z);
    CubeRenderer.render(bb, textures, null, cols);
    Tessellator.instance.addTranslation(-x, -y, -z);


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

    int x;
    int y;
    int z;

    public VertXForm() {
    }

    void set(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
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

      vec.add(x, y, z);
    }

    @Override
    public void applyToNormal(Vector3f vec) {
    }

  }

}
