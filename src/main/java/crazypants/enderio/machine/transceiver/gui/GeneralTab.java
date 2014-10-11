package crazypants.enderio.machine.transceiver.gui;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import crazypants.enderio.config.Config;
import crazypants.enderio.gui.ITabPanel;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.gui.GuiToolTip;
import crazypants.render.ColorUtil;
import crazypants.render.RenderUtil;

public class GeneralTab implements ITabPanel {

  private static final int SEND_BAR_OFFSET = 13;
  ContainerTransceiver container;
  GuiTransceiver parent;
  GuiToolTip sendPowerBarTT;
  
  public GeneralTab(GuiTransceiver guiTransceiver) {
    parent = guiTransceiver;    
    container = parent.container;
    
    sendPowerBarTT = new GuiToolTip(new Rectangle(parent.getPowerX() + SEND_BAR_OFFSET, parent.getPowerY(), parent.getPowerWidth(), parent.getPowerHeight()), "") {
      @Override
      protected void updateText() {        
        text.clear();
        if(parent.renderPowerBar()) {
          updateSendPowerBarTooltip(text);
        }
      }      
    };    
    parent.addToolTip(sendPowerBarTT);
  }

  @Override
  public void onGuiInit(int x, int y, int width, int height) {
    container.setPlayerInventory(true);
  }

  @Override
  public void deactivate() {   
    container.setPlayerInventory(false);
  }

  @Override
  public IconEIO getIcon() {
    return IconEIO.IO_CONFIG_UP;
  }

  @Override
  public void render(float par1, int par2, int par3) {
    int top = parent.getGuiTop();
    int left = parent.getGuiLeft();
    
    GL11.glColor3f(1, 1, 1);
    
    //Inventory
    RenderUtil.bindTexture("enderio:textures/gui/transceiver.png");
    Point invRoot = container.getPlayerInventoryOffset();
    parent.drawTexturedModalRect(left + invRoot.x - 1, top + invRoot.y - 1, 24, 180, 162, 76);  
    
    invRoot = container.getItemInventoryOffset();
    parent.drawTexturedModalRect(left + invRoot.x - 1, top + invRoot.y - 1, 24, 180, 72, 36);
    parent.drawTexturedModalRect(left + invRoot.x - 1 + (18 * 4) + container.getItemBufferSpacing(), top + invRoot.y - 1, 24, 180, 72, 36);
            
    FontRenderer fr = parent.getFontRenderer();
    String sendTxt = "Send";    
    int x = left + invRoot.x + 36 - fr.getStringWidth(sendTxt)/2;
    int y = top + invRoot.y - fr.FONT_HEIGHT - 3;
    fr.drawString(sendTxt, x, y, ColorUtil.getRGB(Color.BLACK));
    String recText = "Receive";
    x = left + invRoot.x + 72 + container.getItemBufferSpacing() + 36 - fr.getStringWidth(recText)/2;
    fr.drawString(recText, x, y, ColorUtil.getRGB(Color.BLACK));
    
    //Highlights
    parent.renderSlotHighlights();    
    
    //Power    
    RenderUtil.bindTexture("enderio:textures/gui/transceiver.png");
    GL11.glColor3f(1, 1, 1);
    
    x = left + parent.getPowerX() - 1;
    y = top + parent.getPowerY() - 1;
    int maxHeight = parent.getPowerHeight();
    
    parent.drawTexturedModalRect(x, y, 233, 196, 12, maxHeight + 2);
    parent.drawTexturedModalRect(x + SEND_BAR_OFFSET, y, 233, 196, 12, maxHeight + 2);
    
    int totalPixelHeight = parent.transceiver.getEnergyStoredScaled(maxHeight * 2);
    int fillHeight = Math.min(totalPixelHeight,maxHeight);
    
    int fillY = y + 1 + parent.getPowerHeight() - fillHeight;
    x += 1;
    parent.drawTexturedModalRect(x, fillY, parent.getPowerU(), parent.getPowerV(), parent.getPowerWidth(), fillHeight);
    
    fillHeight = Math.max(0, totalPixelHeight - maxHeight);
    fillY = y + 1 + parent.getPowerHeight() - fillHeight;
    parent.drawTexturedModalRect(x + SEND_BAR_OFFSET, fillY, parent.getPowerU() - 25, parent.getPowerV(), parent.getPowerWidth(), fillHeight);
    
  }

  public void updatePowerBarTooltip(List<String> text) {
    text.add("Local Buffer");
    text.add("Upkeep: " + PowerDisplayUtil.formatPowerPerTick(parent.getPowerOutputValue()));    
    int maxEnergy = parent.transceiver.getCapacitor().getMaxEnergyStored()/2;
    int energyStored = Math.min(parent.transceiver.getEnergyStored(), maxEnergy);       
    text.add(PowerDisplayUtil.formatStoredPower(energyStored, maxEnergy));    
  }
  
  private void updateSendPowerBarTooltip(List<String> text) {
    text.add("Send/Recieve Buffer");
    text.add("Max IO: " + PowerDisplayUtil.formatPowerPerTick(Config.transceiverMaxIoRF));
    int maxEnergy = parent.transceiver.getCapacitor().getMaxEnergyStored()/2;
    int energyStored = Math.max(0, parent.transceiver.getEnergyStored() - maxEnergy);
    text.add(PowerDisplayUtil.formatStoredPower(energyStored, maxEnergy));    
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
