package crazypants.enderio.machines.machine.sagmill;

import java.awt.Rectangle;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.lang.LangPower;
import crazypants.enderio.base.machine.gui.GuiInventoryMachineBase;
import crazypants.enderio.base.machine.gui.PowerBar;
import crazypants.enderio.base.recipe.sagmill.IGrindingMultiplier;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.text.TextFormatting;

public class GuiSagMill extends GuiInventoryMachineBase<TileSagMill> {

  boolean isSimple;

  public GuiSagMill(@Nonnull InventoryPlayer par1InventoryPlayer, @Nonnull TileSagMill inventory) {
    super(inventory, ContainerSagMill.create(par1InventoryPlayer, inventory), "crusher", "crusher_light");
    isSimple = inventory instanceof TileSagMill.Simple;
    if (!isSimple) {
      addToolTip(new GuiToolTip(new Rectangle(142, 23, 5, 17), "") {

        @Override
        protected void updateText() {
          text.clear();
          final IGrindingMultiplier ball = getTileEntity().grindingBall;
          if (ball != null) {
            text.add(Lang.GRINDING_BALL_DURABILITY.get(getTileEntity().getBallDurationScaled(100)));
            text.add(Lang.GRINDING_BALL_1.get(TextFormatting.BLUE));
            text.add(Lang.GRINDING_BALL_2.get(TextFormatting.GRAY, LangPower.toPercent(ball.getGrindingMultiplier())));
            text.add(Lang.GRINDING_BALL_3.get(TextFormatting.GRAY, LangPower.toPercent(ball.getChanceMultiplier())));
            text.add(Lang.GRINDING_BALL_4.get(TextFormatting.GRAY, LangPower.toPercent(ball.getPowerMultiplier())));
          }
        }
      });
    }

    addProgressTooltip(79, 31, 18, 24);
    redstoneButton.setIsVisible(!isSimple);

    addDrawingElement(new PowerBar(inventory, this));
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    bindGuiTexture(isSimple ? 1 : 0);

    drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.xSize, this.ySize);

    if (shouldRenderProgress()) {
      int barHeight = getProgressScaled(24);
      drawTexturedModalRect(guiLeft + 79, guiTop + 31, 200, 0, 18, barHeight + 1);
    }

    int barHeight = getTileEntity().getBallDurationScaled(16);
    if (!isSimple && barHeight > 0) {
      drawTexturedModalRect(guiLeft + 142, guiTop + 23 + (16 - barHeight), 186, 31, 4, barHeight);
    }
    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

  @Override
  protected void renderSlotHighlight(@Nonnull Slot slot, @Nonnull Vector4f col) {
    // Check if this is a simple sag mill, if so don't draw the grinding slot highlight
    if (isSimple && slot.getSlotIndex() == ContainerSagMill.GRINDING_BALL_SLOT) {
      return;
    }
    super.renderSlotHighlight(slot, col);
  }

}
