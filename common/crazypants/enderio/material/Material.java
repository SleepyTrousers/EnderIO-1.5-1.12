package crazypants.enderio.material;

public enum Material {

  SILICON("silicon", "Silicon", "silicon"),
  CONDUIT_BINDER("conduitBinder", "Conduit Binder", "conduitBinder"),
  BINDER_COMPOSITE("binderComposite", "Binder Composite", "binderComposite"),
  PHASED_IRON_NUGGET("phasedIronNugget", "Pulsating Iron Nugget", "phasedIronNugget");

  public final String unlocalisedName;
  public final String uiName;
  public final String iconKey;

  private Material(String unlocalisedName, String uiName, String iconKey) {
    this.unlocalisedName = unlocalisedName;
    this.uiName = uiName;
    this.iconKey = "enderio:" + iconKey;
  }

}
