package crazypants.enderio.machines.machine.light;

import java.util.Locale;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import net.minecraft.util.IStringSerializable;

public enum LightType implements IStringSerializable {

  ELECTRIC(".electric", false, true, false),
  ELECTRIC_INV(".electric.inverted", true, true, false),
  BASIC("", false, false, false),
  BASIC_INV(".inverted", true, false, false),
  WIRELESS(".wireless", false, true, true),
  WIRELESS_INV(".wireless.inverted", true, true, true);

  final @Nonnull private String unlocName;
  private final boolean isInverted;
  private final boolean isPowered;
  private final boolean isWireless;

  private LightType(@Nonnull String unlocName, boolean isInverted, boolean isPowered, boolean isWireless) {
    this.unlocName = unlocName;
    this.isInverted = isInverted;
    this.isPowered = isPowered;
    this.isWireless = isWireless;
  }

  @Override
  public @Nonnull String getName() {
    return name().toLowerCase(Locale.US);
  }

  public int getMetadata() {
    return ordinal();
  }

  public static @Nonnull LightType fromMetadata(int meta) {
    return NullHelper.notnullJ(values()[meta >= 0 && meta < values().length ? meta : 0], "Enum.values()");
  }

  public @Nonnull String getUnlocalizedSuffix() {
    return unlocName;
  }

  public boolean isInverted() {
    return isInverted;
  }

  public boolean isPowered() {
    return isPowered;
  }

  public boolean isWireless() {
    return isWireless;
  }

}
