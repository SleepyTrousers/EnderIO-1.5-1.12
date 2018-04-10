package crazypants.enderio.machines.machine.buffer;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.widget.TextFieldEnder;
import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.base.lang.LangPower;
import crazypants.enderio.base.machine.gui.GuiInventoryMachineBase;
import crazypants.enderio.base.machine.gui.PowerBar;
import crazypants.enderio.base.machine.modes.IoMode;
import crazypants.enderio.base.power.PowerDisplayUtil;
import crazypants.enderio.machines.EnderIOMachines;
import crazypants.enderio.machines.lang.Lang;
import crazypants.enderio.machines.network.PacketHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

public class GuiBuffer extends GuiInventoryMachineBase<TileBuffer> {

  private static final @Nonnull String TEXTURE_FULL = "buffer_full";

  private TextFieldEnder maxInput;
  private TextFieldEnder maxOutput;

  private int lastInput, lastOutput, lastInputTe, lastOutputTe;

  private final boolean hasInventory, hasPower;

  public GuiBuffer(@Nonnull InventoryPlayer par1InventoryPlayer, @Nonnull TileBuffer te) {
    super(te, new ContainerBuffer(par1InventoryPlayer, te), TEXTURE_FULL);
    hasInventory = te.hasInventory();
    hasPower = te.hasPower();

    redstoneButton.setPosition(153, 14);
    configB.setPosition(153, 32);

    if (hasPower) {
      int x = (isFull() ? 32 : 58);
      int y = guiTop + 28;

      maxInput = new TextFieldEnder(getFontRenderer(), x, y, 60, 12);
      y += 29;
      maxOutput = new TextFieldEnder(getFontRenderer(), x, y, 60, 12);

      textFields.add(maxInput);
      textFields.add(maxOutput);
      addDrawingElement(new PowerBar(te, this, 16, 15, 40) {
        @Override
        protected String getPowerOutputLabel(@Nonnull String rft) {
          return Lang.GUI_BUFFER_MAXIO.get(LangPower.RFt(getTileEntity().getMaxIO()));
        }
      });
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
    boolean changed = false;
    int input = PowerDisplayUtil.parsePower(maxInput);
    if (input != lastInput) {
      lastInput = input;
      changed = true;
    }
    int output = PowerDisplayUtil.parsePower(maxOutput);
    if (output != lastOutput) {
      lastOutput = output;
      changed = true;
    }
    if (changed) {
      sendUpdateToServer(); // also sets local te
      maxInput.setText(LangPower.format(getTileEntity().getMaxInput()));
      maxOutput.setText(LangPower.format(getTileEntity().getMaxOutput()));
    }
  }

  private void checkForTeChanges() {
    if (hasPower) {
      if (getTileEntity().getMaxInput() != lastInputTe) {
        maxInput.setText(LangPower.format(lastInputTe = getTileEntity().getMaxInput()));
      }
      if (getTileEntity().getMaxOutput() != lastOutputTe) {
        maxOutput.setText(LangPower.format(lastOutputTe = getTileEntity().getMaxOutput()));
      }
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
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    checkForTeChanges();

    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

    bindGuiTexture();
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);

    bindGuiTexture();

    if (hasPower) {
      drawPowerBg(sx, sy + 2); // Dirty hack for capacitor slot! I'm sorry, not.
    }

    if (hasInventory) {
      drawSlotBg(sx, sy);
    }

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);

    String invName = EnderIOMachines.lang.localizeExact(getTileEntity().getMachineName() + ".name");
    getFontRenderer().drawStringWithShadow(invName, sx + (xSize / 2) - (getFontRenderer().getStringWidth(invName) / 2), sy + 4, 0xFFFFFF);

    if (hasPower) {
      sx += isFull() ? 32 : 58;
      sy += 17;

      getFontRenderer().drawStringWithShadow(Lang.GUI_BUFFER_IN.get(), sx, sy, 0xFFFFFF);
      getFontRenderer().drawStringWithShadow(Lang.GUI_BUFFER_OUT.get(), sx, sy + 29, 0xFFFFFF);
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
    drawTexturedModalRect(sx + 14, sy + 12, xSize + 10, 0, 12, 44);
    if (!getTileEntity().isCreative()) {
      drawTexturedModalRect(sx + 11, sy + 59, xSize, 54, 19, 19);
    }
  }

  private void drawSlotBg(int sx, int sy) {
    drawTexturedModalRect(sx + (isFull() ? 95 : 61), sy + 14, xSize + 22, 0, 54, 54);
  }
}
