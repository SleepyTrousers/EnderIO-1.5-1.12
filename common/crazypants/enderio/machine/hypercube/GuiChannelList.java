package crazypants.enderio.machine.hypercube;

import java.awt.Color;
import java.io.ObjectInputStream.GetField;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import crazypants.render.GuiScrollableList;
import crazypants.render.RenderUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;

public class GuiChannelList extends GuiScrollableList {

  private int width;
  private int height;
  
  private int currentSelection = -1;
  
  private final List<Channel> channels = new ArrayList<Channel>();
  
  private Channel activeChannel;
  
  private final GuiHyperCube parent;
  
  public GuiChannelList(GuiHyperCube parent, int width, int height, int originX, int originY) {
    super(width, height, originX, originY, parent.getFontRenderer().FONT_HEIGHT + 4);
    this.parent = parent;
    this.width = width;
    this.height = height;
  }
  
  void setChannels(List<Channel> val) {
    channels.clear();
    channels.addAll(val);
  }
  
  void addChannel(Channel chanel) {
    channels.add(chanel);
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
  protected void drawSlot(int index, int xPosition, int yPosition, int l, Tessellator tessellator) {
    if(index < 0 || index >= channels.size()) {
      return;
    }    
    Channel c = channels.get(index);        
    parent.drawString(parent.getFontRenderer(), c.name, xPosition + margin, yPosition + margin/2, RenderUtil.getRGB(Color.white));
  }
  

}
