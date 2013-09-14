package crazypants.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;

public class ToggleButton extends IconButton {

  private boolean selected = false;

  public ToggleButton(FontRenderer fr, int id, int x, int y, Icon icon, ResourceLocation texture) {
    super(fr, id, x, y, icon, texture);
  }

  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  protected int getHoverState(boolean par1) {
    int result = 1;
    if (!enabled || selected) {
      result = 0;
    } else if (par1) {
      result = 2;
    }
    return result;
  }

}
