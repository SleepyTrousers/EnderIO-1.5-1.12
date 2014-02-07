package crazypants.enderio.material;

public enum Alloy {

  ELECTRICAL_STEEL("electricalSteel"),
  ENERGETIC_ALLOY("energeticAlloy"),
  PHASED_GOLD("phasedGold"),
  REDSTONE_ALLOY("redstoneAlloy"),
  CONDUCTIVE_IRON("conductiveIron"),
  PHASED_IRON("phasedIron");

  public final String unlocalisedName;
  public final String iconKey;

  private Alloy(String iconKey) {
    this.unlocalisedName = "enderio." + iconKey;
    this.iconKey = "enderio:" + iconKey;
  }

}
