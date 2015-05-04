package crazypants.enderio.material;

public enum MachinePart {

  MACHINE_CHASSI("machineChassi", true, false, false, null, false, false), //
  BASIC_GEAR("basicGear", false, false, false, null, false, false), //
  MACHINE_FRAME("machineFrame", true, true, true, null, false, false), //
  FRAME_TANK("frameTank", true, true, false, null, false, true), //
  FRAME_TANKS("frameTanks", true, true, false, null, true, false), //
  MACHINE_FRAME_TANK("machineFrameTank", true, true, true, null, true, false); //

  public final String unlocalisedName;
  public final String iconKey;
  public final boolean render3d;
  public final boolean renderAsFrameMachine;
  public final boolean hasFrame;
  public final String controllerModelName;
  public final boolean hasTanks;
  public final boolean hasSingleTank;

  private MachinePart(String unlocalisedName, boolean render3d, boolean renderAsFrameMachine, boolean hasFrame,
      String controllerModelName, boolean hasTanks, boolean hasSingleTank) {
    this.unlocalisedName = "enderio." + unlocalisedName;
    this.iconKey = "enderio:" + unlocalisedName;
    this.render3d = render3d;
    this.renderAsFrameMachine = renderAsFrameMachine;
    this.hasFrame = hasFrame;
    this.controllerModelName = controllerModelName;
    this.hasTanks = hasTanks;
    this.hasSingleTank = hasSingleTank;
  }

  public String getControllerModelName() {
    return controllerModelName;
  }

}
