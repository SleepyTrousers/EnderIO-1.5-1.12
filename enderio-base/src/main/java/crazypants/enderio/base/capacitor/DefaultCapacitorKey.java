package crazypants.enderio.base.capacitor;

import javax.annotation.Nonnull;

import crazypants.enderio.base.init.IModObject;

public class DefaultCapacitorKey implements ICapacitorKey {

  private final @Nonnull IModObject owner;
  private final @Nonnull CapacitorKeyType valueType;
  private final @Nonnull Scaler scaler;
  private final int baseValue;

  public DefaultCapacitorKey(@Nonnull IModObject owner, @Nonnull CapacitorKeyType valueType, @Nonnull Scaler scaler, int baseValue) {
    this.owner = owner;
    this.valueType = valueType;
    this.scaler = scaler;
    this.baseValue = baseValue;
  }

  @Override
  public int get(@Nonnull ICapacitorData capacitor) {
    return (int) (baseValue * scaler.scaleValue(capacitor.getUnscaledValue(this)));
  }

  @Override
  public float getFloat(@Nonnull ICapacitorData capacitor) {
    return baseValue * scaler.scaleValue(capacitor.getUnscaledValue(this));
  }

  @Override
  public @Nonnull IModObject getOwner() {
    return owner;
  }

  @Override
  public @Nonnull CapacitorKeyType getValueType() {
    return valueType;
  }

  @Override
  public @Nonnull String getName() {
    return owner + ":" + valueType;
  }

}
