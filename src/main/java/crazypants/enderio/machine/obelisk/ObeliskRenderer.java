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
import com.enderio.core.client.render.IconUtil;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vector3d;
import com.enderio.core.common.vecmath.Vector3f;
import com.enderio.core.common.vecmath.Vertex;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.AbstractMachineBlock;

public class ObeliskRenderer implements ISimpleBlockRenderingHandler {

  private VertXForm2 xform2 = new VertXForm2();
  private VertXForm3 xform3 = new VertXForm3();

  private static float WIDE_PINCH = 0.9f;
  private static float WIDTH = 18f / 32f * WIDE_PINCH;

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

    Tessellator.instance.addTranslation(x, y, z);

    IIcon icon;
    IIcon[] icons;
    IIcon[] bottomIcon = null;
    if (world != null) { // block
      RenderUtil.setTesselatorBrightness(world, x, y, z);
      icon = block.getIcon(world, x, y, z, 0);
      icons = RenderUtil.getBlockTextures(world, x, y, z);
    } else { // item
      icon = block.getIcon(1, 0);
      icons = RenderUtil.getBlockTextures(block, 0);
    }

    if (icons != null) {
      bottomIcon = new IIcon[6];
      for (int i = 0; i < bottomIcon.length; i++) {
        bottomIcon[i] = IconUtil.blankTexture;
      }
      bottomIcon[0] = icons[0];
      icons[0] = IconUtil.blankTexture;
    }

    if(renderer.hasOverrideBlockTexture()) {
      icon = renderer.overrideBlockTexture;
      icons = null;
    }

    float height = 0.475f;
    float width = WIDTH;
    BoundingBox bb = BoundingBox.UNIT_CUBE.scale(width, height, 1).translate(0, -0.5f + height / 2, 0);
    xform2.isX = false;
    if (icons == null) {
      CubeRenderer.render(bb, icon, xform2, true);
    } else {
      CubeRenderer.render(bb, icons, xform2, true);
    }

    bb = BoundingBox.UNIT_CUBE.scale(1, height, width).translate(0, -0.5f + height / 2, 0);
    xform2.isX = true;
    if (icons == null) {
      CubeRenderer.render(bb, icon, xform2, true);
    } else {
      icons[1] = IconUtil.blankTexture;
      CubeRenderer.render(bb, icons, xform2, true);
    }

    if (bottomIcon != null) {
      bb = BoundingBox.UNIT_CUBE;
      CubeRenderer.render(bb, bottomIcon, xform3, true);
    }

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
      double pinch = WIDE_PINCH;
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

  private static class VertXForm3 implements VertexTransform {

    public VertXForm3() {
    }

    @Override
    public void apply(Vertex vertex) {
      apply(vertex.xyz);
    }

    @Override
    public void apply(Vector3d vec) {
      vec.x -= 0.5;
      vec.x *= (double) WIDE_PINCH;
      vec.x += 0.5;
      vec.z -= 0.5;
      vec.z *= (double) WIDE_PINCH;
      vec.z += 0.5;
    }

    @Override
    public void applyToNormal(Vector3f vec) {
    }

  }

}
