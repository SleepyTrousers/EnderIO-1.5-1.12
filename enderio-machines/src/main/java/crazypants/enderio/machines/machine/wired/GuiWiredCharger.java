package crazypants.enderio.machines.machine.wired;

import javax.annotation.Nonnull;

import crazypants.enderio.base.machine.gui.GuiInventoryMachineBase;
import crazypants.enderio.base.machine.gui.PowerBar;
import crazypants.enderio.machines.EnderIOMachines;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiWiredCharger extends GuiInventoryMachineBase<TileWiredCharger> {

  public GuiWiredCharger(@Nonnull InventoryPlayer par1InventoryPlayer, @Nonnull TileWiredCharger te) {
    super(te, new ContainerWiredCharger(par1InventoryPlayer, te), "wired_charger");

    addDrawingElement(new PowerBar<TileWiredCharger>(te, this, 15, 14, 42));
  }

  @Override
  public void initGui() {
    super.initGui();
    ((ContainerWiredCharger) inventorySlots).addGhostslots(getGhostSlotHandler().getGhostSlots());
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

    bindGuiTexture();
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);

    if (shouldRenderProgress()) {
      int scaled = getProgressScaled(37);
      drawTexturedModalRect(sx + 81, sy + 17, 176, 0, 14, 1 + scaled);
    }

    String invName = EnderIOMachines.lang.localizeExact(getTileEntity().getMachineName() + ".name");
    getFontRenderer().drawStringWithShadow(invName, sx + (xSize / 2) - (getFontRenderer().getStringWidth(invName) / 2), sy + 4, 0xFFFFFF);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

}
