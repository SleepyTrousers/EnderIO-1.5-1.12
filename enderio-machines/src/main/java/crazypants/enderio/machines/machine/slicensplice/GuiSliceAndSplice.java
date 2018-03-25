package crazypants.enderio.machines.machine.slicensplice;

import javax.annotation.Nonnull;

import crazypants.enderio.base.machine.gui.GuiInventoryMachineBase;
import crazypants.enderio.base.machine.gui.PowerBar;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiSliceAndSplice extends GuiInventoryMachineBase<TileSliceAndSplice> {

  public GuiSliceAndSplice(@Nonnull InventoryPlayer par1InventoryPlayer, @Nonnull TileSliceAndSplice te) {
    super(te, new ContainerSliceAndSplice(par1InventoryPlayer, te), "slice_and_splice");

    addProgressTooltip(103, 49, 24, 16);

    addDrawingElement(new PowerBar(te, this));
  }

  @Override
  public void initGui() {
    super.initGui();
    ((ContainerSliceAndSplice) inventorySlots).createGhostSlots(getGhostSlotHandler().getGhostSlots());
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
      drawTexturedModalRect(k + 103, l + 49, 176, 14, scaled + 1, 16);
    }

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

}
