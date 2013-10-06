package crazypants.enderio.machine.crusher;

import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.GuiMachineBase;
import crazypants.render.RenderUtil;

public class GuiCrusher extends GuiMachineBase {

  private AbstractMachineEntity tileEntity;

  public GuiCrusher(InventoryPlayer par1InventoryPlayer, AbstractMachineEntity inventory) {
    super(inventory, new ContainerCrusher(par1InventoryPlayer, inventory));
    tileEntity = inventory;
  }

  /**
   * Draw the background layer for the GuiContainer (everything behind the
   * items)
   */
  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture("enderio:textures/gui/crusher.png");
    int guiLeft = (width - xSize) / 2;
    int guiTop = (height - ySize) / 2;

    drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.xSize, this.ySize);
    int i1;

    i1 = tileEntity.getProgressScaled(24);
    drawTexturedModalRect(guiLeft + 79, guiTop + 31, 200, 0, 18, i1 + 1);

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }
}
