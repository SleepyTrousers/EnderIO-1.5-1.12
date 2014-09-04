package crazypants.enderio.material;

public enum FrankenSkull {

  ZOMBIE_ELECTRODE("skullZombieElectrode"),
  ZOMBIE_CONTROLLER("skullZombieController");
  
  public final String unlocalisedName;
  public final String iconKey;
  
  private FrankenSkull(String unlocalisedName) {
    this.unlocalisedName = unlocalisedName;
    this.iconKey = "enderio:" + unlocalisedName;
  }

  public String getUnlocalisedName() {
    return unlocalisedName;
  }

  public String getIconKey() {
    return iconKey;
  }
  
}
