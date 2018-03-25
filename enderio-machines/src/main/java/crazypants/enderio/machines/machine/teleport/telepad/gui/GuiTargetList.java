package crazypants.enderio.machines.machine.teleport.telepad.gui;

import java.awt.Rectangle;
import java.util.ArrayList;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.widget.GuiScrollableList;
import com.enderio.core.client.render.EnderWidget;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.item.coordselector.TelepadTarget;
import crazypants.enderio.machines.lang.Lang;
import crazypants.enderio.machines.machine.teleport.telepad.TileDialingDevice;
import crazypants.enderio.machines.machine.teleport.telepad.packet.PacketTargetList;
import crazypants.enderio.machines.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;

public class GuiTargetList extends GuiScrollableList<TelepadTarget> {

  private final @Nonnull TileDialingDevice te;

  public GuiTargetList(int width, int height, int originX, int originY, @Nonnull TileDialingDevice te) {
    super(width, height, originX, originY, Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + 4);
    this.te = te;
  }

  @Override
  public @Nonnull TelepadTarget getElementAt(int index) {
    ArrayList<TelepadTarget> targs = te.getTargets();
    if (index < 0 || index >= targs.size()) {
      throw new IndexOutOfBoundsException("No telepad target for index " + index);
    }
    return NullHelper.notnull(targs.get(index), "Telepad target was null in list");
  }

  @Override
  public int getNumElements() {
    return te.getTargets().size();
  }

  @Override
  protected void drawElement(int elementIndex, int x, int y, int heightIn, @Nonnull BufferBuilder renderer) {
    TelepadTarget targ = getElementAt(elementIndex);
    String name = targ.getName();
    if (name.trim().length() == 0) {
      name = Lang.GUI_TELEPAD_UNNAMED_LOCATION.get();
    }
    FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
    fr.drawString(name, x + 4, y + 2, 0xffffff, true);

    if (getSelectedElement() == targ) {
      Rectangle iconBounds = getIconBounds(y);
      EnderWidget icon = EnderWidget.X_BUT;
      // TODO no access to these vars anymore
      // if(iconBounds.contains(mouseX, mouseY)) {
      // icon = EnderWidget.X_BUT_HOVER;
      // }
      EnderWidget.map.render(icon, iconBounds.x, iconBounds.y, iconBounds.width, iconBounds.height, 0, true);
    }
  }

  @Override
  protected boolean elementClicked(int elementIndex, boolean doubleClick, int elX, int elY) {
    TelepadTarget target = getSelectedElement();
    Rectangle iconBounds = getIconBounds(0);
    if (iconBounds.contains(elX, elY)) {
      PacketHandler.INSTANCE.sendToServer(new PacketTargetList(te, target, false));
      if (selectedIndex >= getNumElements()) {
        setSelection(getNumElements() - 1);
      }
      return false;
    }
    return true;
  }

  public Rectangle getIconBounds(int minElY) {
    EnderWidget icon = EnderWidget.RETURN_BUT_HOVER;
    int xPos = minX + width - icon.width - 2;
    if (getContentOverhang() > 0) { // scroll bar visible
      xPos -= 6;
    }
    Rectangle iconBounds = new Rectangle(xPos, minElY + 1, icon.width, icon.height);
    return iconBounds;
  }

}
