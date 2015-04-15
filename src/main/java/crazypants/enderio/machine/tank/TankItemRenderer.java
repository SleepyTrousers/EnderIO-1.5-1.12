package crazypants.enderio.machine.tank;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.fluids.FluidTank;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.EnderIO;
import crazypants.enderio.tool.SmartTank;
import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;
import crazypants.render.RenderUtil;

public class TankItemRenderer implements IItemRenderer {

  private RenderItem ri = new RenderItem();

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
    if(data != null && data.length > 0) {
      renderToInventory(item, (RenderBlocks) data[0]);
    }
  }

  public void renderToInventory(ItemStack item, RenderBlocks renderBlocks) {

    if (item.getItem() instanceof BlockItemTank) {
      SmartTank tank = TileTank.readTankFromItem(item);
      if (tank != null) {
        TankFluidRenderer.renderTankFluid(tank, 0, -0.1f, 0);
      }
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
