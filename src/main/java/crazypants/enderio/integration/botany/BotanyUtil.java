package crazypants.enderio.integration.botany;

import crazypants.enderio.machine.farm.FarmersRegistry;

public class BotanyUtil {

  private BotanyUtil() {
  }

  public static void addBotany() {
    FarmersRegistry.registerFlower("block:Botany:flower");
  }

}
