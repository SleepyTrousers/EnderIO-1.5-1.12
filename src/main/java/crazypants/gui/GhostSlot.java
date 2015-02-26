package crazypants.gui;

import net.minecraft.item.ItemStack;

public class GhostSlot {

  public int x;
  public int y;
  public boolean visible = true;
  public ItemStack stack;

  public boolean isMouseOver(int mx, int my) {
    return mx >= x && mx < (x+16) && my >= y && my < (y+16);
  }

  public boolean isVisible() {
    return visible;
  }

  public ItemStack getStack() {
    return stack;
  }

  public void putStack(ItemStack stack) {
  }
}
