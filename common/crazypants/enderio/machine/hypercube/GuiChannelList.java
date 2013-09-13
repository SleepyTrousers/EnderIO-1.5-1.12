package crazypants.enderio.machine.hypercube;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.renderer.Tessellator;
import crazypants.render.GuiScrollableList;
import crazypants.render.RenderUtil;

public class GuiChannelList extends GuiScrollableList {

  private int width;
  private int height;
  
  private int currentSelection = -1;
  
  private List<Channel> channels = new ArrayList<Channel>();
  
  private Channel activeChannel;
  
  private final GuiHyperCube parent;
  
  public GuiChannelList(GuiHyperCube parent, int width, int height, int originX, int originY) {
    super(width, height, originX, originY, parent.getFontRenderer().FONT_HEIGHT + 4);
    this.parent = parent;
    this.width = width;
    this.height = height;
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
  protected int getNumElements() {
    //return Math.max(1,channels.size()); // avoid /0 in base class
    return channels.size();
  }

  @Override
  protected void elementClicked(int i, boolean flag) {
    if(i < 0 || i >= channels.size()) {
      currentSelection = -1;
    } else {
      currentSelection = i;
    }
  }

  @Override
  protected boolean isSelected(int i) {    
    return i == currentSelection;
  }
  
  @Override
  protected void drawElement(int index, int xPosition, int yPosition, int l, Tessellator tessellator) {
    if(index < 0 || index >= channels.size()) {
      return;
    }    
    Channel c = channels.get(index);        
    parent.drawString(parent.getFontRenderer(), c.name, xPosition + margin, yPosition + margin/2, RenderUtil.getRGB(Color.white));
  }
  

}
