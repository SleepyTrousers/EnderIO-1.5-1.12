package crazypants.enderio.powertools.capacitor;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.capacitor.CapacitorKeyHelper;
import crazypants.enderio.base.capacitor.CapacitorKeyType;
import crazypants.enderio.base.capacitor.ICapacitorKey;
import crazypants.enderio.base.capacitor.Scaler;
import crazypants.enderio.base.config.Config.Section;
import crazypants.enderio.powertools.EnderIOPowerTools;
import crazypants.enderio.powertools.config.Config;
import crazypants.enderio.powertools.init.PowerToolObject;
import net.minecraftforge.common.config.Configuration;

public enum CapacitorKey implements ICapacitorKey.Computable {

  POWER_MONITOR_POWER_INTAKE(PowerToolObject.block_power_monitor, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.FIXED_1, 10),
  POWER_MONITOR_POWER_BUFFER(PowerToolObject.block_power_monitor, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.FIXED_1, 1000),
  POWER_MONITOR_POWER_USE(PowerToolObject.block_power_monitor, CapacitorKeyType.ENERGY_USE, Scaler.Factory.FIXED_1, 5),

  //
  ;

  // /////////////////////////////////////////////////////////////////// //
  // /////////////////////////////////////////////////////////////////// //
  // /////////////////////////////////////////////////////////////////// //

  private final @Nonnull PowerToolObject owner;
  private final @Nonnull CapacitorKeyType valueType;
  private @Nonnull Scaler scaler;
  private final @Nonnull String configKey;
  private final @Nonnull Section configSection;
  private final @Nonnull String configComment;
  private final int defaultBaseValue;
  private int baseValue;

  private CapacitorKey(@Nonnull PowerToolObject owner, @Nonnull CapacitorKeyType valueType, @Nonnull Scaler scaler, int defaultBaseValue) {
    this(owner, valueType, scaler, defaultBaseValue, Config.sectionCapacitor, null);
  }

  private CapacitorKey(@Nonnull PowerToolObject owner, @Nonnull CapacitorKeyType valueType, @Nonnull Scaler scaler, int defaultBaseValue,
      @Nonnull Section configSection, @Nullable String configKey) {
    this.owner = owner;
    this.valueType = valueType;
    this.scaler = scaler;
    this.configKey = CapacitorKeyHelper.createConfigKey(this, configKey);
    this.configSection = configSection;
    this.configComment = CapacitorKeyHelper.localizeComment(EnderIOPowerTools.lang, this.configSection, this.configKey);
    this.baseValue = this.defaultBaseValue = defaultBaseValue;
  }

  @Override
  public @Nonnull PowerToolObject getOwner() {
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