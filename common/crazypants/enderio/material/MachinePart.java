package crazypants.enderio.material;

public enum MachinePart {

  MACHINE_CHASSI("machineChassi","Machine Chassi","machineChassi"),
  BASIC_GEAR("basicGear", "Basic Gear", "basicGear"),
  REDSTONE_INDUCTOR("redstoneInductor", "Redstone Inductor", "redstoneInductor");
  
  public final String unlocalisedName;
  public final String uiName;
  public final String iconKey;
    
  private MachinePart(String unlocalisedName, String uiName, String iconKey) {
    this.unlocalisedName = unlocalisedName;
    this.uiName = uiName;
    this.iconKey = "enderio:" + iconKey;
  }
  
  
}
