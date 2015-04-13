package crazypants.enderio.machine.transceiver.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.event.terraingen.BiomeEvent.GetWaterColor;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.machine.hypercube.GuiHyperCube;
import crazypants.enderio.machine.transceiver.Channel;
import crazypants.gui.GuiScrollableList;
import crazypants.render.ColorUtil;

public class GuiChannelList extends GuiScrollableList<Channel> {

  private List<Channel> channels = new ArrayList<Channel>();

//  private Channel activeChannel;

  private final GuiTransceiver parent;

  public GuiChannelList(GuiTransceiver parent, int width, int height, int originX, int originY) {
    super(width, height, originX, originY, Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + 4);
    this.parent = parent;
  }

  void setChannels(List<Channel> val) {
    if(val == null) {
      channels = Collections.emptyList();
    }
    channels = val;
  }

  @Override
  public int getNumElements() {
    return channels.size();
  }

  @Override
  public Channel getElementAt(int index) {    
    if(index < 0 || index >= channels.size()) {
      return null;
    }
    return channels.get(index);
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
    parent.drawString(parent.getFontRenderer(), c.getName(), xPosition + margin, yPosition + margin / 2, col);
    if(!c.isPublic()) {
      IconEIO.LOCK_LOCKED.renderIcon(xPosition + width - 18, yPosition - 3, 16, 15, 0, true);
    }
  }

}
