package crazypants.enderio.machine.farm;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.machine.GuiMachineBase;
import crazypants.render.ColorUtil;
import crazypants.render.RenderUtil;

public class GuiFarmStation extends GuiMachineBase {

  public GuiFarmStation(InventoryPlayer par1InventoryPlayer, TileFarmStation machine) {
    super(machine, new FarmStationContainer(par1InventoryPlayer, machine));
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture("enderio:textures/gui/farmStation.png");
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);

    FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
    
    GL11.glEnable(GL11.GL_BLEND);    
    fr.drawString("SW", sx + 55, sy + 41, ColorUtil.getARGB(1f,1f,0.35f,1f), true);    
    fr.drawString("NW", sx + 55, sy + 59, ColorUtil.getARGB(1f,1f,0.35f,1f), true);
    fr.drawString("SE", sx + 73, sy + 41, ColorUtil.getARGB(1f,1f,0.35f,1f), true);
    fr.drawString("NE", sx + 73, sy + 59, ColorUtil.getARGB(1f,1f,0.35f,1f), true);        
    GL11.glDisable(GL11.GL_BLEND);
    
    RenderUtil.bindTexture("enderio:textures/gui/farmStation.png");
    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

  @Override
  protected boolean showRecipeButton() {
    return false;
  }
}
