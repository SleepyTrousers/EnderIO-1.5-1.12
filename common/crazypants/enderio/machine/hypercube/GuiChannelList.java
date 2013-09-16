package crazypants.enderio.machine.hypercube;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import crazypants.gui.GuiScrollableList;
import crazypants.render.ColorUtil;
import crazypants.render.RenderUtil;

public class GuiChannelList extends GuiScrollableList<Channel> {

  private List<Channel> channels = new ArrayList<Channel>();
  
  private Channel activeChannel;
  
  private final GuiHyperCube parent;
  
  public GuiChannelList(GuiHyperCube parent, int width, int height, int originX, int originY) {
    super(width, height, originX, originY, Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + 4);
    this.parent = parent;  
  }
  
  void setChannels(List<Channel> val) {
    if(val == null) {
      channels = Collections.emptyList();
    }
    channels = val;    
  }
  
  void setActiveChannel(Channel channel) {
    activeChannel = channel;
  }
  
  @Override
  public int getNumElements() {
    return isActiveChannelListed() ? channels.size() : channels.size() + 1;
  }
  
  @Override
  public Channel getElementAt(int index) {
    if(!isActiveChannelListed()) {
      if(index == 0) {
        return activeChannel;
      }
      index--;
    }
    if(index < 0 || index >= channels.size()) {
      return null;
    }
    return channels.get(index);
  }
  
  protected boolean isActiveChannelListed() {
    return activeChannel == null || channels.contains(activeChannel);
  }

  @Override
  protected boolean elementClicked(int i, boolean flag) {
    if(getElementAt(i) == null) {
      return false;
    } else {
      return true;
    }
  }

 
  @Override
  protected void drawElement(int index, int xPosition, int yPosition, int rowHeight, Tessellator tessellator) {
    if(index < 0 || index >= channels.size()) {
      return;
    }    
    Channel c = getElementAt(index);
    if(c == null) {
      return;
    }
    int col = ColorUtil.getRGB(Color.white);
    if(c.equals(activeChannel)) {      
      col = ColorUtil.getRGB(Color.cyan);
      if(isActiveChannelListed()) {
        RenderUtil.renderQuad2D(xPosition - 1, yPosition, 0, width, rowHeight + 2, ColorUtil.getRGB(Color.darkGray));
      } else {
        RenderUtil.renderQuad2D(xPosition - 1, yPosition, 0, width, rowHeight + 2, ColorUtil.getRGB(Color.red.darker()));
      }
    }
    parent.drawString(parent.getFontRenderer(), c.name, xPosition + margin, yPosition + margin/2, col);
  }

}
