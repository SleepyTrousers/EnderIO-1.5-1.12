package crazypants.util;

public class MetadataUtil {

  public static void printBits(int meta) {
    for (int k = 0; k < 4; k++) {
      if(((meta >> k) & 1) == 1) {
        System.out.print(1);
      } else {
        System.out.print(0);
      }
    }
    System.out.println();
  }

  public static String toBitString(int meta) {
    StringBuilder sb = new StringBuilder();
    for (int k = 0; k < 4; k++) {
      if(((meta >> k) & 1) == 1) {
        sb.append(1);
      } else {
        sb.append(0);
      }
    }
    return sb.toString();
  }

  public static boolean isBitSet(int position, int meta) {
    return ((meta >> position) & 1) == 1;
  }

  public static int setBit(int position, boolean value, int meta) {
    if(value) {
      return meta | (1 << position);
    }
    return meta & ~(1 << position);
  }

  public static int toggleBit(int position, int meta) {
    return setBit(position, !isBitSet(position, meta), meta);
  }

}
