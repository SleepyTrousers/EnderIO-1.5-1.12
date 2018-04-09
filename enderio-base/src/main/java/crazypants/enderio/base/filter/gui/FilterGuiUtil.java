package crazypants.enderio.base.filter.gui;

public class FilterGuiUtil {

  // List of filter indexes for reference in guis
  public static final int INDEX_INPUT_ITEM = 1;
  public static final int INDEX_OUTPUT_ITEM = 2;
  public static final int INDEX_INPUT_FLUID = 3;
  public static final int INDEX_OUTPUT_FLUID = 4;
  public static final int INDEX_NONE = 0;

  private static int nextButtonId = 1;

  public static int nextButtonId() {
    return nextButtonId++;
  }
}
