package crazypants.enderio.machines.machine.tank;

import java.util.List;
import java.util.Locale;

import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.client.gui.button.CycleButton.ICycleEnum;
import com.google.common.collect.Lists;

import crazypants.enderio.EnderIO;
import crazypants.enderio.gui.IconEIO;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum VoidMode {
  ALWAYS,
  IF_NOT_CONTAINER,
  NEVER;

  @SideOnly(Side.CLIENT)
  public static enum IconHolder implements ICycleEnum {
    ALWAYS(VoidMode.ALWAYS, IconEIO.TICK),
    IF_NOT_CONTAINER(VoidMode.IF_NOT_CONTAINER, IconEIO.ITEM_SINGLE),
    NEVER(VoidMode.NEVER, IconEIO.CROSS);

    private final VoidMode mode;
    private final IWidgetIcon icon;
    private final List<String> unlocTooltips;

    IconHolder(VoidMode mode, IWidgetIcon icon) {
      this.mode = mode;
      this.icon = icon;
      String prefix = "gui.void.mode";
      String str = prefix + "." + name().toLowerCase(Locale.US);
      this.unlocTooltips = Lists.newArrayList(prefix, str, str + ".desc");
    }

    @Override
    public IWidgetIcon getIcon() {
      return icon;
    }

    @Override
    public List<String> getTooltipLines() {
      return EnderIO.lang.localizeAll(unlocTooltips);
    }

    public VoidMode getMode() {
      return mode;
    }

    public static IconHolder getFromMode(VoidMode mode) {
      for (IconHolder holder : values()) {
        if (holder.mode == mode) {
          return holder;
        }
      }
      return NEVER;
    }

  }
}