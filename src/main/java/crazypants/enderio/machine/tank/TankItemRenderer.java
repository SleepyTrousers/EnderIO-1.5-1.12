package crazypants.enderio.machine.tank;

import java.awt.Color;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import cofh.api.energy.IEnergyContainerItem;
import crazypants.enderio.EnderIO;
import crazypants.render.BoundingBox;
import crazypants.render.ColorUtil;
import crazypants.render.CubeRenderer;
import crazypants.render.RenderUtil;

public class TankItemRenderer implements IItemRenderer {

  private RenderItem ri = new RenderItem();

  @Override
  public boolean handleRenderType(ItemStack item, ItemRenderType type) {
    return type == ItemRenderType.INVENTORY;
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

    boolean hasFluid = false;
    if(item.stackTagCompound != null) {
      TileTank tt = new TileTank();
      tt.readCommon(item.stackTagCompound);

      TankFluidRenderer.renderTankFluid(tt.tank, 0, -0.1f, 0);
      hasFluid = true;
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
