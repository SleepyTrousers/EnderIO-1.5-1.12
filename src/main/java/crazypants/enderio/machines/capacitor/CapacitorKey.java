package crazypants.enderio.machines.capacitor;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.capacitor.CapacitorHelper.SetType;
import crazypants.enderio.base.capacitor.CapacitorKeyHelper;
import crazypants.enderio.base.capacitor.CapacitorKeyType;
import crazypants.enderio.base.capacitor.ICapacitorKey;
import crazypants.enderio.base.capacitor.Scaler;
import crazypants.enderio.base.config.Config.Section;
import crazypants.enderio.base.loot.WeightedUpgrade;
import crazypants.enderio.machines.EnderIOMachines;
import crazypants.enderio.machines.init.MachineObject;
import net.minecraftforge.common.config.Configuration;

import static crazypants.enderio.machines.config.Config.sectionCapacitor;

public enum CapacitorKey implements ICapacitorKey.Computable {

  SIMPLE_ALLOY_SMELTER_POWER_INTAKE(MachineObject.block_simple_alloy_smelter, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.FIXED_1, 10),
  SIMPLE_ALLOY_SMELTER_POWER_BUFFER(MachineObject.block_simple_alloy_smelter, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.FIXED_1, 1000),
  SIMPLE_ALLOY_SMELTER_POWER_USE(MachineObject.block_simple_alloy_smelter, CapacitorKeyType.ENERGY_USE, Scaler.Factory.FIXED_1, 5),

  ALLOY_SMELTER_POWER_INTAKE(MachineObject.block_alloy_smelter, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.POWER, 80),
  ALLOY_SMELTER_POWER_BUFFER(MachineObject.block_alloy_smelter, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.POWER, 100000),
  ALLOY_SMELTER_POWER_USE(MachineObject.block_alloy_smelter, CapacitorKeyType.ENERGY_USE, Scaler.Factory.POWER, 20),

  CREATIVE_BUFFER_POWER_INTAKE(MachineObject.block_buffer, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.FIXED_1, 100000),
  CREATIVE_BUFFER_POWER_BUFFER(MachineObject.block_buffer, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.FIXED_1, 1000000),
  CREATIVE_BUFFER_POWER_USE(MachineObject.block_buffer, CapacitorKeyType.ENERGY_USE, Scaler.Factory.FIXED_1, 0),

  BUFFER_POWER_INTAKE(MachineObject.block_buffer, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.CUBIC, 2500),
  BUFFER_POWER_BUFFER(MachineObject.block_buffer, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.POWER, 100000),
  BUFFER_POWER_USE(MachineObject.block_buffer, CapacitorKeyType.ENERGY_USE, Scaler.Factory.FIXED_1, 0),

  FARM_POWER_INTAKE(MachineObject.block_farm_station, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.RANGE, 400),
  FARM_POWER_BUFFER(MachineObject.block_farm_station, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.RANGE, 250000),
  FARM_POWER_USE(MachineObject.block_farm_station, CapacitorKeyType.ENERGY_USE, Scaler.Factory.RANGE, 10),
  FARM_BASE_SIZE(MachineObject.block_farm_station, CapacitorKeyType.AREA, Scaler.Factory.FIXED_1, 1),
  FARM_BONUS_SIZE(MachineObject.block_farm_station, CapacitorKeyType.AREA, Scaler.Factory.IDENTITY, 2),
  FARM_STACK_LIMIT(MachineObject.block_farm_station, CapacitorKeyType.AMOUNT, Scaler.Factory.QUADRATIC, 16),

  COMBUSTION_POWER_LOSS(MachineObject.block_combustion_generator, CapacitorKeyType.ENERGY_LOSS, Scaler.Factory.FIXED_1, 0),
  COMBUSTION_POWER_BUFFER(MachineObject.block_combustion_generator, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.POWER, 100000),
  COMBUSTION_POWER_GEN(MachineObject.block_combustion_generator, CapacitorKeyType.ENERGY_GEN, Scaler.Factory.CHEMICAL, 1),

  ENHANCED_COMBUSTION_POWER_LOSS(MachineObject.block_enhanced_combustion_generator, CapacitorKeyType.ENERGY_LOSS, Scaler.Factory.FIXED_1, 0),
  ENHANCED_COMBUSTION_POWER_BUFFER(MachineObject.block_enhanced_combustion_generator, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.POWER, 200000),
  ENHANCED_COMBUSTION_POWER_GEN(MachineObject.block_enhanced_combustion_generator, CapacitorKeyType.ENERGY_GEN, Scaler.Factory.CHEMICAL, 1),

  STIRLING_POWER_LOSS(MachineObject.block_stirling_generator, CapacitorKeyType.ENERGY_LOSS, Scaler.Factory.FIXED_1, 0),
  STIRLING_POWER_BUFFER(MachineObject.block_stirling_generator, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.POWER, 100000),
  STIRLING_POWER_GEN(MachineObject.block_stirling_generator, CapacitorKeyType.ENERGY_GEN, Scaler.Factory.QUADRATIC, 20),
  STIRLING_POWER_TIME(MachineObject.block_stirling_generator, CapacitorKeyType.SPEED, Scaler.Factory.BURNTIME, 1),

  SIMPLE_STIRLING_POWER_LOSS(MachineObject.block_simple_stirling_generator, CapacitorKeyType.ENERGY_LOSS, Scaler.Factory.FIXED_1, 1),
  SIMPLE_STIRLING_POWER_BUFFER(MachineObject.block_simple_stirling_generator, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.FIXED_1, 1000),
  SIMPLE_STIRLING_POWER_GEN(MachineObject.block_simple_stirling_generator, CapacitorKeyType.ENERGY_GEN, Scaler.Factory.FIXED_1, 10),

  ZOMBIE_POWER_LOSS(MachineObject.block_zombie_generator, CapacitorKeyType.ENERGY_LOSS, Scaler.Factory.FIXED_1, 0),
  ZOMBIE_POWER_BUFFER(MachineObject.block_zombie_generator, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.POWER, 100000),
  ZOMBIE_POWER_GEN(MachineObject.block_zombie_generator, CapacitorKeyType.ENERGY_GEN, Scaler.Factory.FIXED_1, 80),

  ATTRACTOR_POWER_INTAKE(MachineObject.block_attractor_obelisk, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.QUADRATIC, 80),
  ATTRACTOR_POWER_BUFFER(MachineObject.block_attractor_obelisk, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.POWER, 100000),
  ATTRACTOR_POWER_USE(MachineObject.block_attractor_obelisk, CapacitorKeyType.ENERGY_USE, Scaler.Factory.QUADRATIC, 20),
  ATTRACTOR_RANGE(MachineObject.block_attractor_obelisk, CapacitorKeyType.AREA, Scaler.Factory.QUADRATIC, 16),

  AVERSION_POWER_INTAKE(MachineObject.block_aversion_obelisk, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.CUBIC, 640),
  AVERSION_POWER_BUFFER(MachineObject.block_aversion_obelisk, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.POWER, 100000),
  AVERSION_POWER_USE(MachineObject.block_aversion_obelisk, CapacitorKeyType.ENERGY_USE, Scaler.Factory.CUBIC, 80),
  AVERSION_RANGE(MachineObject.block_aversion_obelisk, CapacitorKeyType.AREA, Scaler.Factory.RANGE, 16),

  INHIBITOR_POWER_INTAKE(MachineObject.block_aversion_obelisk, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.CUBIC, 80),
  INHIBITOR_POWER_BUFFER(MachineObject.block_aversion_obelisk, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.POWER, 100000),
  INHIBITOR_POWER_USE(MachineObject.block_aversion_obelisk, CapacitorKeyType.ENERGY_USE, Scaler.Factory.CUBIC, 20),
  INHIBITOR_RANGE(MachineObject.block_aversion_obelisk, CapacitorKeyType.AREA, Scaler.Factory.RANGE, 8),

  RELOCATOR_POWER_INTAKE(MachineObject.block_aversion_obelisk, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.CUBIC, 640),
  RELOCATOR_POWER_BUFFER(MachineObject.block_aversion_obelisk, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.POWER, 100000),
  RELOCATOR_POWER_USE(MachineObject.block_aversion_obelisk, CapacitorKeyType.ENERGY_USE, Scaler.Factory.CUBIC, 80),
  RELOCATOR_RANGE(MachineObject.block_aversion_obelisk, CapacitorKeyType.AREA, Scaler.Factory.RANGE, 12),

  WEATHER_POWER_INTAKE(MachineObject.block_weather_obelisk, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.POWER, 80),
  WEATHER_POWER_BUFFER(MachineObject.block_weather_obelisk, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.POWER, 100000),
  WEATHER_POWER_USE(MachineObject.block_weather_obelisk, CapacitorKeyType.ENERGY_USE, Scaler.Factory.FIXED_1, 20),

  PAINTER_POWER_INTAKE(MachineObject.block_painter, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.POWER, 80),
  PAINTER_POWER_BUFFER(MachineObject.block_painter, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.POWER, 100000),
  PAINTER_POWER_USE(MachineObject.block_painter, CapacitorKeyType.ENERGY_USE, Scaler.Factory.POWER, 20),

  SAG_MILL_POWER_INTAKE(MachineObject.block_sag_mill, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.POWER, 80),
  SAG_MILL_POWER_BUFFER(MachineObject.block_sag_mill, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.POWER, 100000),
  SAG_MILL_POWER_USE(MachineObject.block_sag_mill, CapacitorKeyType.ENERGY_USE, Scaler.Factory.POWER, 20),

  SLICE_POWER_INTAKE(MachineObject.block_slice_and_splice, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.QUADRATIC, 160),
  SLICE_POWER_BUFFER(MachineObject.block_slice_and_splice, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.POWER, 100000),
  SLICE_POWER_USE(MachineObject.block_slice_and_splice, CapacitorKeyType.ENERGY_USE, Scaler.Factory.QUADRATIC, 80),

  SOUL_BINDER_POWER_INTAKE(MachineObject.block_soul_binder, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.QUADRATIC, 1000),
  SOUL_BINDER_POWER_BUFFER(MachineObject.block_soul_binder, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.POWER, 100000),
  SOUL_BINDER_POWER_USE(MachineObject.block_soul_binder, CapacitorKeyType.ENERGY_USE, Scaler.Factory.QUADRATIC, 500),
  SOUL_BINDER_SOUND_PITCH(MachineObject.block_soul_binder, CapacitorKeyType.AMOUNT, Scaler.Factory.IDENTITY, 1),

  SPAWNER_POWER_INTAKE(MachineObject.block_powered_spawner, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.SPAWNER, 200),
  SPAWNER_POWER_BUFFER(MachineObject.block_powered_spawner, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.POWER, 100000),
  SPAWNER_POWER_USE(MachineObject.block_powered_spawner, CapacitorKeyType.ENERGY_USE, Scaler.Factory.SPAWNER, 160),
  SPAWNER_SPEEDUP(MachineObject.block_powered_spawner, CapacitorKeyType.SPEED, Scaler.Factory.QUADRATIC, 1),

  TRANSCEIVER_POWER_INTAKE(MachineObject.block_transceiver, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.FIXED_1, 20480 * 2),
  TRANSCEIVER_POWER_BUFFER(MachineObject.block_transceiver, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.FIXED_1, 500000),
  TRANSCEIVER_POWER_USE(MachineObject.block_transceiver, CapacitorKeyType.ENERGY_USE, Scaler.Factory.FIXED_1, 10),

  VAT_POWER_INTAKE(MachineObject.block_vat, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.POWER, 80),
  VAT_POWER_BUFFER(MachineObject.block_vat, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.POWER, 100000),
  VAT_POWER_USE(MachineObject.block_vat, CapacitorKeyType.ENERGY_USE, Scaler.Factory.CHEMICAL, 20),

  WIRELESS_POWER_INTAKE(MachineObject.block_wireless_charger, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.FIXED_1, 10000),
  WIRELESS_POWER_BUFFER(MachineObject.block_wireless_charger, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.FIXED_1, 200000),
  WIRELESS_POWER_OUTPUT(MachineObject.block_wireless_charger, CapacitorKeyType.ENERGY_USE, Scaler.Factory.FIXED_1, 10000),

  //
  ;

  // /////////////////////////////////////////////////////////////////// //
  // /////////////////////////////////////////////////////////////////// //
  // /////////////////////////////////////////////////////////////////// //

  private final @Nonnull MachineObject owner;
  private final @Nonnull CapacitorKeyType valueType;
  private @Nonnull Scaler scaler;
  private final @Nonnull String configKey;
  private final @Nonnull Section configSection;
  private final @Nonnull String configComment;
  private final int defaultBaseValue;
  private int baseValue;

  private CapacitorKey(@Nonnull MachineObject owner, @Nonnull CapacitorKeyType valueType, @Nonnull Scaler scaler, int defaultBaseValue) {
    this(owner, valueType, scaler, defaultBaseValue, sectionCapacitor, null);
  }

  private CapacitorKey(@Nonnull MachineObject owner, @Nonnull CapacitorKeyType valueType, @Nonnull Scaler scaler, int defaultBaseValue,
      @Nonnull Section configSection, @Nullable String configKey) {
    this.owner = owner;
    this.valueType = valueType;
    this.scaler = scaler;
    this.configKey = CapacitorKeyHelper.createConfigKey(this, configKey);
    this.configSection = configSection;
    this.configComment = CapacitorKeyHelper.localizeComment(EnderIOMachines.lang, this.configSection, this.configKey);
    this.baseValue = this.defaultBaseValue = defaultBaseValue;
  }

  @Override
  public @Nonnull MachineObject getOwner() {
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

  @Override
  public @Nonnull Scaler getScaler() {
    return scaler;
  }

  @Override
  public void setScaler(@Nonnull Scaler scaler) {
    this.scaler = scaler;
  }

  @Override
  public @Nonnull String getConfigKey() {
    return configKey;
  }

  @Override
  public @Nonnull Section getConfigSection() {
    return configSection;
  }

  @Override
  public @Nonnull String getConfigComment() {
    return configComment;
  }

  @Override
  public int getDefaultBaseValue() {
    return defaultBaseValue;
  }

  @Override
  public int getBaseValue() {
    return baseValue;
  }

  @Override
  public void setBaseValue(int baseValue) {
    this.baseValue = baseValue;
  }

  public static void processConfig(Configuration config) {
    CapacitorKeyHelper.processConfig(config, values());
  }

  static {
    WeightedUpgrade.registerWeightedUpgrade(SetType.NAME, ALLOY_SMELTER_POWER_USE, "smelting", 10);
    WeightedUpgrade.registerWeightedUpgrade(SetType.TYPE, ATTRACTOR_RANGE, "area", 5);
    WeightedUpgrade.registerWeightedUpgrade(SetType.NAME, FARM_BONUS_SIZE, "green", 10);
    WeightedUpgrade.registerWeightedUpgrade(SetType.NAME, STIRLING_POWER_GEN, "red", 10);
    WeightedUpgrade.registerWeightedUpgrade(SetType.NAME, SPAWNER_SPEEDUP, "mobby", 5);
    WeightedUpgrade.registerWeightedUpgrade(SetType.NAME, SAG_MILL_POWER_USE, "crushed", 15);
    WeightedUpgrade.registerWeightedUpgrade(SetType.NAME, SLICE_POWER_USE, "cleancut", 5);
    WeightedUpgrade.registerWeightedUpgrade(SetType.NAME, SOUL_BINDER_POWER_USE, "tight", 5);
    WeightedUpgrade.registerWeightedUpgrade(SetType.NAME, PAINTER_POWER_USE, "aa", 10);
    WeightedUpgrade.registerWeightedUpgrade(SetType.NAME, VAT_POWER_USE, "wet", 8);
    WeightedUpgrade.registerWeightedUpgrade(SetType.NAME, COMBUSTION_POWER_GEN, "kaboom", 20);
    WeightedUpgrade.registerWeightedUpgrade(SetType.NAME, ENHANCED_COMBUSTION_POWER_GEN, "fatman", 10);
  }

}