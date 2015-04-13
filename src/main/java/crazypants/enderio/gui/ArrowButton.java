package crazypants.enderio.gui;

import crazypants.gui.IGuiScreen;

public class ArrowButton extends IconButtonEIO {

  private final IconEIO unpressed, pressed, hover;

  public ArrowButton(IGuiScreen gui, int id, int x, int y, boolean right) {
    super(gui, id, x, y, null);
    if(right) {
      unpressed = IconEIO.RIGHT_ARROW;
      pressed = IconEIO.RIGHT_ARROW_PRESSED;
      hover = IconEIO.RIGHT_ARROW_HOVER;
    } else {
      unpressed = IconEIO.LEFT_ARROW;
      pressed = IconEIO.LEFT_ARROW_PRESSED;
      hover = IconEIO.LEFT_ARROW_HOVER;
    }
  }

  @Override
  protected IconEIO getIconForHoverState(int hoverState) {
    if(hoverState == 0) {
      return pressed;
    }
    if(hoverState == 2) {
      return hover;
    }
    return unpressed;
  }
}