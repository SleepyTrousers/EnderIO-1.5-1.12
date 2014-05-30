package crazypants.enderio.machine.crafter;

import java.awt.Color;
import java.awt.Point;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.machine.GuiMachineBase;
import crazypants.enderio.machine.generator.stirling.StirlingGeneratorContainer;
import crazypants.enderio.machine.generator.stirling.TileEntityStirlingGenerator;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.render.ColorUtil;
import crazypants.render.RenderUtil;
import crazypants.util.Lang;

public class GuiCrafter extends GuiMachineBase  {

  private TileCrafter entity;

  public GuiCrafter(InventoryPlayer par1InventoryPlayer, TileCrafter te) {
    super(te, new ContainerCrafter(par1InventoryPlayer, te));
    this.entity = te;
    xSize = getXSize();
  }

  @Override
  protected boolean showRecipeButton() {
    return false;
  }

  @Override
  public int getXSize() {
    return 219;
  }
  
  protected int getPowerU() {
    return 220;
  }

  @Override
  protected int getPowerX() {    
    return 9;
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture("enderio:textures/gui/crafter.png");
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);    

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }
  
}
