package crazypants.enderio.integration.botany;

import crazypants.enderio.farming.FarmersRegistry;

public class BotanyUtil {

  private BotanyUtil() {
  }

  public static void addBotany() {
    FarmersRegistry.registerFlower("block:botany:flower");
  }

}
