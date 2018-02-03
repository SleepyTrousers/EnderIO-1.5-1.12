package crazypants.enderio.base.capacitor;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.Config.Section;
import crazypants.enderio.base.init.ModObject;
import net.minecraftforge.common.config.Configuration;

import static crazypants.enderio.base.config.Config.sectionCapacitor;

public enum CapacitorKey implements ICapacitorKey.Computable {
  NO_POWER_INTAKE(ModObject.block_machine_base, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.FIXED_1, 0),

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

  private CapacitorKey(@Nonnull ModObject owner, @Nonnull CapacitorKeyType valueType, @Nonnull Scaler scaler, int defaultBaseValue,
      @Nonnull Section configSection, @Nullable String configKey) {
    this.owner = owner;
    this.valueType = valueType;
    this.scaler = scaler;
    this.configKey = CapacitorKeyHelper.createConfigKey(this, configKey);
    this.configSection = configSection;
    this.configComment = CapacitorKeyHelper.localizeComment(EnderIO.lang, this.configSection, this.configKey);
    this.baseValue = this.defaultBaseValue = defaultBaseValue;
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

}