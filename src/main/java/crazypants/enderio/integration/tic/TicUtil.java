package crazypants.enderio.integration.tic;

import crazypants.enderio.farming.FarmersRegistry;

public class TicUtil {

  private TicUtil() {
  }

  public static void addTic() {
    FarmersRegistry.registerLogs("blockSlimeCongealed"); // oreDict
  }

}
