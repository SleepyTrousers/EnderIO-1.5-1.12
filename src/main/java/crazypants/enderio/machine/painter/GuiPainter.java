package crazypants.enderio.machine.painter;

import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.GuiMachineBase;
import crazypants.render.RenderUtil;

@SideOnly(Side.CLIENT)
public class GuiPainter extends GuiMachineBase {

  private AbstractMachineEntity tileEntity;

  public GuiPainter(InventoryPlayer par1InventoryPlayer, AbstractMachineEntity furnaceInventory) {
    super(furnaceInventory, new PainterContainer(par1InventoryPlayer, furnaceInventory));
    tileEntity = furnaceInventory;
  }

  /**
   * Draw the background layer for the GuiContainer (everything behind the
   * items)
   */
  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture("enderio:textures/gui/painter.png");
    int k = (width - xSize) / 2;
    int l = (height - ySize) / 2;

    drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    int i1;

    i1 = tileEntity.getProgressScaled(24);
    drawTexturedModalRect(k + 88, l + 34, 176, 14, i1 + 1, 16);

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }
}
