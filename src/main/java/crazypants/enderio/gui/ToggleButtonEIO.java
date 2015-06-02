package crazypants.enderio.gui;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import crazypants.gui.GuiToolTip;
import crazypants.gui.IGuiScreen;

public class ToggleButtonEIO extends IconButtonEIO {

  private boolean selected;
  private final IconEIO unselectedIcon;
  private final IconEIO selectedIcon;
  
  private GuiToolTip selectedTooltip, unselectedTooltip;
  private boolean paintSelectionBorder;

  public ToggleButtonEIO(IGuiScreen gui, int id, int x, int y, IconEIO unselectedIcon, IconEIO selectedIcon) {
    super(gui, id, x, y, unselectedIcon);
    this.unselectedIcon = unselectedIcon;
    this.selectedIcon = selectedIcon;
    selected = false;
    paintSelectionBorder = true;
  }

  public boolean isSelected() {
    return selected;
  }

  public ToggleButtonEIO setSelected(boolean selected) {
    this.selected = selected;
    icon = selected ? selectedIcon : unselectedIcon;
    if(selected && selectedTooltip != null) {
      setToolTip(selectedTooltip);
    } else if(!selected && unselectedTooltip != null) {
      setToolTip(unselectedTooltip);
    }
    return this;
  }

  @Override
  protected IconEIO getIconForHoverState(int hoverState) {
    if(!selected || !paintSelectionBorder) {
      return super.getIconForHoverState(hoverState);
    }
    if(hoverState == 0) {
      return IconEIO.BUTTON_DISABLED;
    }
    if(hoverState == 2) {
      return IconEIO.BUTTON_DOWN_HIGHLIGHT;
    }
    return IconEIO.BUTTON_DOWN;
  }

  @Override
  public boolean mousePressed(Minecraft par1Minecraft, int par2, int par3) {
    if(super.mousePressed(par1Minecraft, par2, par3)) {
      return toggleSelected();
    }
    return false;
  }

  protected boolean toggleSelected() {
    setSelected(!selected);
    return true;
  }

  public void setSelectedToolTip(String... tt) {
    String[] combinedTooltip = ArrayUtils.addAll(toolTipText, tt);
    selectedTooltip = new GuiToolTip(getBounds(), Lists.newArrayList(combinedTooltip));
    setSelected(selected);
  }

  public void setUnselectedToolTip(String... tt) {
    String[] combinedTooltip = ArrayUtils.addAll(toolTipText, tt);
    unselectedTooltip = new GuiToolTip(getBounds(), Lists.newArrayList(combinedTooltip));
    setSelected(selected);
  }

  public void setPaintSelectedBorder(boolean b) {
    this.paintSelectionBorder = b;
  }

}
