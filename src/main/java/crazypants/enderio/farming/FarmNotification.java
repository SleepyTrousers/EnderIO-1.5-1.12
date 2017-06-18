package crazypants.enderio.farming;

import javax.annotation.Nonnull;

import crazypants.enderio.EnderIO;
import crazypants.enderio.Log;

public enum FarmNotification {

  OUTPUT_FULL("outputFull"),
  NO_SEEDS("noSeeds"),
  NO_AXE("noAxe"),
  NO_HOE("noHoe"),
  NO_TREETAP("noTreetap"),
  NO_POWER("noPower"),
  NO_SHEARS("noShears");

  private final @Nonnull String langStr;

  private FarmNotification(@Nonnull String langStr) {
    this.langStr = "farm.note." + langStr;
  }

  public @Nonnull String getDisplayString() {
    return EnderIO.lang.localize(langStr);
  }

  static {
    for (FarmNotification lang : values()) {
      if (!EnderIO.lang.canLocalize(lang.langStr)) {
        Log.error("Missing translation for '" + lang.langStr);
      }
    }
  }

}
