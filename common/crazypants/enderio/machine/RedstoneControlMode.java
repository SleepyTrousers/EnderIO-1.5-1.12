package crazypants.enderio.machine;

import net.minecraft.tileentity.TileEntity;

public enum RedstoneControlMode {

  IGNORE("Always active."),
  ON("Active with signal."),
  OFF("Active without signal."),
  NEVER("Never active.");

  public final String tooltip;
  
  RedstoneControlMode(String tooltip) {
    this.tooltip = tooltip;
  }
  
  public static boolean isConditionMet(RedstoneControlMode redstoneControlMode, TileEntity te) {
    boolean redstoneCheckPassed = true;
    if (redstoneControlMode == RedstoneControlMode.NEVER) {      
      redstoneCheckPassed = false;      
    } else if (redstoneControlMode == RedstoneControlMode.ON) {
      int powerLevel = te.worldObj.getStrongestIndirectPower(te.xCoord, te.yCoord, te.zCoord);
      if (powerLevel < 1) {
        redstoneCheckPassed = false;
      }
    } else if (redstoneControlMode == RedstoneControlMode.OFF) {
      int powerLevel = te.worldObj.getStrongestIndirectPower(te.xCoord, te.yCoord, te.zCoord);
      if (powerLevel > 0) {
        redstoneCheckPassed = false;
      }
    }
    return redstoneCheckPassed;
  }
  
}
