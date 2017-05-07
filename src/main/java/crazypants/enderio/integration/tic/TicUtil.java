package crazypants.enderio.integration.tic;

import crazypants.enderio.machine.farm.FarmersRegistry;

public class TicUtil {

  private TicUtil() {
  }

  public static void addTic() {
    FarmersRegistry.registerLogs("blockSlimeCongealed");
  }

}
