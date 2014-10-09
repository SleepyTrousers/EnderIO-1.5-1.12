package crazypants.enderio.machine.transceiver.gui;

import java.awt.Point;

import net.minecraft.client.gui.GuiButton;
import crazypants.enderio.gui.ITabPanel;
import crazypants.enderio.gui.IconEIO;
import crazypants.render.RenderUtil;

public class GeneralTab implements ITabPanel {

  ContainerTransceiver container;
  GuiTransceiver parent;
  
  public GeneralTab(GuiTransceiver guiTransceiver) {
    parent = guiTransceiver;
    container = parent.container;
  }

  @Override
  public void onGuiInit(int x, int y, int width, int height) {
    container.setPlayerInventoryVisible(true);
  }

  @Override
  public void deactivate() {   
    container.setPlayerInventoryVisible(false);
  }

  @Override
  public IconEIO getIcon() {
    return IconEIO.IO_CONFIG_UP;
  }

  @Override
  public void render(float par1, int par2, int par3) {    
    RenderUtil.bindTexture("enderio:textures/gui/transceiver.png");
    Point invRoot = container.getPlayerInventoryOffset();
    parent.drawTexturedModalRect(parent.getGuiLeft() + invRoot.x - 1, parent.getGuiTop() + invRoot.y - 1, 24, 180, 162, 76);       
    
  }

  @Override
  public void actionPerformed(GuiButton guiButton) {    
  }

  @Override
  public void mouseClicked(int x, int y, int par3) {    
  }
  
  @Override
  public void updateScreen() {   
  }

  @Override
  public void keyTyped(char par1, int par2) {    
  }

}
