package crazypants.enderio.base.capacitor;

import javax.annotation.Nonnull;

import crazypants.enderio.base.init.IModObject;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface ICapacitorKey extends IForgeRegistryEntry<ICapacitorKey> {

  /**
   * Calculates the value to use for the given capacitor. Calculation is:
   * <p>
   * <tt>final value = base value * scaler(capacitor level)</tt>
   * <p>
   * The capacitor level is a 1, 2 and 3 for the basic capacitors. Custom ones may have any non-zero, positive level. The scalers are expected to only map to
   * halfway reasonable output levels. Capacitors can choose to report different levels for each and any CapacitorKey.
   */
  default int get(float level) {
    return (int) getFloat(level);
  }
  
  default int get(@Nonnull ICapacitorData data) {
    return (int) getFloat(data);
  }

  /**
   * See {@link ICapacitorKey#get(ICapacitorData)}, but this method will return the value as a float. Depending on the scaler and capacitor level, this may make
   * a difference.
   */
  float getFloat(float level);
  
  default float getFloat(@Nonnull ICapacitorData data) {
    return getFloat(data.getUnscaledValue(this));
  }
  
  default int getBaseValue() {
    return get(1);
  }

  @Nonnull
  IModObject getOwner();

  @Nonnull
  CapacitorKeyType getValueType();

  @Nonnull
  @Deprecated // TODO 1.13: Remove
  String getLegacyName();

  @Override
  @Nonnull
  ResourceLocation getRegistryName();

  /*
   * These methods are used by the xml configuration system only. For keys that are not configured that way, make the setters complain and the validate do
   * nothing.
   */

  void setScaler(@Nonnull Scaler scaler);

  void setBaseValue(int baseValue);

  void validate();

}