package crazypants.enderio.material;

import crazypants.util.Lang;

public enum Material {

  SILICON("silicon"),
  CONDUIT_BINDER("conduitBinder"),
  BINDER_COMPOSITE("binderComposite"),
  PHASED_IRON_NUGGET("phasedIronNugget");

  public final String unlocalisedName;
  public final String uiName;
  public final String iconKey;

  private Material(String unlocalisedName) {
    this.unlocalisedName = unlocalisedName;
    this.uiName = Lang.localize(unlocalisedName);
    this.iconKey = "enderio:" + unlocalisedName;
  }

}
