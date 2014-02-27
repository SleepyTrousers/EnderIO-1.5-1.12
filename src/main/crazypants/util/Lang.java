package crazypants.util;

import net.minecraft.util.StatCollector;

public class Lang {

  public static String localize(String s) {
    return localize(s, true);
  }

  public static String localize(String s, boolean appendEIO) {
    if(appendEIO) {
      s = "enderio." + s;
    }
    return StatCollector.translateToLocal(s);
  }

  public static String[] localizeList(String string) {
    String s = localize(string);
    return s.split("\\|");
  }

}
