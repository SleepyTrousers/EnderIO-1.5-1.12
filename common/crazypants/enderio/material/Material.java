package crazypants.enderio.material;

public enum Material {

  SILICON("silicon"),
  CONDUIT_BINDER("conduitBinder"),
  BINDER_COMPOSITE("binderComposite"),
  PHASED_IRON_NUGGET("phasedIronNugget");

  public final String unlocalisedName;
  public final String iconKey;

  private Material(String unlocalisedName) {
    this.unlocalisedName = "enderio." + unlocalisedName;
    this.iconKey = "enderio:" + unlocalisedName;
  }

}
