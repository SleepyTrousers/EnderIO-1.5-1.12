package crazypants.render;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

public abstract class GuiContainerBase extends GuiContainer {

  private List<GuiToolTip> toolTips = new ArrayList<GuiToolTip>();

  protected GuiContainerBase(Container par1Container) {
    super(par1Container);
  }

  protected void addToolTip(GuiToolTip toolTip) {
    toolTips.add(toolTip);
  }

  @Override
  protected final void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    
    drawForegroundImpl(mouseX, mouseY);
    for (GuiToolTip toolTip : toolTips) {
      toolTip.onTick(mouseX - guiLeft, mouseY - guiTop);
      if (toolTip.shouldDraw()) {
        drawTooltip(toolTip, mouseX - guiLeft, mouseY - guiTop);
      }
    }
  }

  protected void drawForegroundImpl(int mouseX, int mouseY) {
    super.drawGuiContainerForegroundLayer(mouseX, mouseY);
  }

  @SuppressWarnings("rawtypes")
  protected void drawTooltip(GuiToolTip toolTip, int mouseX, int mouseY) {
    List<String> list = toolTip.getToolTipText();
    if(mouseX > (xSize/2)) {
      int maxWidth = 0;
      Iterator iterator = list.iterator();
      while (iterator.hasNext()) {
        String s = (String) iterator.next();
        int w = fontRenderer.getStringWidth(s);
        if (w > maxWidth) {
          maxWidth = w;
        }
      }      
      mouseX -= (maxWidth + 18);
    }        
    drawHoveringText(list, mouseX, mouseY, fontRenderer);    
  }

}
