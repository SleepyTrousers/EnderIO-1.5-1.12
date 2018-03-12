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

  public GuiImpulseHopper(@Nonnull InventoryPlayer playerInv, @Nonnull TileImpulseHopper te) {
    super(new ContainerImpulseHopper(playerInv, te), "impulse_hopper");

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

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

}
