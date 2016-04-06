package crazypants.enderio.capacitor;

import java.util.Locale;

import net.minecraftforge.common.config.Configuration;
import crazypants.enderio.EnderIO;
import crazypants.enderio.Log;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config.Section;

import static crazypants.enderio.config.Config.sectionAttractor;
import static crazypants.enderio.config.Config.sectionCapacitor;

public enum CapacitorKey implements ICapacitorKey {
  ALLOY_SMELTER_POWER_INTAKE(ModObject.blockAlloySmelter, CapacitorKeyType.ENERGY_INTAKE, Scaler.POWER, 80),
  ALLOY_SMELTER_POWER_BUFFER(ModObject.blockAlloySmelter, CapacitorKeyType.ENERGY_BUFFER, Scaler.POWER, 100000),
  ALLOY_SMELTER_POWER_USE(ModObject.blockAlloySmelter, CapacitorKeyType.ENERGY_USE, Scaler.POWER, 20),

  BUFFER_POWER_INTAKE(ModObject.blockBuffer, CapacitorKeyType.ENERGY_INTAKE, Scaler.POWER, 80),
  BUFFER_POWER_BUFFER(ModObject.blockBuffer, CapacitorKeyType.ENERGY_BUFFER, Scaler.POWER, 100000),

  CRAFTER_POWER_INTAKE(ModObject.blockCrafter, CapacitorKeyType.ENERGY_INTAKE, Scaler.POWER10, 500),
  CRAFTER_POWER_BUFFER(ModObject.blockCrafter, CapacitorKeyType.ENERGY_BUFFER, Scaler.POWER, 100000),
  CRAFTER_TICKS(ModObject.blockCrafter, CapacitorKeyType.SPEED, Scaler.SPEED, 1),

  ATTRACTOR_POWER_INTAKE(ModObject.blockAttractor, CapacitorKeyType.ENERGY_INTAKE, Scaler.QUADRATIC, 80),
  ATTRACTOR_POWER_BUFFER(ModObject.blockAttractor, CapacitorKeyType.ENERGY_BUFFER, Scaler.POWER, 100000),
  ATTRACTOR_POWER_USE(ModObject.blockAttractor, CapacitorKeyType.ENERGY_USE, Scaler.QUADRATIC, 20),
  ATTRACTOR_RANGE(ModObject.blockAttractor, CapacitorKeyType.AREA, Scaler.QUADRATIC, 16, sectionAttractor, null),

  LEGACY_ENERGY_INTAKE(ModObject.itemBasicCapacitor, CapacitorKeyType.ENERGY_INTAKE, Scaler.POWER, 80, null, null),
  LEGACY_ENERGY_BUFFER(ModObject.itemBasicCapacitor, CapacitorKeyType.ENERGY_BUFFER, Scaler.POWER, 100000, null, null),
  LEGACY_ENERGY_USE(ModObject.itemBasicCapacitor, CapacitorKeyType.ENERGY_USE, Scaler.POWER, 20, null, null),

  //
  ;
  
  // /////////////////////////////////////////////////////////////////// //
  // /////////////////////////////////////////////////////////////////// //
  // /////////////////////////////////////////////////////////////////// //

  private final ModObject owner;
  private final CapacitorKeyType valueType;
  private final Scaler scaler;
  private final String configKey;
  private final Section configSection;
  private final String configComment;
  private final int defaultBaseValue;
  private int baseValue;

  private CapacitorKey(ModObject owner, CapacitorKeyType valueType, Scaler scaler, int defaultBaseValue) {
    this(owner, valueType, scaler, defaultBaseValue, sectionCapacitor, null);
  }

  private CapacitorKey(ModObject owner, CapacitorKeyType valueType, Scaler scaler, int defaultBaseValue, Section configSection, String configKey) {
    this.owner = owner;
    this.valueType = valueType;
    this.scaler = scaler;
    this.configKey = configKey == null ? name().toLowerCase(Locale.US) : configKey;
    this.configSection = configSection;
    this.configComment = localizeComment(this.configSection, this.configKey);
    this.baseValue = this.defaultBaseValue = defaultBaseValue;
  }

  private static String localizeComment(Section configSection, String configKey) {
    if (configSection == null || configKey == null) {
      return null;
    } else {
      final String langKey = "config.capacitor." + configKey;
      if (!EnderIO.lang.canLocalize(langKey)) {
        Log.warn("Missing translation: " + langKey);
      }
      return EnderIO.lang.localize(langKey);
    }
  }

  /**
   * Calculates the value to use for the given capacitor. Calculation is:
   * <p>
   * <tt>final value = base value * scaler(capacitor level)</tt>
   * <p>
   * The capacitor level is a 1, 2 and 3 for the basic capacitors. Custom ones may have any non-zero, positive level. The scalers are expected to only map to
   * halfway reasonable output levels. Capacitors can choose to report different levels for each and any CapacitorKey.
   */
  @Override
  public int get(ICapacitorData capacitor) {
    return (int) (baseValue * scaler.scaleValue(capacitor.getUnscaledValue(this)));
  }

  /**
   * See {@link CapacitorKey#get(ICapacitorData)}, but this method will return the value as a float. Depending on the scaler and capacitor level, this may make a
   * difference.
   */
  @Override
  public float getFloat(ICapacitorData capacitor) {
    return baseValue * scaler.scaleValue(capacitor.getUnscaledValue(this));
  }

  @Override
  public ModObject getOwner() {
    return owner;
  }

  @Override
  public CapacitorKeyType getValueType() {
    return valueType;
  }
  
  @Override
  public String getName() {
    return name().toLowerCase(Locale.ENGLISH);
  }

  public static void processConfig(Configuration config) {
    for (CapacitorKey key : values()) {
      if (key.configSection != null && key.configKey != null && key.configComment != null) {
        key.baseValue = config.get(key.configSection.name, key.configKey, key.defaultBaseValue, key.configComment).getInt(key.baseValue);
      }
    }
  }

}