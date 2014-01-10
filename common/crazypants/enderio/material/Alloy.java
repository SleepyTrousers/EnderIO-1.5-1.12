package crazypants.enderio.material;

public enum Alloy {

  ELECTRICAL_STEEL("Electrical Steel", "electricalSteel"),
  ENERGETIC_ALLOY("Energetic Alloy", "energeticAlloy"),
  PHASED_GOLD("Vibrant Alloy", "phasedGold"),
  REDSTONE_ALLOY("Redstone Alloy", "redstoneAlloy"),
  CONDUCTIVE_IRON("Conductive Iron", "conductiveIron"),
  PHASED_IRON("Pulsating Iron", "phasedIron");

  public final String unlocalisedName;
  public final String uiName;
  public final String iconKey;

  private Alloy(String uiName, String iconKey) {
    this.unlocalisedName = name();
    this.uiName = uiName;
    this.iconKey = "enderio:" + iconKey;
  }

}
