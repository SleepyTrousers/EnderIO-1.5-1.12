package crazypants.enderio.base.filter.gui;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.client.gui.button.CycleButton.ICycleEnum;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.gui.IconEIO;
import net.minecraft.util.math.MathHelper;
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
    return Collections.singletonList(EnderIO.lang.localize("gui.item_filter.damage.".concat(name().toLowerCase(Locale.ENGLISH))));
  }

  public @Nonnull DamageMode getMode() {
    return NullHelper.first(DamageMode.values()[MathHelper.clamp(this.ordinal(), 0, DamageMode.values().length - 1)], DamageMode.DISABLED);
  }

  public static @Nonnull DamageModeIconHolder getFromMode(DamageMode mode) {
    if (mode != null) {
      return NullHelper.first(values()[MathHelper.clamp(mode.ordinal(), 0, values().length - 1)], DISABLED);
    }
    return DISABLED;
  }

}