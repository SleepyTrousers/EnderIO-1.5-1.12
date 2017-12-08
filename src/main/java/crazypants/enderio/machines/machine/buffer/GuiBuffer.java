package crazypants.enderio.machines.machine.buffer;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.widget.TextFieldEnder;
import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.base.lang.LangPower;
import crazypants.enderio.base.machine.gui.GuiPoweredMachineBase;
import crazypants.enderio.base.machine.modes.IoMode;
import crazypants.enderio.base.power.PowerDisplayUtil;
import crazypants.enderio.machines.EnderIOMachines;
import crazypants.enderio.machines.lang.Lang;
import crazypants.enderio.machines.network.PacketHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

public class GuiBuffer extends GuiPoweredMachineBase<TileBuffer> {

  private static final @Nonnull String TEXTURE_SIMPLE = "buffer";
  private static final @Nonnull String TEXTURE_FULL = "buffer_full";

  private TextFieldEnder maxInput;
  private TextFieldEnder maxOutput;

  private int lastInput, lastOutput;

  private final boolean hasInventory, hasPower;

  public GuiBuffer(@Nonnull InventoryPlayer par1InventoryPlayer, @Nonnull TileBuffer te) {
    super(te, new ContainerBuffer(par1InventoryPlayer, te), TEXTURE_SIMPLE, TEXTURE_FULL);
    hasInventory = te.hasInventory();
    hasPower = te.hasPower();

    redstoneButton.setPosition(isFull() ? 153 : 120, 14);
    configB.setPosition(isFull() ? 153 : 120, 32);

    if (hasPower) {
      int x = (isFull() ? 20 : 58);
      int y = guiTop + 27;

      maxInput = new TextFieldEnder(getFontRenderer(), x, y, 60, 12);
      y += 28;
      maxOutput = new TextFieldEnder(getFontRenderer(), x, y, 60, 12);

      textFields.add(maxInput);
      textFields.add(maxOutput);
    }
  }

  @Override
  public void initGui() {
    super.initGui();

    if (hasPower) {
      maxInput.setMaxStringLength(10);
      maxInput.setText(LangPower.format(getTileEntity().getMaxInput()));
      maxOutput.setMaxStringLength(10);
      maxOutput.setText(LangPower.format(getTileEntity().getMaxOutput()));
    }
  }

  @Override
  protected void keyTyped(char par1, int par2) throws IOException {
    super.keyTyped(par1, par2);
    if (par1 == 'e') {
      super.keyTyped(par1, 1);
    }

    if (hasPower) {
      updateInputOutput();
    }
  }

  private void updateInputOutput() {
    int input = PowerDisplayUtil.parsePower(maxInput);
    setMaxInput(input);
    int output = PowerDisplayUtil.parsePower(maxOutput);
    setMaxOutput(output);
    sendUpdateToServer();
  }

  private void setMaxOutput(int output) {
    if (output != lastOutput) {
      lastOutput = output;
      maxOutput.setText(LangPower.format(output));
    }
  }

  private void setMaxInput(int input) {
    if (input != lastInput) {
      lastInput = input;
      maxInput.setText(LangPower.format(input));
      sendUpdateToServer();
    }
  }

  protected void sendUpdateToServer() {
    PacketHandler.sendToServer(new PacketBufferIO(getTileEntity(), lastInput, lastOutput));
  }

  @Override
  protected boolean showRecipeButton() {
    return false;
  }

  @Override
  protected boolean renderPowerBar() {
    return hasPower;
  }

  @Override
  public int getYSize() {
    return ySize;
  }

  @Override
  protected int getPowerHeight() {
    return 52;
  }

  @Override
  protected int getPowerX() {
    return isFull() ? 6 : 44;
  }

  @Override
  protected int getPowerY() {
    return 15;
  }

  @Override
  protected int getPowerV() {
    return 0;
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {

    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

    bindGuiTexture(isFull() ? 1 : 0);
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);

    bindGuiTexture();

    if (hasPower) {
      drawPowerBg(sx, sy);
    }

    if (hasInventory) {
      drawSlotBg(sx, sy);
    }

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);

    String invName = EnderIOMachines.lang.localizeExact(getTileEntity().getMachineName() + ".name");
    getFontRenderer().drawStringWithShadow(invName, sx + (xSize / 2) - (getFontRenderer().getStringWidth(invName) / 2), sy + 4, 0xFFFFFF);

    if (hasPower) {
      sx += isFull() ? 19 : 57;
      sy += 17;

      getFontRenderer().drawStringWithShadow(Lang.GUI_BUFFER_IN.get(), sx, sy, 0xFFFFFF);
      getFontRenderer().drawStringWithShadow(Lang.GUI_BUFFER_OUT.get(), sx, sy + 27, 0xFFFFFF);
    }
  }

  boolean isFull() {
    return hasInventory && hasPower;
  }

  @Override
  public void renderSlotHighlights(@Nonnull IoMode mode) {
    if (!hasInventory) {
      return;
    }

    for (int slot = 0; slot < getTileEntity().getSizeInventory(); slot++) {
      renderSlotHighlight(slot, mode);
    }
  }

  protected void renderSlotHighlight(int slot, IoMode mode) {
    Slot invSlot = inventorySlots.inventorySlots.get(slot);
    if (mode == IoMode.PULL) {
      renderSlotHighlight(slot, PULL_COLOR);
    } else if (mode == IoMode.PUSH) {
      renderSlotHighlight(slot, PUSH_COLOR);
    } else if (mode == IoMode.PUSH_PULL) {
      renderSplitHighlight(invSlot.xPos, invSlot.yPos, 16, 16);
    }
  }

  protected void renderSplitHighlight(int x, int y, int widthIn, int heightIn) {
    GL11.glEnable(GL11.GL_BLEND);
    RenderUtil.renderQuad2D(getGuiLeft() + x, getGuiTop() + y, 0, widthIn, heightIn / 2, PULL_COLOR);
    RenderUtil.renderQuad2D(getGuiLeft() + x, getGuiTop() + y + (heightIn / 2), 0, widthIn, heightIn / 2, PUSH_COLOR);
    GL11.glDisable(GL11.GL_BLEND);
  }

  private void drawPowerBg(int sx, int sy) {
    drawTexturedModalRect(sx + (isFull() ? 5 : 43), sy + 14, xSize + 10, 0, 12, 54);
  }

  private void drawSlotBg(int sx, int sy) {
    drawTexturedModalRect(sx + (isFull() ? 95 : 61), sy + 14, xSize + 22, 0, 54, 54);
  }
}
