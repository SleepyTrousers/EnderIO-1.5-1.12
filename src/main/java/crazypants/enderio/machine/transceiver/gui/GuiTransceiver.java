package crazypants.enderio.machine.transceiver.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.enderio.core.api.client.gui.IGuiOverlay;
import com.enderio.core.api.client.gui.ITabPanel;

import crazypants.enderio.machine.gui.GuiPoweredMachineBase;
import crazypants.enderio.machine.transceiver.ChannelType;
import crazypants.enderio.machine.transceiver.TileTransceiver;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiTransceiver extends GuiPoweredMachineBase<TileTransceiver> {

  private int activeTab = 0;
  private final List<ITabPanel> tabs = new ArrayList<ITabPanel>();
  GeneralTab generalTab;

  public GuiTransceiver(InventoryPlayer par1InventoryPlayer, TileTransceiver te) {
    super(te, new ContainerTransceiver(par1InventoryPlayer, te), "transceiver", "itemFilter");

    generalTab = new GeneralTab(this);
    tabs.add(generalTab);
    FilterTab filterTab = new FilterTab(this);
    tabs.add(filterTab);
    tabs.add(new ChannelTab(this, ChannelType.POWER));
    tabs.add(new ChannelTab(this, ChannelType.ITEM));
    tabs.add(new ChannelTab(this, ChannelType.FLUID));
  }

  @Override
  protected void updatePowerBarTooltip(List<String> text) {
    generalTab.updatePowerBarTooltip(text);
  }

  @Override
  protected boolean showRecipeButton() {
    return false;
  }

  @Override
  public int getXSize() {
    return ContainerTransceiver.GUI_WIDTH;
  }

  @Override
  public void updateScreen() {
    for (int i = 0; i < tabs.size(); i++) {
      if (i == activeTab) {
        tabs.get(i).updateScreen();
        return;
      }
    }
  }

  @Override
  protected void keyTyped(char par1, int par2) {
    if (par2 == 1) {
      for (IGuiOverlay overlay : overlays) {
        if (overlay.isVisible()) {
          overlay.setIsVisible(false);
          return;
        }
      }
      mc.thePlayer.closeScreen();
    }

    for (int i = 0; i < tabs.size(); i++) {
      if (i == activeTab) {
        tabs.get(i).keyTyped(par1, par2);
        return;
      }
    }
  }
  
  void doDefaultKeyTyped(char par1, int par2) {
    try {
      super.keyTyped(par1, par2);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void initGui() {
    super.initGui();
    for (int i = 0; i < tabs.size(); i++) {
      if (i != activeTab) {
        tabs.get(i).deactivate();
      }
    }
    ghostSlots.clear();
    for (int i = 0; i < tabs.size(); i++) {
      if (i == activeTab) {
        tabs.get(i).onGuiInit(guiLeft + 10, guiTop, xSize - 20, ySize - 20);
      }
    }
    configB.visible = activeTab == 0;
    redstoneButton.visible = activeTab == 0;
  }

  @Override
  public void renderPowerBar(int k, int l) {
    // super.renderPowerBar(k, l);
  }

  @Override
  protected boolean renderPowerBar() {
    return activeTab == 0;
  }

  @Override
  public int getPowerX() {
    return super.getPowerX() - 4;
  }

  @Override
  public int getPowerHeight() {
    return 58;
  }

  @Override
  public int getPowerY() {
    return super.getPowerY();
  }

  @Override
  public int getPowerWidth() {
    return POWER_WIDTH;
  }

  @Override
  public int getPowerV() {
    return 196;
  }

  @Override
  public int getPowerU() {
    return 246;
  }

  @Override
  public String getPowerOutputLabel() {
    return super.getPowerOutputLabel();
  }

  @Override
  public int getPowerOutputValue() {
    return super.getPowerOutputValue();
  }

  @Override
  protected void mouseClicked(int x, int y, int par3) throws IOException {
    super.mouseClicked(x, y, par3);

    int tabFromCoords = getTabFromCoords(x, y);
    if (tabFromCoords >= 0) {
      activeTab = tabFromCoords;
      hideOverlays();
      initGui();
      return;
    }

    x = (x - guiLeft);
    y = (y - guiTop);

    tabs.get(activeTab).mouseClicked(x, y, par3);
  }

  @Override
  protected void actionPerformed(GuiButton guiButton) throws IOException {
    super.actionPerformed(guiButton);
    tabs.get(activeTab).actionPerformed(guiButton);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    bindGuiTexture();
    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);

    startTabs();
    for (int i = 0; i < tabs.size(); i++) {
      renderStdTab(sx, sy, i, tabs.get(i).getIcon(), i == activeTab);
    }

    tabs.get(activeTab).render(par1, par2, par3);

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

  public TileTransceiver getTransciever() {
    return getTileEntity();
  }

  public ContainerTransceiver getContainer() {
    return (ContainerTransceiver) inventorySlots;
  }

  // @Override
  // @Optional.Method(modid = "NotEnoughItems")
  // public boolean hideItemPanelSlot(GuiContainer gc, int x, int y, int w, int h) {
  // if(tabs.size() > 0) {
  // int sx = (width - xSize) / 2;
  // int sy = (height - ySize) / 2;
  // int tabX = sx + xSize - 3;
  // int tabY = sy + tabYOffset;
  //
  // return (x+w) >= tabX && x < (tabX + 14) && (y+h) >= tabY && y < (tabY + tabs.size()*TAB_HEIGHT);
  // }
  // return false;
  // }
}
