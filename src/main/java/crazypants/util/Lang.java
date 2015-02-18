package crazypants.util;

import net.minecraft.util.StatCollector;

public class Lang {
  
  public static final String prefix = "enderio.";

  public static String localize(String s, String... args) {
    return localize(s, true, args);
  }

  public static String localize(String s, boolean appendEIO, String... args) {
    if(appendEIO) {
      s = prefix + s;
    }
    return String.format(StatCollector.translateToLocal(s), (Object[]) args);
  }

  public static String[] localizeList(String string) {
    String s = localize(string);
    return s.split("\\|");
  }
}
