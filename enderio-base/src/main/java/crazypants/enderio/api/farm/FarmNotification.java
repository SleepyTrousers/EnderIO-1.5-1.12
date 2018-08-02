package crazypants.enderio.api.farm;

import javax.annotation.Nonnull;

import crazypants.enderio.api.ILocalizable;

public enum FarmNotification implements ILocalizable {

  OUTPUT_FULL("outputFull"),
  NO_SEEDS("noSeeds", true),
  NO_AXE("noAxe", true),
  NO_HOE("noHoe", true),
  NO_TREETAP("noTreetap", true),
  NO_POWER("noPower"),
  NO_SHEARS("noShears", true),
  NO_CAP("noCapacitor");

  private final @Nonnull String langStr;
  private final boolean autoCleanup;

  private FarmNotification(@Nonnull String langStr) {
    this(langStr, false);
  }

  private FarmNotification(@Nonnull String langStr, boolean autoCleanup) {
    this.langStr = "enderio.farm.note." + langStr;
    this.autoCleanup = autoCleanup;
  }

  @Override
  public @Nonnull String getUnlocalizedName() {
    return langStr;
  }

  public boolean isAutoCleanup() {
    return autoCleanup;
  }

}
