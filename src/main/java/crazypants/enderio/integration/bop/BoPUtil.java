package crazypants.enderio.integration.bop;

import crazypants.enderio.machine.farm.FarmersRegistry;

public class BoPUtil {

  private BoPUtil() {
  }

  public static void addBoP() {
    FarmersRegistry.registerFlower("block:biomesoplenty:flowers", "block:biomesoplenty:flowers2");
  }

}
