package crazypants.enderio.machine.tank;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.CubeRenderer;
import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.EnderIO;

public class TankItemRenderer implements IItemRenderer {

  @Override
  public boolean handleRenderType(ItemStack item, ItemRenderType type) {
    return true;
  }

  @Override
  public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
    return true;
  }

  @Override
  public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
    if (data != null && data.length > 0) {
      renderToInventory(item, (RenderBlocks) data[0]);
    }
  }

  public void renderToInventory(ItemStack item, RenderBlocks renderBlocks) {
    if (item.hasTagCompound()) {
      TankFluidRenderer.renderTankFluid(TileTank.loadTank(item.stackTagCompound), 0, -0.1f, 0);
    }

    GL11.glEnable(GL11.GL_ALPHA_TEST);
    Block block = EnderIO.blockTank;
    int meta = item.getItemDamage();

    IIcon[] icons = RenderUtil.getBlockTextures(block, meta);
    BoundingBox bb = BoundingBox.UNIT_CUBE.translate(0, -0.1f, 0);
    Tessellator.instance.startDrawingQuads();
    CubeRenderer.render(bb, icons, null, RenderUtil.getDefaultPerSideBrightness());
    Tessellator.instance.draw();
  }
}
