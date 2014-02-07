package crazypants.enderio.machine;

import net.minecraft.tileentity.TileEntity;
import crazypants.util.Lang;

public enum RedstoneControlMode {

  IGNORE,
  ON,
  OFF,
  NEVER;

  RedstoneControlMode() {
  }

  public String getTooltip() {
    return Lang.localize("gui.tooltip.redstoneControlMode." + name().toLowerCase());
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
