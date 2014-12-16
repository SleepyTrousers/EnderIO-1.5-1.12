package crazypants.enderio.machine.buffer;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.player.InventoryPlayer;
import crazypants.enderio.machine.gui.GuiMachineBase;
import crazypants.render.RenderUtil;

public class GuiBuffer extends GuiMachineBase {

  public GuiBuffer(InventoryPlayer par1InventoryPlayer, TileBuffer te) {
    super(te, new ContainerBuffer(par1InventoryPlayer, te));
  }
  
  @Override
  protected boolean showRecipeButton() {
    return false;
  }
  
  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture("enderio:textures/gui/buffer.png");
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);    

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }
}
