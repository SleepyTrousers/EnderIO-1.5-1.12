package crazypants.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;

public class ToolTipManager {

  public static interface ToolTipRenderer {
    int getGuiLeft();
    int getGuiTop();
    int getXSize();
    FontRenderer getFontRenderer();
    void drawHoveringText(List par1List, int par2, int par3, FontRenderer font);
  }
  
  private List<GuiToolTip> toolTips = new ArrayList<GuiToolTip>();
  
  public void addToolTip(GuiToolTip toolTip) {
    toolTips.add(toolTip);
  }

  protected final void drawTooltips(ToolTipRenderer renderer, int mouseX, int mouseY) {
    for (GuiToolTip toolTip : toolTips) {
      toolTip.onTick(mouseX - renderer.getGuiLeft(), mouseY - renderer.getGuiTop());
      if (toolTip.shouldDraw()) {
        drawTooltip(toolTip, mouseX - renderer.getGuiLeft(), mouseY - renderer.getGuiTop(), renderer);
      }
    }
  }

  protected void drawTooltip(GuiToolTip toolTip, int mouseX, int mouseY, ToolTipRenderer renderer) {
    List<String> list = toolTip.getToolTipText();
    
    List<String> formatted = new ArrayList<String>(list.size());
    for(int i=0;i<list.size();i++) {
      if(i == 0) {
        formatted.add("\u00a7" + Integer.toHexString(15) + list.get(i));
      } else {
        formatted.add("\u00a77" + list.get(i));
      }
    }
    
    if (mouseX > (renderer.getXSize() / 2)) {
      int maxWidth = 0;
      Iterator iterator = formatted.iterator();
      while (iterator.hasNext()) {
        String s = (String) iterator.next();
        int w = renderer.getFontRenderer().getStringWidth(s);
        if (w > maxWidth) {
          maxWidth = w;
        }
      }
      mouseX -= (maxWidth + 18);
    }
    renderer.drawHoveringText(formatted, mouseX, mouseY,renderer.getFontRenderer());
  }

  
}
