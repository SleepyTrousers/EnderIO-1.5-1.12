package crazypants.enderio.item.darksteel;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import cofh.api.energy.IEnergyContainerItem;
import crazypants.render.ColorUtil;
import crazypants.render.RenderUtil;
import crazypants.vecmath.Vector4f;

public class PoweredItemRenderer implements IItemRenderer {

  private RenderItem ri = new RenderItem();

  @Override
  public boolean handleRenderType(ItemStack item, ItemRenderType type) {
    return type == ItemRenderType.INVENTORY;
  }

  @Override
  public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
    return false;
  }

  @Override
  public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
    if(data != null && data.length > 0) {
      renderToInventory(item, (RenderBlocks) data[0]);
    }
  }

  public void renderToInventory(ItemStack item, RenderBlocks renderBlocks) {

    Minecraft mc = Minecraft.getMinecraft();
    ri.renderItemIntoGUI(mc.fontRenderer, mc.getTextureManager(), item, 0, 0, true);

    IEnergyContainerItem armor = (IEnergyContainerItem) item.getItem();
    if(isJustCrafted(item)) {
      return;
    }

    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.renderQuad2D(2, 13, 0, 13, 3, ColorUtil.getRGB(Color.black));

    double maxDam = item.getMaxDamage();
    double dispDamage = item.getItemDamageForDisplay();
    float r = 0.0f;
    float g = 1f;
    float b = 0.0f;
    int y = 14;
    renderBar(y, maxDam, dispDamage, Color.green, Color.red);

    maxDam = armor.getMaxEnergyStored(item);
    dispDamage = armor.getEnergyStored(item);

    r = 0.4f;
    g = 0.4f;
    b = 1f;
    y = 13;
    renderBar(y, maxDam, maxDam - dispDamage, new Color(200, 100, 10), Color.red);

    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

  }

  private boolean isJustCrafted(ItemStack item) {
    return EnergyUpgrade.loadFromItem(item) == null && item.getItemDamageForDisplay() == 0;
  }

  private void renderBar(int y, double maxDam, double dispDamage, Color full, Color empty) {
    double ratio = dispDamage / maxDam;
    Vector4f fg = ColorUtil.toFloat(full);
    Vector4f ec = ColorUtil.toFloat(empty);

    fg.interpolate(ec, (float) ratio);

    Vector4f bg = new Vector4f(fg);
    bg.scale(0.25 + (0.75 * 1 - ratio));

    int barLength = (int) Math.round(12.0 * (1 - ratio));
    RenderUtil.renderQuad2D(2, y, 0, 12, 1, bg);
    RenderUtil.renderQuad2D(2, y, 0, barLength, 1, fg);
  }

  private void renderBar(int y, double maxDam, double dispDamage) {
    int ratio = (int) Math.round(255.0D - dispDamage * 255.0D / maxDam);
    int fgCol = 255 - ratio << 16 | ratio << 8;
    int bgCol = (255 - ratio) / 4 << 16 | 16128;
    int barLength = (int) Math.round(12.0D - dispDamage * 12.0D / maxDam);
    RenderUtil.renderQuad2D(2, y, 0, 12, 1, bgCol);
    RenderUtil.renderQuad2D(2, y, 0, barLength, 1, fgCol);
  }
}