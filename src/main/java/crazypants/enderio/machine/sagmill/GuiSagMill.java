package crazypants.enderio.machine.sagmill;

import java.awt.Rectangle;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.widget.GuiToolTip;

import crazypants.enderio.machine.gui.GuiPoweredMachineBase;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiSagMill extends GuiPoweredMachineBase<TileSagMill> {

  public GuiSagMill(InventoryPlayer par1InventoryPlayer, TileSagMill inventory) {
    super(inventory, new ContainerSagMill(par1InventoryPlayer, inventory), "crusher");
    addToolTip(new GuiToolTip(new Rectangle(142, 23, 5, 17), "") {

      @Override
      protected void updateText() {
        text.clear();
        text.add(getTileEntity().getBallDurationScaled(100) + "%");
      }
    });

    addProgressTooltip(79, 31, 18, 24);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    bindGuiTexture();

    drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.xSize, this.ySize);

    if (shouldRenderProgress()) {
      int barHeight = getProgressScaled(24);
      drawTexturedModalRect(guiLeft + 79, guiTop + 31, 200, 0, 18, barHeight + 1);
    }

    int barHeight = getTileEntity().getBallDurationScaled(16);
    if (barHeight > 0) {
      drawTexturedModalRect(guiLeft + 142, guiTop + 23 + (16 - barHeight), 186, 31, 4, barHeight);
    }
    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

}
