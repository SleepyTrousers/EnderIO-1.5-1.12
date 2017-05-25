package crazypants.enderio.capacitor;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.EnderIO;
import crazypants.enderio.Log;
import crazypants.enderio.config.Config.Section;
import crazypants.enderio.init.ModObject;
import net.minecraftforge.common.config.Configuration;

import static crazypants.enderio.config.Config.sectionAttractor;
import static crazypants.enderio.config.Config.sectionCapacitor;
import static crazypants.enderio.config.Config.sectionFarm;
import static crazypants.enderio.config.Config.sectionPower;
import static crazypants.enderio.config.Config.sectionSpawner;

public enum CapacitorKey implements ICapacitorKey {
  ALLOY_SMELTER_POWER_INTAKE(ModObject.blockAlloySmelter, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.POWER, 80),
  ALLOY_SMELTER_POWER_BUFFER(ModObject.blockAlloySmelter, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.POWER, 100000),
  ALLOY_SMELTER_POWER_USE(ModObject.blockAlloySmelter, CapacitorKeyType.ENERGY_USE, Scaler.Factory.POWER, 20),

  SAG_MILL_POWER_INTAKE(ModObject.blockSagMill, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.POWER, 80),
  SAG_MILL_POWER_BUFFER(ModObject.blockSagMill, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.POWER, 100000),
  SAG_MILL_POWER_USE(ModObject.blockSagMill, CapacitorKeyType.ENERGY_USE, Scaler.Factory.POWER, 20),

  PAINTER_POWER_INTAKE(ModObject.blockPainter, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.POWER, 80),
  PAINTER_POWER_BUFFER(ModObject.blockPainter, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.POWER, 100000),
  PAINTER_POWER_USE(ModObject.blockPainter, CapacitorKeyType.ENERGY_USE, Scaler.Factory.POWER, 20),

  BUFFER_POWER_INTAKE(ModObject.blockBuffer, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.POWER, 80),
  BUFFER_POWER_BUFFER(ModObject.blockBuffer, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.POWER, 100000),

  CRAFTER_POWER_INTAKE(ModObject.blockCrafter, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.POWER10, 500),
  CRAFTER_POWER_BUFFER(ModObject.blockCrafter, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.POWER, 100000),
  CRAFTER_TICKS(ModObject.blockCrafter, CapacitorKeyType.SPEED, Scaler.Factory.SPEED, 1),

  ATTRACTOR_POWER_INTAKE(ModObject.blockAttractor, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.QUADRATIC, 80),
  ATTRACTOR_POWER_BUFFER(ModObject.blockAttractor, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.POWER, 100000),
  ATTRACTOR_POWER_USE(ModObject.blockAttractor, CapacitorKeyType.ENERGY_USE, Scaler.Factory.QUADRATIC, 20, sectionAttractor, "attractorPowerPerTickLevelOne"),
  ATTRACTOR_RANGE(ModObject.blockAttractor, CapacitorKeyType.AREA, Scaler.Factory.QUADRATIC, 16, sectionAttractor, "attractorRangeLevelOne"),

  AVERSION_POWER_INTAKE(ModObject.blockSpawnGuard, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.CUBIC, 640),
  AVERSION_POWER_BUFFER(ModObject.blockSpawnGuard, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.POWER, 100000),
  AVERSION_POWER_USE(ModObject.blockSpawnGuard, CapacitorKeyType.ENERGY_USE, Scaler.Factory.CUBIC, 80, sectionAttractor, "spawnGuardPowerPerTickLevelOne"),
  AVERSION_RANGE(ModObject.blockSpawnGuard, CapacitorKeyType.AREA, Scaler.Factory.RANGE, 16),

  TRANSCEIVER_POWER_INTAKE(ModObject.blockTransceiver, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.FIXED_1, 20480 * 2, sectionPower, "transceiverMaxIoRF"),
  TRANSCEIVER_POWER_BUFFER(ModObject.blockTransceiver, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.FIXED_1, 500000),
  TRANSCEIVER_POWER_USE(ModObject.blockTransceiver, CapacitorKeyType.ENERGY_USE, Scaler.Factory.FIXED_1, 10, sectionPower, "transceiverUpkeepCostRF"),

  WEATHER_POWER_INTAKE(ModObject.blockWeatherObelisk, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.POWER, 80),
  WEATHER_POWER_BUFFER(ModObject.blockWeatherObelisk, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.POWER, 100000),
  WEATHER_POWER_USE(ModObject.blockWeatherObelisk, CapacitorKeyType.ENERGY_USE, Scaler.Factory.POWER, 20),

  FARM_POWER_INTAKE(ModObject.blockFarmStation, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.RANGE, 400),
  FARM_POWER_BUFFER(ModObject.blockFarmStation, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.RANGE, 250000),
  FARM_POWER_USE(ModObject.blockFarmStation, CapacitorKeyType.ENERGY_USE, Scaler.Factory.RANGE, 10),
  FARM_BASE_SIZE(ModObject.blockFarmStation, CapacitorKeyType.AREA, Scaler.Factory.FIXED_1, 1, sectionFarm, null),
  FARM_BONUS_SIZE(ModObject.blockFarmStation, CapacitorKeyType.AREA, Scaler.Factory.IDENTITY, 2, sectionFarm, "farmBonusSize"),
  FARM_STACK_LIMIT(ModObject.blockFarmStation, CapacitorKeyType.AMOUNT, Scaler.Factory.QUADRATIC, 16),

  SPAWNER_POWER_INTAKE(ModObject.blockPoweredSpawner, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.SPAWNER, 200),
  SPAWNER_POWER_BUFFER(ModObject.blockPoweredSpawner, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.POWER, 100000),
  SPAWNER_POWER_USE(ModObject.blockPoweredSpawner, CapacitorKeyType.ENERGY_USE, Scaler.Factory.SPAWNER, 160, sectionSpawner,
      "poweredSpawnerLevelOnePowerPerTickRF"),
  SPAWNER_SPEEDUP(ModObject.blockPoweredSpawner, CapacitorKeyType.SPEED, Scaler.Factory.QUADRATIC, 1),

  SLICE_POWER_INTAKE(ModObject.blockSliceAndSplice, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.QUADRATIC, 160),
  SLICE_POWER_BUFFER(ModObject.blockSliceAndSplice, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.POWER, 100000),
  SLICE_POWER_USE(ModObject.blockSliceAndSplice, CapacitorKeyType.ENERGY_USE, Scaler.Factory.QUADRATIC, 80, sectionPower,
      "sliceAndSpliceLevelOnePowerPerTickRF"),

  SOUL_BINDER_POWER_INTAKE(ModObject.blockSoulBinder, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.QUADRATIC, 1000),
  SOUL_BINDER_POWER_BUFFER(ModObject.blockSoulBinder, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.POWER, 100000),
  SOUL_BINDER_POWER_USE(ModObject.blockSoulBinder, CapacitorKeyType.ENERGY_USE, Scaler.Factory.QUADRATIC, 500, sectionPower,
      "soulBinderLevelOnePowerPerTickRF"),

  STIRLING_POWER_BUFFER(ModObject.blockStirlingGenerator, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.POWER, 100000),
  STIRLING_POWER_GEN(ModObject.blockStirlingGenerator, CapacitorKeyType.ENERGY_USE, Scaler.Factory.QUADRATIC, 20),
  STIRLING_POWER_TIME(ModObject.blockStirlingGenerator, CapacitorKeyType.SPEED, Scaler.Factory.BURNTIME, 1),

  POWER_MONITOR_POWER_INTAKE(ModObject.blockPowerMonitor, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.POWER, 10),
  POWER_MONITOR_POWER_BUFFER(ModObject.blockPowerMonitor, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.POWER, 10000),
  POWER_MONITOR_POWER_USE(ModObject.blockPowerMonitor, CapacitorKeyType.ENERGY_USE, Scaler.Factory.POWER, 1),

  INV_PANEL_SENSOR_POWER_INTAKE(ModObject.blockInventoryPanelSensor, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.POWER, 10),
  INV_PANEL_SENSOR_POWER_BUFFER(ModObject.blockInventoryPanelSensor, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.POWER, 10000),
  INV_PANEL_SENSOR_POWER_USE(ModObject.blockInventoryPanelSensor, CapacitorKeyType.ENERGY_USE, Scaler.Factory.POWER, 1),

  LEGACY_ENERGY_INTAKE(ModObject.itemBasicCapacitor, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.POWER, 80),
  LEGACY_ENERGY_BUFFER(ModObject.itemBasicCapacitor, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.POWER, 100000),
  LEGACY_ENERGY_USE(ModObject.itemBasicCapacitor, CapacitorKeyType.ENERGY_USE, Scaler.Factory.POWER, 20),

  //
  ;

  // /////////////////////////////////////////////////////////////////// //
  // /////////////////////////////////////////////////////////////////// //
  // /////////////////////////////////////////////////////////////////// //

  private final @Nonnull ModObject owner;
  private final @Nonnull CapacitorKeyType valueType;
  private @Nonnull Scaler scaler;
  private final @Nonnull String configKey;
  private final @Nonnull Section configSection;
  private final @Nonnull String configComment;
  private final int defaultBaseValue;
  private int baseValue;

  private CapacitorKey(@Nonnull ModObject owner, @Nonnull CapacitorKeyType valueType, @Nonnull Scaler scaler, int defaultBaseValue) {
    this(owner, valueType, scaler, defaultBaseValue, sectionCapacitor, null);
  }

  private @Nullable CapacitorKey(@Nonnull ModObject owner, @Nonnull CapacitorKeyType valueType, @Nonnull Scaler scaler, int defaultBaseValue,
      @Nonnull Section configSection, @Nullable String configKey) {
    this.owner = owner;
    this.valueType = valueType;
    this.scaler = scaler;
    this.configKey = configKey == null ? name().toLowerCase(Locale.US) : configKey;
    this.configSection = configSection;
    this.configComment = localizeComment(this.configSection, this.configKey);
    this.baseValue = this.defaultBaseValue = defaultBaseValue;
  }

  private static @Nonnull String localizeComment(@Nonnull Section configSection, @Nonnull String configKey) {
    final String langKey = "config.capacitor." + configKey;
    if (!EnderIO.lang.canLocalize(langKey)) {
      Log.warn("Missing translation: " + langKey);
    }
    return EnderIO.lang.localize(langKey);
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
  public int get(@Nonnull ICapacitorData capacitor) {
    return (int) (baseValue * scaler.scaleValue(capacitor.getUnscaledValue(this)));
  }

  /**
   * See {@link CapacitorKey#get(ICapacitorData)}, but this method will return the value as a float. Depending on the scaler and capacitor level, this may make
   * a difference.
   */
  @Override
  public float getFloat(@Nonnull ICapacitorData capacitor) {
    return baseValue * scaler.scaleValue(capacitor.getUnscaledValue(this));
  }

  @Override
  public @Nonnull ModObject getOwner() {
    return owner;
  }

  @Override
  public @Nonnull CapacitorKeyType getValueType() {
    return valueType;
  }

  @Override
  public @Nonnull String getName() {
    return name().toLowerCase(Locale.ENGLISH);
  }

  public static void processConfig(Configuration config) {
    for (CapacitorKey key : values()) {
      key.baseValue = config.get(key.configSection.name, key.configKey, key.defaultBaseValue, key.configComment).getInt(key.baseValue);
      String string = Scaler.Factory.toString(key.scaler);
      if (string != null) {
        String string2 = config.get(key.configSection.name, key.configKey + ".scaler", string, null).getString();
        Scaler tmp = Scaler.Factory.fromString(string2);
        if (tmp != null) {
          key.scaler = tmp;
        } else {
          config.get(key.configSection.name, key.configKey + ".scaler", string, null).set(string);
        }
      }
    }
  }

}