package crazypants.enderio.machine;

import java.util.List;
import java.util.Locale;

import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.client.gui.button.CycleButton.ICycleEnum;
import com.google.common.collect.Lists;

import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.gui.IconEIO;
import net.minecraft.tileentity.TileEntity;

public enum RedstoneControlMode {
  IGNORE,
  ON,
  OFF,
  NEVER;

  // @SideOnly(Side.CLIENT)
  @SuppressWarnings("hiding")
  public static enum IconHolder implements ICycleEnum {

    IGNORE(RedstoneControlMode.IGNORE, IconEIO.REDSTONE_MODE_ALWAYS),
    ON(RedstoneControlMode.ON, IconEIO.REDSTONE_MODE_WITH_SIGNAL),
    OFF(RedstoneControlMode.OFF, IconEIO.REDSTONE_MODE_WITHOUT_SIGNAL),
    NEVER(RedstoneControlMode.NEVER, IconEIO.REDSTONE_MODE_NEVER);

    private final RedstoneControlMode mode;
    private final IWidgetIcon icon;

    IconHolder(RedstoneControlMode mode, IWidgetIcon icon) {
      this.mode = mode;
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

    public RedstoneControlMode getMode() {
      return mode;
    }

    public static IconHolder getFromMode(RedstoneControlMode mode) {
      for (IconHolder holder : values()) {
        if (holder.mode == mode) {
          return holder;
        }
      }
      return IGNORE;
    }

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
    return isConditionMet(redstoneControlMode, ConduitUtil.isBlockIndirectlyGettingPoweredIfLoaded(te.getWorld(), te.getPos()));
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
