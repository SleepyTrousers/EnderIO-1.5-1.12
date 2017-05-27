package crazypants.enderio.farming;

import javax.annotation.Nonnull;

import crazypants.enderio.EnderIO;

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
    this.langStr = langStr;
  }

  public @Nonnull String getDisplayString() {
    return EnderIO.lang.localize("farm.note." + langStr);
  }

}
