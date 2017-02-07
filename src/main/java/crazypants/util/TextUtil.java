package crazypants.util;

import java.text.MessageFormat;

// TODO 1.11 - drop
public class TextUtil {

  // Handle french local 'non breaking space' character used to separate thousands.
  // This is not rendered correctly and cannot be parsed by minecraft so replace it with a regular space

  private static final char NBSP = (char) 160;

  public static String fix(String str) {
    return str.replace(NBSP, ' ');
  }

  public static String format(String str, Object... objects) {
    return fix(MessageFormat.format(str, objects));
  }
}
