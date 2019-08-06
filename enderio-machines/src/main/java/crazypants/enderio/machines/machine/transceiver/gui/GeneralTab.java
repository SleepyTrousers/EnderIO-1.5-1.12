package crazypants.enderio.machines.machine.transceiver.gui;

import java.awt.Color;
import java.awt.Point;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.ITabPanel;
import com.enderio.core.client.gui.button.ToggleButton;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.capacitor.ICapacitorData;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.capacitor.DefaultCapacitorData;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.lang.LangPower;
import crazypants.enderio.base.machine.gui.IPowerBarData;
import crazypants.enderio.base.machine.gui.PowerBar;
import crazypants.enderio.base.machine.gui.PowerBar.Op;
import crazypants.enderio.base.machine.gui.PowerBar.What;
import crazypants.enderio.machines.lang.Lang;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GeneralTab implements ITabPanel {

  private static final int SEND_BAR_OFFSET = 12;
  final @Nonnull ContainerTransceiver container;
  final @Nonnull GuiTransceiver parent;

  final @Nonnull ToggleButton bufferSizeB;
  final @Nonnull PowerBar internalPowerBar, sendPowerBar;

  public GeneralTab(@Nonnull GuiTransceiver guiTransceiver) {
    parent = guiTransceiver;
    container = parent.getContainer();

    int x = parent.getXSize() - 5 - 16;
    int y = 43;
    bufferSizeB = new ToggleButton(parent, 4327, x, y, IconEIO.ITEM_SINGLE, IconEIO.ITEM_STACK);
    bufferSizeB.setSelectedToolTip(Lang.GUI_TRANS_BUFFER_STACKS.get());
    bufferSizeB.setUnselectedToolTip(Lang.GUI_TRANS_BUFFER_SINGLES.get());
    bufferSizeB.setSelected(parent.getTransciever().isBufferStacks());

    internalPowerBar = new PowerBar(new PowerBarDataMain(), parent, 11, 14, 58);
    final PowerBarDataSend sendTank = new PowerBarDataSend();
    sendPowerBar = new PowerBar(sendTank, parent, 11 + SEND_BAR_OFFSET, 14, 58);
    sendPowerBar.addTooltips(Op.REPLACE, What.ALL,
        () -> new NNList<String>(Lang.GUI_TRANS_BUFFER_SHARED.get(),
            Lang.GUI_TRANS_BUFFER_MAXIO.get(LangPower.RFt(parent.getTransciever().getMaxEnergyRecieved(null))),
            LangPower.RF(sendTank.getEnergyStored(), sendTank.getMaxEnergyStored())));
  }

  private class PowerBarDataMain implements IPowerBarData {

    @Override
    public int getMaxEnergyStored() {
      return (int) (container.getTe().getMaxEnergyStored() * container.getTe().getInternalBufferRatio());
    }

    @Override
    @Nonnull
    public ICapacitorData getCapacitorData() {
      return container.getTe().getCapacitorData();
    }

    @Override
    public int getEnergyStored() {
      return Math.min(getMaxEnergyStored(), container.getTe().getEnergyStored());
    }

    @Override
    public int getMaxUsage() {
      return container.getTe().getMaxUsage();
    }

  }

  private class PowerBarDataSend implements IPowerBarData {

    @Override
    public int getMaxEnergyStored() {
      return (int) (container.getTe().getMaxEnergyStored() * container.getTe().getSendBufferRatio());
    }

    @Override
    @Nonnull
    public ICapacitorData getCapacitorData() {
      return DefaultCapacitorData.BASIC_CAPACITOR;
    }

    @Override
    public int getEnergyStored() {
      return Math.max(0, container.getTe().getEnergyStored() - (container.getTe().getMaxEnergyStored() - getMaxEnergyStored()));
    }

    @Override
    public int getMaxUsage() {
      return container.getTe().getMaxUsage();
    }

  }

  @Override
  public void onGuiInit(int x, int y, int width, int height) {
    container.setPlayerInventoryVisible(true);
    container.setBufferSlotsVisible(true);
    parent.addDrawingElement(internalPowerBar);
    parent.addDrawingElement(sendPowerBar);
    bufferSizeB.onGuiInit();
  }

  @Override
  public void deactivate() {
    container.setPlayerInventoryVisible(false);
    container.setBufferSlotsVisible(false);
    parent.removeDrawingElement(internalPowerBar);
    parent.removeDrawingElement(sendPowerBar);
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

    Point invRoot = container.getItemInventoryOffset();

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

  @Override
  @Nonnull
  public ResourceLocation getTexture() {
    return EnderIO.proxy.getGuiTexture("transceiver_general");
  }
}
