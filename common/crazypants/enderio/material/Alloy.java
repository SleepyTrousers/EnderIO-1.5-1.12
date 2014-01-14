package crazypants.enderio.material;

import crazypants.util.Lang;

public enum Alloy {

  ELECTRICAL_STEEL("electricalSteel"),
  ENERGETIC_ALLOY("energeticAlloy"),
  PHASED_GOLD("phasedGold"),
  REDSTONE_ALLOY("redstoneAlloy"),
  CONDUCTIVE_IRON("conductiveIron"),
  PHASED_IRON("phasedIron");

  public final String unlocalisedName;
  public final String uiName;
  public final String iconKey;

  private Alloy(String iconKey) {
    this.unlocalisedName = name();
    this.uiName = Lang.localize(iconKey);
    this.iconKey = "enderio:" + iconKey;
  }

}
