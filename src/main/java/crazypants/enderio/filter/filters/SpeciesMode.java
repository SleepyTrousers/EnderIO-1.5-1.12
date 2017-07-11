package crazypants.enderio.filter.filters;

import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.client.gui.button.CycleButton;
import crazypants.enderio.EnderIO;
import crazypants.enderio.gui.IconEIO;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public enum SpeciesMode {
  BOTH,
  PRIMARY,
  SECONDARY;

  public SpeciesMode next() {
    SpeciesMode[] values = values();
    return values[(ordinal() + 1) % values.length];
  }

  @SideOnly(Side.CLIENT)
  public enum IconHolder implements CycleButton.ICycleEnum {
    BOTH(SpeciesMode.BOTH, IconEIO.FILTER_SPECIES_BOTH),
    PRIMARY(SpeciesMode.PRIMARY, IconEIO.FILTER_SPECIES_PRIMARY),
    SECONDARY(SpeciesMode.SECONDARY, IconEIO.FILTER_SPECIES_SECONDARY);

    private final SpeciesMode mode;
    private final IWidgetIcon icon;

    IconHolder(SpeciesMode mode, IWidgetIcon icon) {
      this.mode = mode;
      this.icon = icon;
    }

    @Override
    public IWidgetIcon getIcon() {
      return icon;
    }

    @Override
    public List<String> getTooltipLines() {
      return Collections.singletonList(EnderIO.lang.localize("gui.conduit.item.species.".concat(name().toLowerCase(Locale.US))));
    }

    public SpeciesMode getMode() {
      return mode;
    }

    public static SpeciesMode.IconHolder getFromMode(SpeciesMode mode) {
      for (SpeciesMode.IconHolder holder : values()) {
        if (holder.mode == mode) {
          return holder;
        }
      }
      return BOTH;
    }

  }
}
