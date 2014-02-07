package crazypants.enderio.material;

public enum MachinePart {

  MACHINE_CHASSI("machineChassi"),
  BASIC_GEAR("basicGear");

  public final String unlocalisedName;
  public final String iconKey;

  private MachinePart(String unlocalisedName) {
    this.unlocalisedName = "enderio." + unlocalisedName;
    this.iconKey = "enderio:" + unlocalisedName;
  }

}
