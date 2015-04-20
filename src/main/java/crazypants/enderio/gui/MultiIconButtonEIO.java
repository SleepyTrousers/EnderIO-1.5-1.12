package crazypants.enderio.gui;

import crazypants.gui.IGuiScreen;

public class MultiIconButtonEIO extends IconButtonEIO {

  private final IconEIO unpressed;
  private final IconEIO pressed;
  private final IconEIO hover;

  public MultiIconButtonEIO(IGuiScreen gui, int id, int x, int y, IconEIO unpressed, IconEIO pressed, IconEIO hover) {
    super(gui, id, x, y, null);
    this.unpressed = unpressed;
    this.pressed = pressed;
    this.hover = hover;
    setSize((int)unpressed.width, (int)unpressed.height);
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

  public static MultiIconButtonEIO createRightArrowButton(IGuiScreen gui, int id, int x, int y) {
    return new MultiIconButtonEIO(gui, id, x, y, IconEIO.RIGHT_ARROW, IconEIO.RIGHT_ARROW_PRESSED, IconEIO.RIGHT_ARROW_HOVER);
  }

  public static MultiIconButtonEIO createLeftArrowButton(IGuiScreen gui, int id, int x, int y) {
    return new MultiIconButtonEIO(gui, id, x, y, IconEIO.LEFT_ARROW, IconEIO.LEFT_ARROW_PRESSED, IconEIO.LEFT_ARROW_HOVER);
  }

  public static MultiIconButtonEIO createAddButton(IGuiScreen gui, int id, int x, int y) {
    return new MultiIconButtonEIO(gui, id, x, y, IconEIO.ADD_BUT, IconEIO.ADD_BUT_PRESSED, IconEIO.ADD_BUT_HOVER);
  }

  public static MultiIconButtonEIO createMinusButton(IGuiScreen gui, int id, int x, int y) {
    return new MultiIconButtonEIO(gui, id, x, y, IconEIO.MINUS_BUT, IconEIO.MINUS_BUT_PRESSED, IconEIO.MINUS_BUT_HOVER);
  }
}
