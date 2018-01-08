package crazypants.enderio.base.capacitor;

import javax.annotation.Nonnull;

public interface ICapacitorData {

  float getUnscaledValue(@Nonnull ICapacitorKey key);

  @Nonnull
  String getUnlocalizedName();

  @Nonnull
  String getLocalizedName();

}