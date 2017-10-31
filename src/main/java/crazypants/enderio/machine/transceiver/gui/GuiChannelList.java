package crazypants.enderio.machine.transceiver.gui;

import java.awt.Color;
import java.util.Set;

import com.enderio.core.client.gui.widget.GuiScrollableList;
import com.enderio.core.client.render.ColorUtil;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;

import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.transceiver.Channel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.VertexBuffer;

public class GuiChannelList extends GuiScrollableList<Channel> {

  private FluentIterable<Channel> channels;

  private final GuiTransceiver parent;

  public GuiChannelList(GuiTransceiver parent, int width, int height, int originX, int originY) {
    super(width, height, originX, originY, Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + 4);
    this.parent = parent;
  }

  void setChannels(Set<Channel> val, Predicate<Channel> filter) {
    if (val == null) {
      channels = FluentIterable.from(Sets.<Channel> newHashSet());
    }
    channels = FluentIterable.from(val).filter(filter);
  }

  @Override
  public int getNumElements() {
    return channels.size();
  }

  @Override
  public Channel getElementAt(int index) {
    if (index < 0 || index >= channels.size()) {
      return null;
    }
    return channels.get(index);
  }

  @Override
  protected boolean elementClicked(int i, boolean flag, int x, int y) {
    if (getElementAt(i) == null) {
      return false;
    } else {
      return true;
    }
  }

  @Override
  protected void drawElement(int index, int xPosition, int yPosition, int rowHeight, VertexBuffer renderer) {
    if (index < 0 || index >= channels.size()) {
      return;
    }
    Channel c = getElementAt(index);
    if (c == null) {
      return;
    }
    int col = ColorUtil.getRGB(Color.white);
    parent.drawString(parent.getFontRenderer(), c.getName(), xPosition + margin, yPosition + margin / 2, col);
    if (!c.isPublic()) {
      IconEIO.map.render(IconEIO.LOCK_LOCKED, xPosition + width - 18, yPosition - 3, 16, 15, 0, true);
    }
  }

}
