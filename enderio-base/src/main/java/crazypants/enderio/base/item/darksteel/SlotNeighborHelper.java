package crazypants.enderio.base.item.darksteel;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import net.minecraft.util.math.MathHelper;

public class SlotNeighborHelper {

  private static final @Nonnull int[][] MAP = new int[41][];
  private static final @Nonnull int[] ALL = new int[41];

  static {
    MAP[0] = new int[] { 27, 28, 40, 1 };
    for (int i = 1; i <= 7; i++) {
      MAP[i] = new int[] { i + 26, i + 27, i + 28, i - 1, i + 1 };
    }
    MAP[8] = new int[] { 34, 25, 7 };
    MAP[9] = new int[] { 36, 10, 18, 19 };
    for (int i = 10; i <= 11; i++) {
      MAP[i] = new int[] { i + 8, i + 9, i + 10, i - 1, i + 1 };
    }
    for (int i = 12; i <= 14; i++) {
      MAP[i] = new int[] { 40, i + 8, i + 9, i + 10, i - 1, i + 1 };
    }
    for (int i = 15; i <= 16; i++) {
      MAP[i] = new int[] { i + 8, i + 9, i + 10, i - 1, i + 1 };
    }
    MAP[17] = new int[] { 16, 25, 26 };
    MAP[18] = new int[] { 9, 10, 19, 27, 28 };
    for (int i = 19; i <= 25; i++) {
      MAP[i] = new int[] { i + 8, i + 9, i + 10, i - 1, i + 1, i - 10, i - 9, i - 8 };
    }
    MAP[26] = new int[] { 16, 17, 25, 34, 35 };
    MAP[27] = new int[] { 18, 19, 28, 40, 0, 1 };
    for (int i = 28; i <= 34; i++) {
      MAP[i] = new int[] { i - 28, i - 27, i - 26, i - 1, i + 1, i - 10, i - 9, i - 8 };
    }
    MAP[35] = new int[] { 25, 26, 34, 7, 8 };
    MAP[36] = new int[] { 9, 10, 40, 37 };
    MAP[37] = new int[] { 36, 38, 40 };
    MAP[38] = new int[] { 37, 39, 40 };
    MAP[39] = new int[] { 38, 40 };
    MAP[40] = new int[] { 36, 37, 38, 39, 12, 13, 14, 0, 27 };

    for (int i = 0; i <= 40; i++) {
      ALL[i] = i;
    }
  }

  public static @Nonnull int[] getSlotNeighbors(int slot) {
    return NullHelper.notnull(MAP[MathHelper.clamp(slot, 0, 40)], "Internal data error");
  }

  public static @Nonnull int[] getAllSlots() {
    return ALL;
  }

}
