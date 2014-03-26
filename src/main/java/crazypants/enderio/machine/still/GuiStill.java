package crazypants.enderio.machine.still;

import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.machine.GuiMachineBase;
import crazypants.render.RenderUtil;

public class GuiStill extends GuiMachineBase {

  private final TileStill still;

  public GuiStill(InventoryPlayer inventory, TileStill te) {
    super(te, new ContainerStill(inventory, te));
    still = te;
  }

  /**
   * Draw the background layer for the GuiContainer (everything behind the
   * items)
   */
  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture("enderio:textures/gui/still.png");
    int guiLeft = (width - xSize) / 2;
    int guiTop = (height - ySize) / 2;
    drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

    //    int i1 = tileEntity.getProgressScaled(24);
    //    drawTexturedModalRect(guiLeft + 79, guiTop + 31, 200, 0, 18, i1 + 1);

    if(still.getProgress() < 1 && still.getProgress() > 0) {
      int scaled = still.getProgressScaled(12);
      drawTexturedModalRect(guiLeft + 89, guiTop + 76 - scaled, 176, 12 - scaled, 14, scaled + 2);
    }

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

  @Override
  protected int getPowerX() {
    return 10;
  }

  @Override
  protected int getPowerY() {
    return 13;
  }

  @Override
  protected int getPowerHeight() {
    return 60;
  }

}
