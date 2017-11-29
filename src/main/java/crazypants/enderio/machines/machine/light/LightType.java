package crazypants.enderio.machines.machine.light;

import java.util.Locale;

import net.minecraft.util.IStringSerializable;

public enum LightType implements IStringSerializable {
  
  ELECTRIC("item.itemElectricLight", false, true, false),
  ELECTRIC_INV("item.itemElectricLightInverted", true, true, false),
  BASIC("item.itemLight", false, false, false),
  BASIC_INV("item.itemLightInverted", true, false, false),
  WIRELESS("item.itemWirelessLight", false, true, true),
  WIRELESS_INV("item.itemWirelessLightInverted", true, true, true);

  final String unlocName;
  final boolean isInverted;
  final boolean isPowered;
  final boolean isWireless;

  private LightType(String unlocName, boolean isInverted, boolean isPowered, boolean isWireless) {
    this.unlocName = unlocName;
    this.isInverted = isInverted;
    this.isPowered = isPowered;
    this.isWireless = isWireless;
  }
  
  @Override
  public String getName() {
    return name().toLowerCase(Locale.US);
  }

  public int getMetadata() {
    return ordinal();
  }

  public static LightType fromMetadata(int meta) {
    return values()[meta >= 0 && meta < values().length ? meta : 0];
  }


}
