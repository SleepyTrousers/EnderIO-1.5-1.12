package crazypants.enderio.conduit.redstone;

import net.minecraft.item.ItemDye;

public enum SignalColor {
  BLACK,
  RED,
  GREEN,
  BROWN,
  BLUE,
  PURPLE,
  CYAN,
  SILVER,
  GRAY,
  PINK,
  LIME,
  YELLOW,
  LIGHT_BLUE,
  MAGENTA,
  ORANGE,
  WHITE;

  public static SignalColor getNext(SignalColor mode) {
    int ord = mode.ordinal() + 1;
    if(ord >= SignalColor.values().length) {
      ord = 0;
    }
    return SignalColor.values()[ord];
  }

  public static SignalColor fromIndex(int index) {
    return SignalColor.values()[index];
  }

  private SignalColor() {
  }

  int getColor() {
    return ItemDye.dyeColors[ordinal()];
  }

}