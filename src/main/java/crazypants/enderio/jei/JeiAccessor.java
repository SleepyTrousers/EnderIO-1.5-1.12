package crazypants.enderio.jei;

import javax.annotation.Nonnull;

public class JeiAccessor {

  static boolean jeiRuntimeAvailable = false;

  public static boolean isJeiRuntimeAvailable() {
    return jeiRuntimeAvailable;
  }

  public static void setFilterText(@Nonnull String filterText) {
    if (jeiRuntimeAvailable) {
      try { // TODO: 1.10.2 remove tray/catch after updating and setting the required JEI version to 3.7.3.221 or higher
        JeiPlugin.setFilterText(filterText);
      } catch (Exception e) {
      }
    }
  }

  public static @Nonnull String getFilterText() {
    if (jeiRuntimeAvailable) {
      try { // TODO: 1.10.2 remove tray/catch after updating and setting the required JEI version to 3.7.3.221 or higher
        return JeiPlugin.getFilterText();
      } catch (Exception e) {
      }
    }
    return "";
  }

}
