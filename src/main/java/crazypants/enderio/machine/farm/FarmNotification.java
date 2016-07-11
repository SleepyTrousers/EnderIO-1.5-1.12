package crazypants.enderio.machine.farm;

import crazypants.enderio.EnderIO;

public enum FarmNotification {

  OUTPUT_FULL("outputFull"),
  NO_SEEDS("noSeeds"),
  NO_AXE("noAxe"),
  NO_HOE("noHoe"),
  NO_TREETAP("noTreetap"),
  NO_POWER("noPower"),
  NO_SHEARS("noShears");

  private final String langStr;

  private FarmNotification(String langStr) {
    this.langStr = langStr;
  }

  public String getDisplayString() {
    return EnderIO.lang.localize("farm.note." + langStr);
  }

}
