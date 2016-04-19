package crazypants.enderio.machine.monitor.v2;

import java.awt.Rectangle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import com.enderio.core.client.gui.widget.GuiToolTip;

import crazypants.enderio.machine.gui.GuiMachineBase;

public class InvisibleButton extends GuiButton {

  public static final int DEFAULT_WIDTH = 8;
  public static final int DEFAULT_HEIGHT = 6;

  private int xOrigin;
  private int yOrigin;

  protected GuiMachineBase<?> gui;
  protected String[] toolTipText;

  private GuiToolTip toolTip;

  public InvisibleButton(GuiMachineBase<?> gui, int id, int x, int y) {
    super(id, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, "");
    this.gui = gui;
    this.xOrigin = x;
    this.yOrigin = y;
  }

  public InvisibleButton(GuiMachineBase<?> gui, int id, int x, int y, int width, int height) {
    super(id, x, y, width, height, "");
    this.gui = gui;
    this.xOrigin = x;
    this.yOrigin = y;
  }

  public void setToolTip(String... tooltipText) {
    if (toolTip == null) {
      toolTip = new GuiToolTip(getBounds(), tooltipText);
    } else {
      toolTip.setToolTipText(tooltipText);
    }
    this.toolTipText = tooltipText;
  }

  protected void setToolTip(GuiToolTip newToolTip) {
    boolean addTooltip = false;
    if (toolTip != null) {
      addTooltip = gui.removeToolTip(toolTip);
    }
    toolTip = newToolTip;
    if (addTooltip && toolTip != null) {
      gui.addToolTip(toolTip);
    }
  }

  public final Rectangle getBounds() {
    return new Rectangle(xOrigin, yOrigin, getWidth(), getHeight());
  }

  public void onGuiInit() {
    gui.addButton(this);
    if (toolTip != null) {
      gui.addToolTip(toolTip);
    }
    xPosition = xOrigin + gui.getGuiLeft();
    yPosition = yOrigin + gui.getGuiTop();
  }

  public void detach() {
    gui.removeToolTip(toolTip);
    gui.removeButton(this);
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public GuiToolTip getToolTip() {
    return toolTip;
  }

  /**
   * Draws this button to the screen.
   */
  @Override
  public void drawButton(Minecraft mc, int mouseX, int mouseY) {
    if (toolTip != null) {
      toolTip.setIsVisible(visible && enabled);
    }
  }

}
