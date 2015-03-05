package crazypants.enderio.machine.drain;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.EnderIO;
import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;
import crazypants.render.RenderUtil;

public class DrainItemRenderer implements IItemRenderer {

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
    if(item.stackTagCompound != null) {
      TileDrain tt = new TileDrain();
      tt.readCommon(item.stackTagCompound);
      DrainFluidRenderer.renderTankFluid(tt, 0f, -0.1f, 0f);
    }

    GL11.glEnable(GL11.GL_ALPHA_TEST);
    Block block = EnderIO.blockDrain;
    int meta = item.getItemDamage();
    
    IIcon[] icons = RenderUtil.getBlockTextures(block, meta);
    BoundingBox bb = BoundingBox.UNIT_CUBE.translate(0, -0.1f, 0);
    Tessellator.instance.startDrawingQuads();
    CubeRenderer.render(bb, icons, null, RenderUtil.getDefaultPerSideBrightness());
    Tessellator.instance.draw();
  }

}
