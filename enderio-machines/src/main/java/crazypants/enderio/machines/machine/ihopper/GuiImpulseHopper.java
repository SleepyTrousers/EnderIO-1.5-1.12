package crazypants.enderio.machines.machine.ihopper;

import javax.annotation.Nonnull;

import crazypants.enderio.base.gui.GuiContainerBaseEIO;
import crazypants.enderio.base.machine.gui.CapPowerBar;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiImpulseHopper extends GuiContainerBaseEIO {

  private static final int POWERX = 15;
  private static final int POWERY = 9;
  private static final int POWER_HEIGHT = 47;

  static final int D = 18;
  static final int ROW1 = 9;
  static final int ROW2 = ROW1 + D + D / 2;
  static final int ROW3 = ROW2 + D + D / 2;
  static final int COL = 44;

  private TileImpulseHopper te;

  public GuiImpulseHopper(@Nonnull InventoryPlayer playerInv, @Nonnull TileImpulseHopper te) {
    super(new ContainerImpulseHopper(playerInv, te), "impulse_hopper");
    this.te = te;

    xSize = 176;
    ySize = 166;

    addDrawingElement(new CapPowerBar(te.getEnergy(), this, POWERX, POWERY, POWER_HEIGHT));
  }

  @Override
  public void initGui() {
    super.initGui();
    ((ContainerImpulseHopper) inventorySlots).createGhostSlots(getGhostSlotHandler().getGhostSlots());
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {

    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    bindGuiTexture();
    drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

    for (int slot = 0; slot < TileImpulseHopper.SLOTS; slot++) {
      if (te.checkGhostSlot(slot)) {
        if (te.checkInputSlot(slot)) {
          drawTexturedModalRect(guiLeft + COL + slot * D - 1, guiTop + ROW1 + D - 1, 200, D / 2, D, D / 2);
        } else {
          drawTexturedModalRect(guiLeft + COL + slot * D - 1, guiTop + ROW1 + D - 1, 200, 0, D, D / 2);
        }
        if (te.checkOutputSlot(slot)) {
          drawTexturedModalRect(guiLeft + COL + slot * D - 1, guiTop + ROW2 + D - 1, 200, D / 2, D, D / 2);
        } else {
          drawTexturedModalRect(guiLeft + COL + slot * D - 1, guiTop + ROW2 + D - 1, 200, 0, D, D / 2);
        }
      }
    }

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

}
