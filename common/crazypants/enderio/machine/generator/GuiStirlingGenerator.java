package crazypants.enderio.machine.generator;

import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.machine.GuiMachineBase;
import crazypants.render.RenderUtil;

@SideOnly(Side.CLIENT)
public class GuiStirlingGenerator extends GuiMachineBase {

  private TileEntityStirlingGenerator entity;

  public GuiStirlingGenerator(InventoryPlayer par1InventoryPlayer, TileEntityStirlingGenerator te) {
    super(te, new StirlingGeneratorContainer(par1InventoryPlayer, te));
    this.entity = te;
  }

  /**
   * Draw the background layer for the GuiContainer (everything behind the
   * items)
   */
  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture("enderio:textures/gui/stirlingGenerator.png");
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);
    int scaled;

    if(entity.getProgress() < 1 && entity.getProgress() > 0) {
      scaled = entity.getProgressScaled(12);
      drawTexturedModalRect(sx + 80, sy + 65 - scaled, 176, 12 - scaled, 14, scaled + 2);
    }

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);

  }
}
