package crazypants.enderio.machine.transceiver.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.gui.IGuiOverlay;
import crazypants.enderio.gui.ITabPanel;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.machine.GuiMachineBase;
import crazypants.enderio.machine.generator.stirling.StirlingGeneratorContainer;
import crazypants.enderio.machine.generator.stirling.TileEntityStirlingGenerator;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.machine.transceiver.ChannelType;
import crazypants.enderio.machine.transceiver.TileTransceiver;
import crazypants.render.ColorUtil;
import crazypants.render.RenderUtil;
import crazypants.util.Lang;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiTransceiver extends GuiMachineBase {

  private static final int TAB_HEIGHT = 24;
  
  private TileTransceiver entity;

  private int activeTab = 0;
  private final List<ITabPanel> tabs = new ArrayList<ITabPanel>();
  private int tabYOffset = 4;
  
  ContainerTransceiver container;
  TileTransceiver transceiver;
  
  public GuiTransceiver(InventoryPlayer par1InventoryPlayer, TileTransceiver te) {
    super(te, new ContainerTransceiver(par1InventoryPlayer, te));
    this.entity = te;
    container = (ContainerTransceiver)inventorySlots;
    transceiver = te;
    
    tabs.add(new GeneralTab(this));
    tabs.add(new ChannelTab(this, ChannelType.POWER));
    tabs.add(new ChannelTab(this, ChannelType.ITEM));
    tabs.add(new ChannelTab(this, ChannelType.FLUID));
  }

  @Override
  protected boolean showRecipeButton() {
    return false;
  }
  
  @Override
  public int getXSize() {    
    return 256;
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
      this.mc.thePlayer.closeScreen();
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
      if(i == activeTab) {
        tabs.get(i).onGuiInit(guiLeft + 10, guiTop, xSize - 20, ySize - 20);
      } else {
        tabs.get(i).deactivate();
      }
    }    
    configB.visible = activeTab == 0;
    redstoneButton.visible = activeTab == 0;    
  }

  @Override
  protected boolean renderPowerBar() {
    return activeTab == 0;
  }

  @Override
  protected int getPowerX() {
    return super.getPowerX() - 4;
  }

  @Override
  protected int getPowerHeight() {
    return 58;
  }

  @Override
  protected int getPowerY() {
    return super.getPowerY();
  }

  @Override
  protected int getPowerV() {
    return 196;
  }

  @Override
  protected int getPowerU() {
    return 246;
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
    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);
    if(renderPowerBar()) {
      drawTexturedModalRect(getGuiLeft() + getPowerX() - 1, getGuiTop() + getPowerY() - 1, 233, 196, 12, getPowerHeight() + 2);
    }
    super.drawGuiContainerBackgroundLayer(par1, par2, par3);

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
