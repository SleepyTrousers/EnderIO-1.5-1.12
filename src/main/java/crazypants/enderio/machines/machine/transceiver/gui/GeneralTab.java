package crazypants.enderio.machines.machine.transceiver.gui;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.ITabPanel;
import com.enderio.core.client.gui.button.ToggleButton;
import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.render.ColorUtil;

import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.lang.LangPower;
import crazypants.enderio.machines.lang.Lang;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class GeneralTab implements ITabPanel {

  private static final int SEND_BAR_OFFSET = 13;
  final @Nonnull ContainerTransceiver container;
  final @Nonnull GuiTransceiver parent;
  final @Nonnull GuiToolTip sendPowerBarTT;

  final @Nonnull ToggleButton bufferSizeB;

  public GeneralTab(@Nonnull GuiTransceiver guiTransceiver) {
    parent = guiTransceiver;
    container = parent.getContainer();

    int x = parent.getXSize() - 5 - 16;
    int y = 43;
    bufferSizeB = new ToggleButton(parent, 4327, x, y, IconEIO.ITEM_SINGLE, IconEIO.ITEM_STACK);
    bufferSizeB.setSelectedToolTip(Lang.GUI_TRANS_BUFFER_STACKS.get());
    bufferSizeB.setUnselectedToolTip(Lang.GUI_TRANS_BUFFER_SINGLES.get());
    bufferSizeB.setSelected(parent.getTransciever().isBufferStacks());

    sendPowerBarTT = new GuiToolTip(new Rectangle(11 + SEND_BAR_OFFSET, 14, 10, 58), "") {
      @Override
      protected void updateText() {
        text.clear();
        if (parent.renderPowerBar()) {
          updateSendPowerBarTooltip(text);
        }
      }
    };
    parent.addToolTip(sendPowerBarTT);
  }

  @Override
  public void onGuiInit(int x, int y, int width, int height) {
    container.setPlayerInventoryVisible(true);
    container.setBufferSlotsVisible(true);
    bufferSizeB.onGuiInit();
  }

  @Override
  public void deactivate() {
    container.setPlayerInventoryVisible(false);
    container.setBufferSlotsVisible(false);
    bufferSizeB.detach();
  }

  @Override
  public @Nonnull IconEIO getIcon() {
    return IconEIO.IO_CONFIG_UP;
  }

  @Override
  public void render(float par1, int par2, int par3) {
    int top = parent.getGuiTop();
    int left = parent.getGuiLeft();

    GlStateManager.color(1, 1, 1);

    // Inventory
    parent.bindGuiTexture();
    Point invRoot = container.getPlayerInventoryOffset();
    parent.drawTexturedModalRect(left + invRoot.x - 1, top + invRoot.y - 1, 24, 180, 162, 76);

    invRoot = container.getItemInventoryOffset();
    parent.drawTexturedModalRect(left + invRoot.x - 1, top + invRoot.y - 1, 24, 180, 72, 36);
    parent.drawTexturedModalRect(left + invRoot.x - 1 + (18 * 4) + container.getItemBufferSpacing(), top + invRoot.y - 1, 24, 180, 72, 36);

    FontRenderer fr = parent.getFontRenderer();
    String sendTxt = Lang.GUI_TRANS_CHANNEL_SEND.get();
    int x = left + invRoot.x + 36 - fr.getStringWidth(sendTxt) / 2;
    int y = top + invRoot.y - fr.FONT_HEIGHT - 3;
    fr.drawStringWithShadow(sendTxt, x, y, ColorUtil.getRGB(Color.WHITE));
    String recText = Lang.GUI_TRANS_CHANNEL_RECEIVE.get();
    x = left + invRoot.x + 72 + container.getItemBufferSpacing() + 36 - fr.getStringWidth(recText) / 2;
    fr.drawStringWithShadow(recText, x, y, ColorUtil.getRGB(Color.WHITE));

    // Highlights
    parent.renderSlotHighlights();

    // Power
    parent.bindGuiTexture();
    GlStateManager.color(1, 1, 1);

    x = left + 11 - 1;
    y = top + 14 - 1;
    int maxHeight = 58;

    parent.drawTexturedModalRect(x, y, 233, 196, 12, maxHeight + 2);
    parent.drawTexturedModalRect(x + SEND_BAR_OFFSET, y, 233, 196, 12, maxHeight + 2);

    int totalPixelHeight = parent.getTransciever().getEnergyStoredScaled(maxHeight * 2);
    int fillHeight = Math.min(totalPixelHeight, maxHeight);

    int fillY = y + 1 + 58 - fillHeight;
    x += 1;
    parent.drawTexturedModalRect(x, fillY, 246, 196, 10, fillHeight);

    fillHeight = Math.max(0, totalPixelHeight - maxHeight);
    fillY = y + 1 + 58 - fillHeight;
    parent.drawTexturedModalRect(x + SEND_BAR_OFFSET, fillY, 246 - 25, 196, 10, fillHeight);

  }

  public void updatePowerBarTooltip(List<String> text) {
    text.add(Lang.GUI_TRANS_BUFFER_LOCAL.get());
    text.add(Lang.GUI_TRANS_BUFFER_UPKEEP.get(LangPower.RFt(parent.getTransciever().getPowerUsePerTick())));
    int maxEnergy = parent.getTransciever().getMaxEnergyStored() / 2;
    int energyStored = Math.min(parent.getTransciever().getEnergyStored(), maxEnergy);
    text.add(LangPower.RF(energyStored, maxEnergy));
  }

  private void updateSendPowerBarTooltip(List<String> text) {
    text.add(Lang.GUI_TRANS_BUFFER_SHARED.get());
    text.add(Lang.GUI_TRANS_BUFFER_MAXIO.get(LangPower.RFt(parent.getTransciever().getMaxEnergyRecieved(null))));
    int maxEnergy = parent.getTransciever().getMaxEnergyStored() / 2;
    int energyStored = Math.max(0, parent.getTransciever().getEnergyStored() - maxEnergy);
    text.add(LangPower.RF(energyStored, maxEnergy));
  }

  @Override
  public void actionPerformed(@Nonnull GuiButton guiButton) {
    if (guiButton == bufferSizeB) {
      parent.getTransciever().setBufferStacks(bufferSizeB.isSelected());
      parent.doSetBufferStacks(bufferSizeB.isSelected());
    }
  }

  @Override
  public void mouseClicked(int x, int y, int par3) {
  }

  @Override
  public void updateScreen() {
  }

  @Override
  public void keyTyped(char par1, int par2) {
    parent.doDefaultKeyTyped(par1, par2);
  }

}
