package crazypants.enderio.machine.modes;

import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.client.gui.button.CycleButton.ICycleEnum;
import com.enderio.core.common.util.NNList;
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

  /**
   * This is the highest level that will make a difference. Meaning, that if it is known that the input level is at least MIN_ON_LEVEL, then there is no need to
   * run any checks for a higher level.
   */
  public static final int MIN_ON_LEVEL = 1;

  @SuppressWarnings("hiding")
  public static enum IconHolder implements ICycleEnum {

    IGNORE(RedstoneControlMode.IGNORE, IconEIO.REDSTONE_MODE_ALWAYS),
    ON(RedstoneControlMode.ON, IconEIO.REDSTONE_MODE_WITH_SIGNAL),
    OFF(RedstoneControlMode.OFF, IconEIO.REDSTONE_MODE_WITHOUT_SIGNAL),
    NEVER(RedstoneControlMode.NEVER, IconEIO.REDSTONE_MODE_NEVER);

    private final @Nonnull RedstoneControlMode mode;
    private final @Nonnull IWidgetIcon icon;

    IconHolder(@Nonnull RedstoneControlMode mode, @Nonnull IWidgetIcon icon) {
      this.mode = mode;
      this.icon = icon;
    }

    public @Nonnull String getTooltip() {
      return EnderIO.lang.localize("gui.tooltip.redstoneControlMode." + name().toLowerCase(Locale.US));
    }

    @Override
    public @Nonnull IWidgetIcon getIcon() {
      return icon;
    }

    @Override
    public @Nonnull List<String> getTooltipLines() {
      return Lists.newArrayList(getTooltip());
    }

    public @Nonnull RedstoneControlMode getMode() {
      return mode;
    }

    public static @Nonnull IconHolder getFromMode(@Nonnull RedstoneControlMode mode) {
      for (IconHolder holder : values()) {
        if (holder.mode == mode) {
          return holder;
        }
      }
      return IGNORE;
    }

  }

  public static boolean isConditionMet(@Nonnull RedstoneControlMode redstoneControlMode, int powerLevel) {
    switch (redstoneControlMode) {
    case IGNORE:
      return true;
    case NEVER:
      return false;
    case OFF:
      return powerLevel == 0;
    case ON:
      return powerLevel > 0;
    default:
      return false;
    }
  }

  public static boolean isConditionMet(@Nonnull RedstoneControlMode redstoneControlMode, TileEntity te) {
    switch (redstoneControlMode) {
    case IGNORE:
      return true;
    case NEVER:
      return false;
    default:
      return isConditionMet(redstoneControlMode, ConduitUtil.isBlockIndirectlyGettingPoweredIfLoaded(te.getWorld(), te.getPos()));
    }
  }

  public @Nonnull RedstoneControlMode next() {
    return NNList.<RedstoneControlMode> of(RedstoneControlMode.class).next(this);
  }

  public @Nonnull RedstoneControlMode previous() {
    return NNList.<RedstoneControlMode> of(RedstoneControlMode.class).prev(this);
  }

}
