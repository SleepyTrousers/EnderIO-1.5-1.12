package crazypants.enderio.capacitor;

import java.util.Locale;

import net.minecraftforge.common.config.Configuration;
import crazypants.enderio.EnderIO;
import crazypants.enderio.Log;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config.Section;

public enum CapacitorKey {
  ALLOY_SMELTER_POWER_USE(ModObject.blockAlloySmelter, CapacitorKeyType.ENERGY_USE, Scaler.POWER, 20),
  ALLOY_SMELTER_POWER_IN(ModObject.blockAlloySmelter, CapacitorKeyType.ENERGY_INTAKE, Scaler.POWER, 80),
  ALLOY_SMELTER_POWER_BUFFER(ModObject.blockAlloySmelter, CapacitorKeyType.BUFFER, Scaler.POWER, 100000),

  LEGACY_ENERGY_USE(ModObject.itemBasicCapacitor, CapacitorKeyType.ENERGY_USE, Scaler.POWER, 21),
  LEGACY_ENERGY_INTAKE(ModObject.itemBasicCapacitor, CapacitorKeyType.ENERGY_INTAKE, Scaler.POWER, 81),
  LEGACY_ENERGY_BUFFER(ModObject.itemBasicCapacitor, CapacitorKeyType.BUFFER, Scaler.POWER, 100001),

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
    this(owner, valueType, scaler, null, null, defaultBaseValue);
  }

  private CapacitorKey(ModObject owner, CapacitorKeyType valueType, Scaler scaler, String configKey, Section configSection, int defaultBaseValue) {
    this.owner = owner;
    this.valueType = valueType;
    this.scaler = scaler;
    this.configKey = configKey;
    this.configSection = configSection;
    this.configComment = localizeComment(configKey);
    this.baseValue = this.defaultBaseValue = defaultBaseValue;
  }

  private static String localizeComment(String configKey) {
    if (configKey == null) {
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
  public int get(ICapacitorData capacitor) {
    return (int) (baseValue * scaler.scaleValue(capacitor.getUnscaledValue(this)));
  }

  /**
   * See {@link CapacitorKey#get(ICapacitorData)}, but this method will return the value as a float. Depending on the scaler and capacitor level, this may make a
   * difference.
   */
  public float getFloat(ICapacitorData capacitor) {
    return baseValue * scaler.scaleValue(capacitor.getUnscaledValue(this));
  }

  public ModObject getOwner() {
    return owner;
  }

  public CapacitorKeyType getValueType() {
    return valueType;
  }
  
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