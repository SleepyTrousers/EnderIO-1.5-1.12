package crazypants.enderio.invpanel.sensor;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.client.gui.widget.TextFieldEnder;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.integration.jei.IHaveGhostTargets;
import crazypants.enderio.base.machine.gui.GuiMachineBase;
import crazypants.enderio.base.machine.gui.PowerBar;
import crazypants.enderio.invpanel.network.PacketHandler;
import crazypants.enderio.invpanel.network.sensor.PacketItemCount;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class GuiSensor extends GuiMachineBase<TileInventoryPanelSensor> implements IHaveGhostTargets<GuiSensor>{

  private TextFieldEnder startTF;
  private TextFieldEnder stopTF;

  public GuiSensor(@Nonnull InventoryPlayer par1InventoryPlayer, @Nonnull TileInventoryPanelSensor te) {
    super(te, new ContainerSensor(par1InventoryPlayer, te), "inv_panel_sensor");

    recipeButton.setYOrigin(recipeButton.getBounds().y + 19);
    redstoneButton.setIsVisible(false);
    configB.setYOrigin(5);

    int tfWidth = 42;
    int tfHeight = 14;
    int tfX = xSize - tfWidth - 6 - 20;
    int tfY = 34;
    startTF = new TextFieldEnder(getFontRenderer(), tfX, tfY, tfWidth, tfHeight);
    startTF.setCanLoseFocus(true);
    startTF.setMaxStringLength(6);
    startTF.setText(te.getStartCount() + "");
    startTF.setCharFilter(TextFieldEnder.FILTER_NUMERIC);
    textFields.add(startTF);

    stopTF = new TextFieldEnder(getFontRenderer(), tfX, tfY + tfHeight + 4, tfWidth, tfHeight);
    stopTF.setCanLoseFocus(true);
    stopTF.setMaxStringLength(6);
    stopTF.setText(te.getStopCount() + "");
    stopTF.setCharFilter(TextFieldEnder.FILTER_NUMERIC);
    textFields.add(stopTF);

    addDrawingElement(new PowerBar(te, this, 15, 14,57));
  }

  @Override
  public void initGui() {
    super.initGui();
    ((ContainerSensor) inventorySlots).addGhostSlots(getGhostSlotHandler());
  }

  @Override
  protected void mouseClickMove(int mouseX, int mouseY, int button, long par4) {
    if (!getGhostSlotHandler().getGhostSlots().isEmpty()) {
      GhostSlot slot = getGhostSlotHandler().getGhostSlotAt(this, mouseX, mouseY);
      if (slot != null) {
        ItemStack st = Minecraft.getMinecraft().player.inventory.getItemStack();
        // don't replace already set slots while dragging an item
        if (st.isEmpty() || slot.getStack().isEmpty()) {
          slot.putStack(st, 0);
        }
      }
    }
    super.mouseClickMove(mouseX, mouseY, button, par4);
  }

  //  @Override
  //  protected int getPowerBarHeight() {
  //    return 57;
  //  }

  @Override
  protected boolean showRecipeButton() {
    return false;
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    bindGuiTexture();
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;
    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);
    super.drawGuiContainerBackgroundLayer(par1, par2, par3);

    String txt = EnderIO.lang.localize("gui.inventorySensor.control1").trim();
    FontRenderer fr = getFontRenderer();
    fr.drawString(txt, startTF.x - 3 - fr.getStringWidth(txt), startTF.y + 3, 0x000000);
    txt = EnderIO.lang.localize("gui.inventorySensor.control2").trim();
    fr.drawString(txt, stopTF.x - 3 - fr.getStringWidth(txt), stopTF.y + 3, 0x000000);

    checkForTextChange();
  }

  private void checkForTextChange() {
    boolean dirty = false;
    TileInventoryPanelSensor te = getTileEntity();
    Integer val = startTF.getInteger();
    if (val != null && val.intValue() > 0 && val.intValue() != te.getStartCount()) {
      te.setStartCount(val);
      dirty = true;
    }
    val = stopTF.getInteger();
    if (val != null && val.intValue() > 0 && val.intValue() != te.getStopCount()) {
      te.setStopCount(val);
      dirty = true;
    }
    if (dirty) {
      PacketHandler.INSTANCE.sendToServer(new PacketItemCount(te));
    }
  }

}
