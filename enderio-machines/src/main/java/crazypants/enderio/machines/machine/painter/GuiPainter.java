package crazypants.enderio.machines.machine.painter;

import javax.annotation.Nonnull;

import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.base.machine.gui.GuiInventoryMachineBase;
import crazypants.enderio.base.machine.gui.PowerBar;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPainter extends GuiInventoryMachineBase<TileEntityPainter> {

  public GuiPainter(@Nonnull InventoryPlayer par1InventoryPlayer, @Nonnull TileEntityPainter te) {
    super(te, new ContainerPainter(par1InventoryPlayer, te), "painter");

    addProgressTooltip(88, 34, 24, 16);

    addDrawingElement(new PowerBar(te, this));
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    bindGuiTexture();
    int k = guiLeft;
    int l = guiTop;

    drawTexturedModalRect(k, l, 0, 0, xSize, ySize);

    if (shouldRenderProgress()) {
      int scaled = getProgressScaled(24);
      drawTexturedModalRect(k + 88, l + 34, 176, 14, scaled + 1, 16);
    }

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

  @Override
  protected void renderSlotHighlight(int slot, @Nonnull Vector4f col) {
    if (getTileEntity().getSlotDefinition().isOutputSlot(slot)) {
      renderSlotHighlight(col, 117, 31, 24, 24);
    } else if (slot != 1) {
      super.renderSlotHighlight(slot, col);
    }
  }

}
