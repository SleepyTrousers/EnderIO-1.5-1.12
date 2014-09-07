package crazypants.enderio.material;

public enum Material {

  SILICON("silicon"),
  CONDUIT_BINDER("conduitBinder"),
  BINDER_COMPOSITE("binderComposite"),
  PHASED_IRON_NUGGET("phasedIronNugget"),
  VIBRANT_NUGGET("vibrantNugget"),
  PULSATING_CYSTAL("pulsatingCrystal", true),
  VIBRANT_CYSTAL("vibrantCrystal", true),
  DRAK_GRINDING_BALL("darkGrindingBall"),
  ENDER_CRYSTAL("enderCrystal", true);

  public final String unlocalisedName;
  public final String iconKey;
  public final boolean hasEffect;

  private Material(String unlocalisedName) {
    this(unlocalisedName, false);
  }
  
  private Material(String unlocalisedName, boolean hasEffect) {
    this.unlocalisedName = "enderio." + unlocalisedName;
    this.iconKey = "enderio:" + unlocalisedName;
    this.hasEffect = hasEffect;
  }

}
