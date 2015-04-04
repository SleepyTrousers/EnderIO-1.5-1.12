package crazypants.enderio.machine.crusher;

import java.awt.Rectangle;

import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.machine.gui.GuiPoweredMachineBase;
import crazypants.gui.GuiToolTip;
import crazypants.render.RenderUtil;

@SideOnly(Side.CLIENT)
public class GuiCrusher extends GuiPoweredMachineBase<TileCrusher> {

  public GuiCrusher(InventoryPlayer par1InventoryPlayer, TileCrusher inventory) {
    super(inventory, new ContainerCrusher(par1InventoryPlayer, inventory));
    addToolTip(new GuiToolTip(new Rectangle(142, 23, 5, 17), "") {

      @Override
      protected void updateText() {
        text.clear();
        text.add(getTileEntity().getBallDurationScaled(100) + "%");
      }
    });

    addProgressTooltip(79, 31, 18, 24);
  }

  /**
   * Draw the background layer for the GuiContainer (everything behind the
   * items)
   */
  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture("enderio:textures/gui/crusher.png");

    drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.xSize, this.ySize);

    if(shouldRenderProgress()) {
      int barHeight = getTileEntity().getProgressScaled(24);
      drawTexturedModalRect(guiLeft + 79, guiTop + 31, 200, 0, 18, barHeight + 1);
    }

    int barHeight = getTileEntity().getBallDurationScaled(16);
    if(barHeight > 0) {
      drawTexturedModalRect(guiLeft + 142, guiTop + 23 + (16 - barHeight), 186, 31, 4, barHeight);
    }
    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }


}
