package crazypants.enderio;

import net.minecraftforge.common.Configuration;

public final class Config {

  static int BID = 700;
  static int IID = 4200;

  public static void load(Configuration config) {
    for (ModObject e : ModObject.values()) {
      e.load(config);
    }
  }

  private Config() {
  }

}
