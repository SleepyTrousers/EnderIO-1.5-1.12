package crazypants.enderio.capacitor;

import javax.annotation.Nonnull;

import crazypants.enderio.IModObject;

public interface ICapacitorKey {

  /**
   * Calculates the value to use for the given capacitor. Calculation is:
   * <p>
   * <tt>final value = base value * scaler(capacitor level)</tt>
   * <p>
   * The capacitor level is a 1, 2 and 3 for the basic capacitors. Custom ones may have any non-zero, positive level. The scalers are expected to only map to
   * halfway reasonable output levels. Capacitors can choose to report different levels for each and any CapacitorKey.
   */
  int get(@Nonnull ICapacitorData capacitor);

  /**
   * See {@link CapacitorKey#get(ICapacitorData)}, but this method will return the value as a float. Depending on the scaler and capacitor level, this may make a
   * difference.
   */
  float getFloat(@Nonnull ICapacitorData capacitor);

  @Nonnull
  IModObject getOwner();

  @Nonnull
  CapacitorKeyType getValueType();

  @Nonnull
  String getName();

}