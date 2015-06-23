package crazypants.enderio.machine.obelisk;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import com.enderio.core.api.client.render.VertexTransform;
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.CubeRenderer;
import com.enderio.core.common.vecmath.Vector3d;
import com.enderio.core.common.vecmath.Vector3f;
import com.enderio.core.common.vecmath.Vertex;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import crazypants.enderio.EnderIO;

public class ObeliskRenderer implements ISimpleBlockRenderingHandler {

  private VertXForm xform = new VertXForm();
  private VertXForm2 xform2 = new VertXForm2();

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

    Tessellator.instance.addTranslation(x, y, z);

    IIcon icon = EnderIO.blockAttractor.getOnIcon();
    if(world != null) {
      icon = block.getIcon(world, x, y, z, 0);
      Tessellator.instance.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
    }

    if(renderer.hasOverrideBlockTexture()) {
      icon = renderer.overrideBlockTexture;
    }

    float height = 0.475f;
    float width = 0.5f;
    bb = BoundingBox.UNIT_CUBE.scale(width, height, 1).translate(0, -0.5f + height / 2, 0);
    xform2.isX = false;
    CubeRenderer.render(bb, icon, xform2, true);

    bb = BoundingBox.UNIT_CUBE.scale(1, height, width).translate(0, -0.5f + height / 2, 0);
    xform2.isX = true;
    CubeRenderer.render(bb, icon, xform2, true);

    Tessellator.instance.addTranslation(-x, -y, -z);

    return true;
  }

  @Override
  public boolean shouldRender3DInInventory(int modelId) {
    return true;
  }

  @Override
  public int getRenderId() {
    return BlockObeliskAbstract.defaultObeliskRenderId;
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
      double pinch = 0.8;
      if(vec.y > 0.5) {
        pinch = 0.4;
      }
      vec.x -= 0.5;
      vec.x *= pinch;
      vec.x += 0.5;
      vec.z -= 0.5;
      vec.z *= pinch;
      vec.z += 0.5;

      double scale = 0.5;
      vec.y -= 0.5;
      vec.y *= scale;
      vec.y += (0.5 * scale);
    }

    @Override
    public void applyToNormal(Vector3f vec) {
    }

  }

  private static class VertXForm2 implements VertexTransform {

    boolean isX = true;

    public VertXForm2() {
    }

    @Override
    public void apply(Vertex vertex) {
      apply(vertex.xyz);
    }

    @Override
    public void apply(Vector3d vec) {
      double pinch = 0.9;
      if(vec.y > 0.2) {
        pinch = 0.5;
      }
      if(isX) {
        vec.x -= 0.5;
        vec.x *= pinch;
        vec.x += 0.5;
      } else {
        vec.z -= 0.5;
        vec.z *= pinch;
        vec.z += 0.5;
      }
    }

    @Override
    public void applyToNormal(Vector3f vec) {
    }

  }
}
