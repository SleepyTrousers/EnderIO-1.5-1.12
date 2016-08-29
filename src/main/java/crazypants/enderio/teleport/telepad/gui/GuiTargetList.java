package crazypants.enderio.teleport.telepad.gui;

import java.awt.Rectangle;
import java.util.ArrayList;

import com.enderio.core.client.gui.widget.GuiScrollableList;
import com.enderio.core.client.render.EnderWidget;

import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.teleport.telepad.TelepadTarget;
import crazypants.enderio.teleport.telepad.TileDialingDevice;
import crazypants.enderio.teleport.telepad.packet.PacketTargetList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.VertexBuffer;

public class GuiTargetList extends GuiScrollableList<TelepadTarget> {

  private final TileDialingDevice te;

  public GuiTargetList(int width, int height, int originX, int originY, TileDialingDevice te) {
    super(width, height, originX, originY, Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT + 4);
    this.te = te;
  }

  @Override
  public TelepadTarget getElementAt(int index) {
    ArrayList<TelepadTarget> targs = te.getTargets();
    if (index < 0 || index >= targs.size()) {
      return null;
    }
    return targs.get(index);
  }

  @Override
  public int getNumElements() {
    return te.getTargets().size();
  }

  @Override
  protected void drawElement(int elementIndex, int x, int y, int height, VertexBuffer renderer) {
    TelepadTarget targ = getElementAt(elementIndex);
    if (targ == null) {
      return;
    }
    String name = targ.getName();
    if (name == null || name.trim().length() == 0) {
      name = "Unnamed";
    }
    FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
    fr.drawString(name, x + 4, y + 2, 0xffffff, true);
    
    if (getSelectedElement() == targ) {      
      Rectangle iconBounds = getIconBounds(y);
      EnderWidget icon = EnderWidget.X_BUT;
      if(iconBounds.contains(mouseX, mouseY)) {       
        icon = EnderWidget.X_BUT_HOVER;
      }      
      EnderWidget.map.render(icon, iconBounds.x, iconBounds.y, iconBounds.width, iconBounds.height, 0, true);
    }
  }

  @Override
  protected boolean elementClicked(int elementIndex, boolean doubleClick, int elX, int elY) {
    TelepadTarget target = getSelectedElement();
    if(target == null) {
      return true;
    }    
    Rectangle iconBounds = getIconBounds(0);   
    if(iconBounds.contains(elX, elY)) {      
      te.removeTarget(target);
      PacketHandler.INSTANCE.sendToServer(new PacketTargetList(te, target, false));
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
