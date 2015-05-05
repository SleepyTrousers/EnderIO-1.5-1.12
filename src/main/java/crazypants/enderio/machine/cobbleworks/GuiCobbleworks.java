package crazypants.enderio.machine.cobbleworks;

import java.awt.Rectangle;

import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.EnderIO;
import crazypants.enderio.fluid.Fluids;
import crazypants.enderio.machine.gui.GuiMachineBase;
import crazypants.enderio.machine.gui.GuiPoweredMachineBase;
import crazypants.gui.GuiToolTip;
import crazypants.render.RenderUtil;
import crazypants.util.IProgressTile;
import crazypants.util.Lang;

public class GuiCobbleworks extends GuiPoweredMachineBase<TileCobbleworks> {

  public GuiCobbleworks(InventoryPlayer par1InventoryPlayer, TileCobbleworks te) {
    super(te, new ContainerCobbleworks(par1InventoryPlayer, te));
  }

  @Override
  protected boolean showRecipeButton() {
    return false;
  }


  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture("enderio:textures/gui/cobbleworks.png");
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);

    if (getTileEntity().isActive()) {
      int phase = (int) ((EnderIO.proxy.getTickCount() >> 1) % 3);

      if (getTileEntity().isActive(0)) {
        drawTexturedModalRect(sx + 26, sy + 8, 16 * phase, 170, 16, 20);
        if (getTileEntity().isActive(1)) {
          drawTexturedModalRect(sx + 49, sy + 25, 16 * phase, 190, 16, 10);
          drawTexturedModalRect(sx + 60, sy + 35, 16 * phase, 200, 16, 20);
          if (getTileEntity().isActive(2)) {
            drawTexturedModalRect(sx + 90, sy + 41, 16 * phase, 220, 16, 10);
            drawTexturedModalRect(sx + 114, sy + 35, 16 * phase, 200, 16, 20);
            if (getTileEntity().isActive(3)) {
              drawTexturedModalRect(sx + 144, sy + 41, 16 * phase, 220, 16, 10);
              drawTexturedModalRect(sx + 168, sy + 35, 16 * phase, 200, 16, 20);
            }
          }
        }
      }
    }

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);

    RenderUtil.bindBlockTexture();
  }

  @Override
  public int getXSize() {
    return 234;
  }

  @Override
  protected int getPowerU() {
    return 234;
  }

  @Override
  protected int getPowerY() {
    return 8;
  }

  @Override
  protected int getPowerHeight() {
    return 48;
  }

}
