package crazypants.enderio.machines.machine.wired;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.base.machine.gui.GuiInventoryMachineBase;
import crazypants.enderio.base.machine.gui.PowerBar;
import crazypants.enderio.machines.EnderIOMachines;
import crazypants.enderio.util.Prep;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiWiredCharger extends GuiInventoryMachineBase<TileWiredCharger> {

  public static final @Nonnull ResourceLocation baublesBackground = new ResourceLocation("baubles", "textures/gui/expanded_inventory.png");

  private final boolean isSimple;

  public GuiWiredCharger(@Nonnull InventoryPlayer playerInv, @Nonnull TileWiredCharger te, int baubleSlots) {
    super(te, ContainerWiredCharger.create(playerInv, te, baubleSlots), "wired_charger", "wired_charger_baubles", "wired_charger_simple",
        "wired_charger_simple_baubles");

    isSimple = te instanceof TileWiredCharger.Simple;

    xSize = 218;

    addDrawingElement(new PowerBar(te, this, 37, 14, 42));
  }

  @Override
  public void initGui() {
    super.initGui();
    ((ContainerWiredCharger) inventorySlots).addGhostslots(getGhostSlotHandler().getGhostSlots());
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

    ContainerWiredCharger container = (ContainerWiredCharger) inventorySlots;

    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    if (container.hasBaublesSlots()) {
      bindGuiTexture(1 + (isSimple ? 2 : 0));
    } else {
      bindGuiTexture(isSimple ? 2 : 0);
    }

    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);

    if (container.hasBaublesSlots()) {
      drawTexturedModalRect(sx + 194, sy + 6, 221, 78, 24, 39);
      for (int i = 1; i < container.baubles.getSizeInventory(); i++) {
        drawTexturedModalRect(sx + 194, sy + 11 + i * 18, 221, 137, 24, 23);
      }
      RenderUtil.bindTexture(baublesBackground);
      for (int i = 0; i < container.baubles.getSizeInventory(); i++) {
        if (Prep.isInvalid(container.baubles.getStackInSlot(i))) {
          final int textureX = 77 + (i / 4) * 19;
          final int textureY = 8 + (i % 4) * 18;
          drawTexturedModalRect(sx + 196, sy + 12 + i * 18, textureX, textureY, 16, 16);
        }
      }
    }

    if (shouldRenderProgress()) {
      int scaled = getProgressScaled(37);
      drawTexturedModalRect(sx + 103, sy + 17 + 37 - scaled, 242, 0 + 37 - scaled, 14, 38);
    }

    String invName = EnderIOMachines.lang.localizeExact(getTileEntity().getMachineName() + ".name");
    getFontRenderer().drawStringWithShadow(invName, sx + (xSize / 2) - (getFontRenderer().getStringWidth(invName) / 2), sy + 4, 0xFFFFFF);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

  // Overridden to set the size of the io config overlay
  @Override
  public int getOverlayOffsetXRight() {
    return 42;
  }

  @Override
  public int getOverlayOffsetXLeft() {
    return super.getOverlayOffsetXLeft() + 21;
  }

  @Override
  protected int getButtonXPos() {
    return 218 - 23;
  }

}
