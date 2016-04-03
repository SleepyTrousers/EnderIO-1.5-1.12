package crazypants.enderio.capacitor;

import crazypants.enderio.ModObject;

public class DefaultCapacitorKey implements ICapacitorKey {

  private final ModObject owner;
  private final CapacitorKeyType valueType;
  private final Scaler scaler;
  private final int baseValue;

  public DefaultCapacitorKey(ModObject owner, CapacitorKeyType valueType, Scaler scaler, int baseValue) {
    this.owner = owner;
    this.valueType = valueType;
    this.scaler = scaler;
    this.baseValue = baseValue;
  }

  @Override
  public int get(ICapacitorData capacitor) {
    return (int) (baseValue * scaler.scaleValue(capacitor.getUnscaledValue(this)));
  }

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
    return owner + ":" + valueType;
  }

}
