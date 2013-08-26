package crazypants.enderio.machine;

public enum RedstoneControlMode {

  IGNORE("Always active."),
  ON("Active with signal."),
  OFF("Active without signal.");

  public final String tooltip;
  
  RedstoneControlMode(String tooltip) {
    this.tooltip = tooltip;
  }
  
}
