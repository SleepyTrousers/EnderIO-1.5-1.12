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

  public static boolean isConditionMet(RedstoneControlMode redstoneControlMode, int powerLevel) {
    boolean redstoneCheckPassed = true;
    if(redstoneControlMode == RedstoneControlMode.NEVER) {
      redstoneCheckPassed = false;
    } else if(redstoneControlMode == RedstoneControlMode.ON) {
      if(powerLevel < 1) {
        redstoneCheckPassed = false;
      }
    } else if(redstoneControlMode == RedstoneControlMode.OFF) {
      if(powerLevel > 0) {
        redstoneCheckPassed = false;
      }
    }
    return redstoneCheckPassed;
  }

  public static boolean isConditionMet(RedstoneControlMode redstoneControlMode, TileEntity te) {
    return isConditionMet(redstoneControlMode, te.worldObj.getStrongestIndirectPower(te.xCoord, te.yCoord, te.zCoord));
  }

  public RedstoneControlMode next() {
    int ord = ordinal();
    if(ord == values().length - 1) {
      ord = 0;
    } else {
      ord++;
    }
    return values()[ord];
  }

}
