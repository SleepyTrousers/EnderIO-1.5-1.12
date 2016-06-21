package crazypants.enderio.machine;

import java.util.List;
import java.util.Locale;

import net.minecraft.tileentity.TileEntity;

import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.client.gui.button.CycleButton.ICycleEnum;
import com.google.common.collect.Lists;

import crazypants.enderio.EnderIO;
import crazypants.enderio.gui.IconEIO;

public enum RedstoneControlMode implements ICycleEnum {

  IGNORE(IconEIO.REDSTONE_MODE_ALWAYS),
  ON(IconEIO.REDSTONE_MODE_WITH_SIGNAL),
  OFF(IconEIO.REDSTONE_MODE_WITHOUT_SIGNAL),
  NEVER(IconEIO.REDSTONE_MODE_NEVER);


  private IWidgetIcon icon;
  
  RedstoneControlMode(IWidgetIcon icon) {
    this.icon = icon;
  }

  public String getTooltip() {
    return EnderIO.lang.localize("gui.tooltip.redstoneControlMode." + name().toLowerCase(Locale.US));
  }
  
  @Override
  public IWidgetIcon getIcon() {
    return icon;
  }
  
  @Override
  public List<String> getTooltipLines() {
    return Lists.newArrayList(getTooltip());
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
    return isConditionMet(redstoneControlMode, te.getWorldObj().getStrongestIndirectPower(te.xCoord, te.yCoord, te.zCoord));
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

  public RedstoneControlMode previous() {
    int ord = ordinal();
    ord--;
    if(ord < 0) {
      ord = values().length - 1;
    } 
    return values()[ord];    
  }

}
