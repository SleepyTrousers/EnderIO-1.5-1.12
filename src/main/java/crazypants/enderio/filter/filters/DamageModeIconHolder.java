package crazypants.enderio.filter.filters;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.client.gui.button.CycleButton.ICycleEnum;

import crazypants.enderio.EnderIO;
import crazypants.enderio.gui.IconEIO;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum DamageModeIconHolder implements ICycleEnum {
  DISABLED(IconEIO.FILTER_DAMAGE_OFF),
  DAMAGE_00_25(IconEIO.FILTER_DAMAGE_00_25),
  DAMAGE_25_00(IconEIO.FILTER_DAMAGE_25_00),
  DAMAGE_00_50(IconEIO.FILTER_DAMAGE_00_50),
  DAMAGE_50_00(IconEIO.FILTER_DAMAGE_50_00),
  DAMAGE_00_75(IconEIO.FILTER_DAMAGE_00_75),
  DAMAGE_75_00(IconEIO.FILTER_DAMAGE_75_00),
  DAMAGE_00_00(IconEIO.FILTER_DAMAGE_00_00),
  DAMAGE_01_00(IconEIO.FILTER_DAMAGE_01_00),
  DAMAGE_YES(IconEIO.FILTER_DAMAGE_YES),
  DAMAGE_NOT(IconEIO.FILTER_DAMAGE_NOT);

  private final @Nonnull IWidgetIcon icon;

  DamageModeIconHolder(@Nonnull IWidgetIcon icon) {
    this.icon = icon;
  }

  @Override
  public @Nonnull IWidgetIcon getIcon() {
    return icon;
  }

  @Override
  public @Nonnull List<String> getTooltipLines() {
    return Collections.singletonList(EnderIO.lang.localize("gui.conduit.item.damage.".concat(name().toLowerCase(Locale.ENGLISH))));
  }

  public DamageMode getMode() {
    return DamageMode.values()[this.ordinal()];
  }

  public static DamageModeIconHolder getFromMode(DamageMode mode) {
    if (mode != null) {
      return values()[mode.ordinal()];
    }
    return DISABLED;
  }

}