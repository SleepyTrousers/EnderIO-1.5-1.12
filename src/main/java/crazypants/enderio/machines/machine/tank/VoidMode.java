package crazypants.enderio.machines.machine.tank;

import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.client.gui.button.CycleButton.ICycleEnum;
import com.google.common.collect.Lists;

import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.machines.EnderIOMachines;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum VoidMode {
  ALWAYS,
  IF_NOT_CONTAINER,
  NEVER;

  @SideOnly(Side.CLIENT)
  public static enum IconHolder implements ICycleEnum {
    ALWAYS_ICON(VoidMode.ALWAYS, IconEIO.TICK),
    IF_NOT_CONTAINER_ICON(VoidMode.IF_NOT_CONTAINER, IconEIO.ITEM_SINGLE),
    NEVER_ICON(VoidMode.NEVER, IconEIO.CROSS);

    private final @Nonnull VoidMode mode;
    private final @Nonnull IWidgetIcon icon;
    private final @Nonnull List<String> unlocTooltips;

    IconHolder(@Nonnull VoidMode mode, @Nonnull IWidgetIcon icon) {
      this.mode = mode;
      this.icon = icon;
      String prefix = "gui.tank.void.mode";
      String str = prefix + "." + name().replace("_ICON", "").toLowerCase(Locale.US);
      this.unlocTooltips = Lists.newArrayList(prefix, str, str + ".desc");
      // e.g.:
      // * enderio.gui.tank.void.mode
      // * enderio.gui.tank.void.mode.always
      // * enderio.gui.tank.void.mode.always.desc
    }

    @Override
    public @Nonnull IWidgetIcon getIcon() {
      return icon;
    }

    @Override
    public @Nonnull List<String> getTooltipLines() {
      return EnderIOMachines.lang.localizeAll(unlocTooltips);
    }

    public @Nonnull VoidMode getMode() {
      return mode;
    }

    public static @Nonnull IconHolder getFromMode(@Nonnull VoidMode mode) {
      for (IconHolder holder : values()) {
        if (holder.mode == mode) {
          return holder;
        }
      }
      return NEVER_ICON;
    }

  }
}