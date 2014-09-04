package crazypants.enderio.machine.slicensplice;

import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.GuiMachineBase;
import crazypants.enderio.machine.soul.ContainerSoulBinder;
import crazypants.render.RenderUtil;

public class GuiSliceAndSplice extends GuiMachineBase {

  private AbstractMachineEntity tileEntity;

  public GuiSliceAndSplice(InventoryPlayer par1InventoryPlayer, AbstractMachineEntity te) {
    super(te, new ContainerSliceAndSplice(par1InventoryPlayer, te));
    tileEntity = te;
  }

  /**
   * Draw the background layer for the GuiContainer (everything behind the
   * items)
   */
  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture("enderio:textures/gui/sliceAndSplice.png");
    int k = (width - xSize) / 2;
    int l = (height - ySize) / 2;

    drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    int i1;

//    i1 = tileEntity.getProgressScaled(24);
//    drawTexturedModalRect(k + 80, l + 34, 176, 14, i1 + 1, 16);

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

  @Override
  protected boolean showRecipeButton() {
    return false;
  }

}
