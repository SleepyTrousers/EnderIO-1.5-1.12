package crazypants.enderio.capacitor;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.config.Config.Section;
import crazypants.enderio.init.ModObject;
import net.minecraftforge.common.config.Configuration;

import static crazypants.enderio.config.Config.sectionCapacitor;

public enum CapacitorKey implements ICapacitorKey.Computable {
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
    this.configComment = CapacitorKeyHelper.localizeComment(this.configSection, this.configKey);
    this.baseValue = this.defaultBaseValue = defaultBaseValue;
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