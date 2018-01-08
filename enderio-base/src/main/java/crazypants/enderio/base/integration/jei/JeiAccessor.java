package crazypants.enderio.base.integration.jei;

import javax.annotation.Nonnull;

public class JeiAccessor {

  static boolean jeiRuntimeAvailable = false;

  public static boolean isJeiRuntimeAvailable() {
    return jeiRuntimeAvailable;
  }

  public static void setFilterText(@Nonnull String filterText) {
    if (jeiRuntimeAvailable) {
      JeiPlugin.setFilterText(filterText);
    }
  }

  public static @Nonnull String getFilterText() {
    if (jeiRuntimeAvailable) {
      return JeiPlugin.getFilterText();
    }
    return "";
  }

}
