package crazypants.enderio.machine.transceiver.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.config.Config;
import crazypants.enderio.gui.IGuiOverlay;
import crazypants.enderio.gui.ITabPanel;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.machine.gui.GuiPoweredMachineBase;
import crazypants.enderio.machine.transceiver.ChannelType;
import crazypants.enderio.machine.transceiver.TileTransceiver;
import crazypants.render.RenderUtil;

public class GuiTransceiver extends GuiPoweredMachineBase {

  private static final int TAB_HEIGHT = 24;

  TileTransceiver entity;

  private int activeTab = 0;
  private final List<ITabPanel> tabs = new ArrayList<ITabPanel>();
  private int tabYOffset = 4;

  ContainerTransceiver container;
  TileTransceiver transceiver;
  
  GeneralTab generalTab;

  public GuiTransceiver(InventoryPlayer par1InventoryPlayer, TileTransceiver te) {
    super(te, new ContainerTransceiver(par1InventoryPlayer, te));
    entity = te;
    container = (ContainerTransceiver) inventorySlots;
    transceiver = te;

    generalTab = new GeneralTab(this); 
    tabs.add(generalTab);
    FilterTab filterTab = new FilterTab(this);
    tabs.add(filterTab);
    tabs.add(new ChannelTab(this, ChannelType.POWER));
    tabs.add(new ChannelTab(this, ChannelType.ITEM));
    tabs.add(new ChannelTab(this, ChannelType.FLUID));  
    if(Config.enderRailEnabled) {
      tabs.add(new ChannelTab(this, ChannelType.RAIL));
    }
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
      if(i == activeTab) {
        tabs.get(i).updateScreen();
        return;
      }
    }
  }

  @Override
  protected void keyTyped(char par1, int par2) {
    if(par2 == 1) {
      for (IGuiOverlay overlay : overlays) {
        if(overlay.isVisible()) {
          overlay.setVisible(false);
          return;
        }
      }
      mc.thePlayer.closeScreen();
    }

    for (int i = 0; i < tabs.size(); i++) {
      if(i == activeTab) {
        tabs.get(i).keyTyped(par1, par2);
        return;
      }
    }
  }

  @Override
  public void initGui() {
    super.initGui();
    for (int i = 0; i < tabs.size(); i++) {
      if(i != activeTab) {
        tabs.get(i).deactivate();
      }
    }
    for (int i = 0; i < tabs.size(); i++) {
      if(i == activeTab) {
        tabs.get(i).onGuiInit(guiLeft + 10, guiTop, xSize - 20, ySize - 20);
      }
    }
    configB.visible = activeTab == 0;
    redstoneButton.visible = activeTab == 0;
  }

  @Override
  public void renderPowerBar(int k, int l) {
    //super.renderPowerBar(k, l);
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
  public  int getPowerOutputValue() {
    return super.getPowerOutputValue();
  }

  @Override
  protected void mouseClicked(int x, int y, int par3) {
    super.mouseClicked(x, y, par3);

    int tabLeftX = xSize;
    int tabRightX = tabLeftX + 22;

    int minY = tabYOffset;
    int maxY = minY + (tabs.size() * TAB_HEIGHT);

    x = (x - guiLeft);
    y = (y - guiTop);

    if(x > tabLeftX && x < tabRightX + 24) {
      if(y > minY && y < maxY) {
        activeTab = (y - minY) / 24;
        hideOverlays();
        initGui();
        return;
      }
    }
    tabs.get(activeTab).mouseClicked(x, y, par3);
  }

  @Override
  protected void actionPerformed(GuiButton guiButton) {
    super.actionPerformed(guiButton);
    tabs.get(activeTab).actionPerformed(guiButton);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {

    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;
    int tabX = sx + xSize - 3;

    Tessellator tes = Tessellator.instance;
    tes.startDrawingQuads();
    for (int i = 0; i < tabs.size(); i++) {
      if(i != activeTab) {
        RenderUtil.bindTexture(IconEIO.TEXTURE);
        IconEIO.INACTIVE_TAB.renderIcon(tabX, sy + tabYOffset + (i * 24));
        IconEIO icon = tabs.get(i).getIcon();
        icon.renderIcon(tabX + 4, sy + tabYOffset + (i * TAB_HEIGHT) + 7, 10, 10, 0, false);
      }
    }

    tes.draw();

    RenderUtil.bindTexture("enderio:textures/gui/transceiver.png");
    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);
    super.drawGuiContainerBackgroundLayer(par1, par2, par3);

    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture(IconEIO.TEXTURE);
    tes.startDrawingQuads();
    IconEIO.ACTIVE_TAB.renderIcon(tabX, sy + tabYOffset + (activeTab * TAB_HEIGHT));

    if(tabs.size() > 0) {
      IconEIO icon = tabs.get(activeTab).getIcon();
      icon.renderIcon(tabX + 4, sy + tabYOffset + (activeTab * TAB_HEIGHT) + 7, 10, 10, 0, false);
      tes.draw();
      tabs.get(activeTab).render(par1, par2, par3);
    } else {
      tes.draw();
    }
  }

}
