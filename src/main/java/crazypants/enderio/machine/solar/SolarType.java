package crazypants.enderio.machine.solar;

import java.util.Locale;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;
import crazypants.enderio.config.Config;

public enum SolarType implements IStringSerializable {

  SIMPLE(""),
  ADVANCED(".advanced"),
  VIBRANT(".vibrant");

  public static final PropertyEnum<SolarType> KIND = PropertyEnum.<SolarType> create("kind", SolarType.class);

  private final String unlocalisedName;

  private SolarType(String unlocalisedName) {
    this.unlocalisedName = unlocalisedName;
  }

  public boolean connectTo(SolarType other) {
    return this == other;
  }

  @Override
  public String getName() {
    return name().toLowerCase(Locale.ENGLISH);
  }

  public static SolarType getTypeFromMeta(int meta) {
    return values()[meta >= 0 && meta < values().length ? meta : 0];
  }

  public static int getMetaFromType(SolarType fusedQuartzType) {
    return fusedQuartzType.ordinal();
  }

  public String getUnlocalisedName() {
    return unlocalisedName;
  }

  public int getRfperTick() {
    switch (this) {
    case ADVANCED:
      return Config.maxPhotovoltaicAdvancedOutputRF;
    case SIMPLE:
      return Config.maxPhotovoltaicOutputRF;
    case VIBRANT:
      return Config.maxPhotovoltaicVibrantOutputRF;
    default:
      return 0;
    }
  }
}