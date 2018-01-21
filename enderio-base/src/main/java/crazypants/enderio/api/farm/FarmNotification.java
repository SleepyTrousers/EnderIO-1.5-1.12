package crazypants.enderio.api.farm;

import javax.annotation.Nonnull;

import crazypants.enderio.api.ILocalizable;

public enum FarmNotification implements ILocalizable {

  OUTPUT_FULL("outputFull"),
  NO_SEEDS("noSeeds"),
  NO_AXE("noAxe"),
  NO_HOE("noHoe"),
  NO_TREETAP("noTreetap"),
  NO_POWER("noPower"),
  NO_SHEARS("noShears");

  private final @Nonnull String langStr;

  private FarmNotification(@Nonnull String langStr) {
    this.langStr = "enderio.farm.note." + langStr;
  }

  @Override
  public @Nonnull String getUnlocalizedName() {
    return langStr;
  }

}
